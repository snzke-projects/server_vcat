package com.vcat.api.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.vcat.common.lock.DistLockHelper;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ShopProduct;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CartDao;
import com.vcat.module.ec.dto.CartDto;
import com.vcat.module.ec.entity.Cart;
import com.vcat.module.ec.entity.ProductItem;
@Service
public class CartService extends CrudService<Cart> {

	@Autowired
	private CartDao cartDao;
	@Autowired
	private ProductItemService productItemService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ShopProductService shopProductService;
	@Override
	protected CrudDao<Cart> getDao() {
		return cartDao;
	}
	//获取购物车列表
	public List<CartDto> getCartList(String buyerId) {
		//查询普通的购物车
		List<CartDto> list = cartDao.getCartList(buyerId,ApiConstants.NORMAL);
		//查询店铺下的购物车
		if(list!=null&&!list.isEmpty()){
			for(CartDto cart:list){
				cart.setCartList(cartDao.findcartListByShop2(
                        buyerId,            // 买家id
						cart.getShopId(),   // 店铺id
                        ApiConstants.NORMAL // 商品类型
                ));
			}
		}
		//查询拿样的购物车
		CartDto scart = new CartDto();
		scart.setShopId("");
		scart.setShopName(ApiConstants.SUPER_NAME);
		scart.setProductType(ApiConstants.SUPER);
		List<Cart> cartList = cartDao.findcartListByShop2(buyerId,scart.getShopId(),ApiConstants.SUPER);

		scart.setCartList(cartList);
		if(cartList.size()>0 && list != null){
			list.add(scart);
		}
		return list;
	}
	//删除购物车
	@Transactional(readOnly = false)
	public void deleteCarts(List<String> list) {
		for(String cartId:list){
			Cart  cart = new Cart();
			cart.setId(cartId);
			cartDao.deleteCarts(cart);
		}
	}
	//添加购物车
	public void saveCarts(String buyerId, List<Cart> carts) {
		for(Cart cart:carts){
			cart.setCustomerId(buyerId);
			//正常状态
			String cartStatus = "0";
			//普通商品
			if(cart.getProductType()==null||ApiConstants.NORMAL.equals(cart.getProductType())){
				String shopId = cart.getShopId();
				String productItemId = cart.getProductItemId();

				//查询当前商品数量是否库存不足
				ProductItem item = productItemService.getItem(productItemId, shopId);
				if(item==null){
					//商品处于下架状态
					cartStatus = "1";
				}
				//查询此商品是否下架
				if(item!=null&&"1".equals(item.getIsSellerLoad())){
					//商品处于下架状态
					cartStatus = "1";
				}

				//如果是预售商品
				if(productService.isReserveProduct(productItemId)){
					//修改库存为小店中的库存
					//获取小店中的库存
					int inventory = shopProductService.getInventory(shopId,productItemService.get(productItemId).getProduct().getId());
					if (item != null) {
						item.setInventory(inventory);
					}
				}
				if(item!=null&&item.getInventory()<cart.getQuantity()){
					cartStatus = "2";
				}
			}
			//拿样商品
			else if(ApiConstants.SUPER.equals(cart.getProductType()) ){
				String productItemId = cart.getProductItemId();
				ProductItem item = productItemService.get(productItemId);
				//查询当前商品数量是否库存不足
				if(item==null){
					cartStatus = "2";
				}
				if(item!=null&&item.getInventory()<cart.getQuantity()){
					cartStatus = "2";
				}
			}
			// 不是预售的商品,预售商品 type = 5, v猫庄园 type = 6
			else if( ApiConstants.RESERVE.equals(cart.getProductType()) || ApiConstants.LEROY.equals(cart.getProductType())){
				String productItemId = cart.getProductItemId();
				if( !productService.isReserveProduct(productItemId)){ //如果不是预售商品
					cart.setProductType(ApiConstants.SUPER);
				}else{
					return;
				}
				// 如果不是预售商品也不是虚拟商品
				//String productItemId = cart.getProductItemId();
				//// 如果是类型为5的拿样商品
				//if(!productService.isVirtualProduct(productItemId) && !productService.isReserveProduct(productItemId)){
				//    //cart.setProductType(ApiConstants.SUPER);
				//    ProductItem item = productItemService.get(productItemId);
				//    //查询当前商品数量是否库存不足
				//    if(item==null){
				//        cartStatus = "2";
				//    }
				//    if(item!=null&&item.getInventory()<cart.getQuantity()){
				//        cartStatus = "2";
				//    }
				//}else
			}

			if(cart.getProductType()==null){
				cart.setProductType(ApiConstants.NORMAL);
			}
			RLock lock  = DistLockHelper.getLock("saveCartLock");
			try {
				lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
				//先查询购物车是否存在，如果存在 更新数量
				Cart ca = cartDao.get(cart);
				if(ca!=null){
					ca.setQuantity(ca.getQuantity()+cart.getQuantity());
					cartDao.update(ca);
				}else{
					cart.preInsert();
					//正常状态
					cart.setCartStatus(cartStatus);
					//将购物车商品列表添加到数据库
					//特约小店店主价为 零售价-销售分红
					//钻石小店店主价为 零售价-销售分红-一级分红
					cartDao.insert2(cart);
				}
			} finally {
				if (lock.isLocked())
					lock.unlock();
			}
		}
	}
	public Integer countByBuyerId(String buyerId) {

		return cartDao.countByBuyerId(buyerId);
	}
	@Transactional(readOnly = false)
	public void updateQuantity(JSONArray list) {
		for (Object object : list) {
			JSONObject json = (JSONObject) object;
			Cart cart = JSON.toJavaObject(json,Cart.class);
			cartDao.update(cart);
		}

	}

}
