package com.vcat.api.service;


import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.utils.StringUtils;
import com.vcat.config.CacheConfig;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.CopywriteDto;
import com.vcat.module.ec.dto.GroupBuyProductDto;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tencent.common.MD5;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ProductDao;
import com.vcat.module.ec.dao.ProductItemDao;
import com.vcat.module.ec.dto.ProductDto;

@Service
@Transactional(readOnly = true)
public class ProductService extends CrudService<Product> {
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ShopProductService shopProductService;
	@Autowired
	private ProductItemDao productItemDao;
	@Autowired
	private ProductImageService productImageService;
	@Autowired
	private ProductPropertyService productPropertyService;
	@Autowired
	private ProductItemService productItemService;
	@Autowired
	private CopywriteDefaultService copywriteDefaultService;
	@Autowired
	private CopywriteService copywriteService;
	@Autowired
	private ShopService shopService;
	@Override
	protected CrudDao<Product> getDao() {
		return productDao;
	}
	public int  countSellerProductList(String productType, String customerId,String categoryId) {
		return productDao.countSellerProductList(productType,customerId,categoryId);
	}
	
	public Product getSellerProductDetail(String shopId, String productId, String couponType) {
		Product product = productDao.getSellerProductDetail(shopId,productId,couponType);
		if(product!=null){
			product.setImageList(productImageService.findListByProductId(product.getId()));
			product.setPropertyList(productPropertyService.findListByProduct(product));
		}
		return product;
	}

	public List<ProductDto> getSellerProductList(String shopId, String productType, Pager page, String categoryId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("customerId", shopId);
		map.put("productType", productType);
		map.put("categoryId", categoryId);
		map.put("page", page);
		List<ProductDto> list =  productDao.getSellerProductList(map);
		// 根据productId获取文案列表,只取第一条数据
		Pager pager = new Pager();
		pager.setPageOffset(0);
		pager.setPageSize(1);
		for(ProductDto productDto : list){
			productDto.getCopywriteList().addAll(copywriteService.getCopywrites(shopId,productDto.getId(),pager,ApiConstants.SORT_MYSHOP));
		}
		return list;
	}
	//更新收藏数量
	@Transactional(readOnly = false)
	public void updateCollectCount(String productId, int count) {
	
		productDao.updateCollectCount(productId, count);
	}
	public void updateSales(String productId, int quantity) {
		productDao.updateSales(productId, quantity);
	}
	//获取拿样的商品数量
	public int countCategoryProductList(String productType, String categoryId,
			String childCategoryId, String brandId) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("productType", productType);
		map.put("categoryId", categoryId);
		map.put("brandId", brandId);
		map.put("childCategoryId", childCategoryId);
		return productDao.countCategoryProductList(map);
	}
	//获取拿样的商品列表
	@Cacheable(value = CacheConfig.GET_CATEGORY_PRODUCT_LIST_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public List<ProductDto> getCategoryProductList(String productType,
			 String categoryId, String childCategoryId, String brandId, String sortType, Pager page) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("childCategoryId", childCategoryId);
		map.put("productType", productType);
		map.put("categoryId", categoryId);
		map.put("brandId", brandId);
		map.put("sortType", sortType);
		map.put("page", page);
		return productDao.getCategoryProductList(map);
	}
	//根据商品id获取商品的信息
	public ProductDto getProduct(String productId) {
		
		return productDao.getProduct(productId);
	}
	public Boolean isVirtualProduct(String productItemId){
		return productDao.isVirtualProduct(productItemId);
	}
	public int countShopProductList(String productType, String customerId,
			String loadType, String categoryId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerId", customerId);
		map.put("productType", productType);
		map.put("categoryId", categoryId);
		map.put("loadType", loadType);
		return productDao.countShopProductList(map);
	}
	public int countShopProductList2(String productType, String customerId,
									 String loadType, String condition){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerId", customerId);
		map.put("productType", productType);
		map.put("loadType", loadType);
		map.put("condition", condition);
		return productDao.countShopProductList2(map);
	}

	public List<ProductDto> getShopProductList(String productType,
			String customerId, String loadType, String sortType, Pager page,
			String categoryId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerId", customerId);
		map.put("productType", productType);
		map.put("loadType", loadType);
		map.put("categoryId", categoryId);
		map.put("sortType", sortType);
		map.put("page", page);
		return productDao.getShopProductList(map);
	}

	public List<ProductDto> getShopProductList2(String productType,
												String customerId, String loadType, String sortType, Pager page,
												String categoryId, String condition){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerId", customerId);
		map.put("productType", productType);
		map.put("loadType", loadType);
		map.put("categoryId", categoryId);
		map.put("sortType", sortType);
		map.put("page", page);
		map.put("condition", condition);
		List<ProductDto> list = productDao.getShopProductList2(map);
		// 根据productId获取文案列表,只取第一条数据
		Pager pager = new Pager();
		pager.setPageOffset(0);
		pager.setPageSize(1);
		for(ProductDto productDto : list){
			productDto.getCopywriteList().addAll(copywriteService.getCopywrites(customerId,productDto.getId(),pager,ApiConstants.SORT_MYSHOP));
		}
		return list;
	}
	//获取v猫商城拿样详情
	public Product getVcatProductDetail(String productId, String buyerId,String couponType) {
		Product product = productDao.getVcatProductDetail(productId,buyerId,couponType);
		if(product!=null){
			product.setImageList(productImageService.findListByProductId(product.getId()));
			product.setPropertyList(productPropertyService.findListByProduct(product));
		}
		return product;
	}

	/**
	 * 下架小店商品
	 * @param productId
	 * @param shopId
	 */
	@Transactional(readOnly = false)
	public void archivedShopProduct(String productId, String shopId) {
		productDao.archivedShopProduct(productId, shopId);
	}

	/**
	 * 获取商品规格集合
	 * @param productId
	 * @param type 1表示普通商品项 2表示拿样商品项 3表示全额抵扣猫币商品项4表示部分抵扣猫币商品项
	 * @return
	 */
	public Map<String, Object> getProductItemList(String productId,String type) {
		List<Map<String, Object>> list = productItemDao.getProductItemList(productId,type);
		Map<String, Object> result = new LinkedHashMap<>();
		if(null != list && !list.isEmpty()){
			for(Map<String,Object> map : list){
				String key = map.get("key") + "";
				map.remove("key");
				key = encodeString(key);
				key = key.replaceAll(",","");
				result.put(key,map);
			}
		}
		return result;
	}

	/**
	 * 获取商品规格标题集合
	 * @param productId
	 * @return
	 */
	public List<Map<String, Object>>  getProductItemTitleList(String productId) {
		List<Map<String, Object>> list = productItemDao.getProductItemTitleList(productId);
		if(null != list && !list.isEmpty()){
			for(Map<String,Object> map : list){
				map.put("title", encodeString(map.get("title") + ""));
			}
		}
		return list;
	}

	/**
	 * MD5加密规格值
	 * @param titleStr
	 * @return
	 */
	public String encodeString(String titleStr){
		String[] titleArray = titleStr.split(",");
		StringBuffer tempTitle = new StringBuffer();
		for (int i = 0; i < titleArray.length; i++) {
			String title = titleArray[i];
			if(i > 0){
				tempTitle.append(",");
			}
			tempTitle.append(MD5.MD5Encode(title));
		}
		return tempTitle.toString();
	}
	//增加上架人数
	@Transactional(readOnly = false)
	public void updateShelves(String productId) {
		productDao.updateShelves(productId);
	}
	//获取购物卷专场商品数量
	//public int countCouponProductList(String customerId, String couponType){
	//
	//	return productDao.countCouponProductList(customerId,couponType);
	//}
	//获取购物卷专场商品列表
	//public List<ProductDto> getCouponProductList(String customerId,
	//		String couponType, String sortType, Pager page) {
	//	return productDao.getCouponProductList(customerId,couponType,sortType,page);
	//}
	//public List<ProductDto> getCouponProductList2(String customerId, Pager page) {
	//	return productDao.getCouponProductList2(customerId,page);
	//}
	//v猫商场推荐数量
	public int countRecommendProductList() {
		return productDao.countRecommendProductList();
	}
	//v猫商场推荐列表
	public List<ProductDto> getRecommendProductList(Pager page) {
		return productDao.getRecommendProductList(page);
	}
	public int countNewProductList() {
		return productDao.countNewProductList();
	}

	@Cacheable(value = CacheConfig.GET_NEW_PRODUCT_LIST_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public List<ProductDto> getNewProductList(Pager page) {
		return productDao.getNewProductList(page);
	}
	public int countReserveProductList() {
		return productDao.countReserveProductList();
	}
	//V猫庄园数量
	public int countLeroyProductList(){
        return productDao.countLeroyProductList();
	}

	public List<ProductDto> getLeroyProductList(String customerId,Pager page){
		List<ProductDto> list = productDao.getLeroyProductList(page);;
		// 根据productId获取文案列表,只取第一条数据
		Pager pager = new Pager();
		pager.setPageOffset(0);
		pager.setPageSize(1);
		for(ProductDto productDto : list){
			// 将默认的(最新的)文案添加到list中
			productDto.getCopywriteList().addAll(copywriteService.getCopywrites(customerId,productDto.getId(),pager,ApiConstants.SORT_LEROY));
		}
        return list;
	}
	@Cacheable(value = CacheConfig.GET_RESERVE_PRODUCT_LIST_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public List<ProductDto> getReserveProductList(Pager page) {
		return productDao.getReserveProductList(page);
	}
    public String getReserveImage(){
        return productDao.getReserveImage();
    }
	// 操作商品
	@Transactional(readOnly = false)
	public void operateProduct(String customerId, String productId,
			String operationType) {
		// 组装对象
		ShopProduct sp = new ShopProduct();
		Shop shop = new Shop();
		shop.setId(customerId);
		Product product = new Product();
		product.setId(productId);
		sp.setShop(shop);
		sp.setProduct(product);
		// 对商品的操作
		switch (operationType) {
		// 入库
		case "add": {
			// 如果商品已经入库,更新商品信息为上架
			sp.setDelFlag(ApiConstants.YES);
			ShopProduct shopProduct = shopProductService.get(sp);
			if (shopProduct != null) {
				// 重新激活
				sp.setDelFlag(ApiConstants.NO);
				shopProductService.delete(shopProduct);
				break;
			}
			sp.preInsert();
			// 入库默认下架
			sp.setArchived(1);
			shopProductService.insert(sp);
			break;
		}
		// 出库，包括下架商品
		case "del": {
			sp.setDelFlag(ApiConstants.YES);
			shopProductService.delete(sp);
			break;
		}
		// 上架商品
		case "up": {
			// 如果商品已经入库,更新商品信息为上架
			ShopProduct shopProduct = shopProductService.get(sp);
			if (shopProduct != null && shopProduct.getArchived()!=0) { //1为未上架
				// 需要拿样，且已经拿样成功
				sp.setArchived(0);
				shopProductService.upOrDownProduct(sp);
				productDao.updateShelves(productId);
			} else if(shopProduct == null) {
				sp.preInsert();
				// 设置上架
				sp.setArchived(0);
                //Code.Ai(店主上架预购商品_完成) 如果是预售商品,设置库存为0
                 if(productDao.isReserveType(productId)){
                     sp.setInventory(0);
                 }
				shopProductService.insert(sp);
				productDao.updateShelves(productId);
			}
			break;
		}
		// 下架商品
		case "down": {
			sp.setArchived(1);
			shopProductService.upOrDownProduct(sp);
			break;
		}
		default:
			break;
		}
	}

	//设置推荐的商品
	@Transactional(readOnly = false)
	public Object setShopProductRecommend(String productId,String shopId,String type){
        int countRecommendShopProduct = productDao.countRecommendShopProduct(shopId);
		if(type.equals(ApiConstants.ADD) && countRecommendShopProduct >= 4){
            return new MsgEntity(ApiMsgConstants.RECOMMEND_FULL_MSG, ApiMsgConstants.RECOMMEND_FULL_CODE);
		}
        productDao.setShopProductRecommendByType(productId,shopId,type);
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,ApiMsgConstants.SUCCESS_CODE);
	}

	/**
	 * 设置默认文案
	 * @param productId
	 * @param shopId
	 * @param type
	 * @return
	 */
	@Transactional(readOnly = false)
	public Object setShopProductDefaultCopywrite(String productId,String copywriteId,String shopId,String type){
		Product product = new Product();
		product.setId(productId);
		Shop shop = new Shop();
		shop.setId(shopId);
		CopywriteDefault copywriteDefault = new CopywriteDefault();
		copywriteDefault.setShop(shop);
		copywriteDefault.setProduct(product);
		CopywriteDefault cwd = copywriteDefaultService.get(copywriteDefault);
		if(type.equals(ApiConstants.CANCEL)){
			if(cwd == null){
				return new MsgEntity(ApiMsgConstants.FAILED_MSG,ApiMsgConstants.FAILED_CODE);
			}else{
				copywriteDefaultService.delete(cwd);
			}
		}
		else if(type.equals(ApiConstants.ADD)){
			Copywrite copywrite = new Copywrite();
			copywrite.setId(copywriteId);
			copywriteDefault.setCopywrite(copywrite);
			if(cwd == null){
				copywriteDefaultService.insert(copywriteDefault);
			}else{
				copywriteDefaultService.update(copywriteDefault);
			}
		}
		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,ApiMsgConstants.SUCCESS_CODE);
	}

	public boolean isReserveProduct(String productItemId){
		return productDao.isReserveProduct(productItemId);
	}

    public Map<String, Object> getProductsByPaymentId(String paymentId){
        return productDao.getProductsByPaymentId(paymentId);
    }

	/**
	 * 获取商城团购商品数量
	 * @return
     */
	public int countGroupBuyProductList(String shopId,String from){
		Map<String, Object> requestMap = new HashMap<>();
		if(!StringUtils.isBlank(from) && from.equals("app")){
			requestMap.put("shopId", "");
		}else
			requestMap.put("shopId", shopId);
		return productDao.countGroupBuyProductList(requestMap);
	}

	/**
	 * 获取商城团购商品列表
	 * @param
	 * @return
     */
	public List<GroupBuyProductDto> getGroupBuyProductList(String shopId,Pager page, String from){
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("page", page);
		if(!StringUtils.isBlank(from) && from.equals("app")){
			requestMap.put("shopId", "");
		}else
			requestMap.put("shopId", shopId);
		Shop shop = shopService.get(shopId);
		List<GroupBuyProductDto> list = productDao.getGroupBuyProductList(requestMap);
		for (GroupBuyProductDto aList : list) {
			List<Map<String, Object>> itemInfo         = this.getGroupBuyProductItemInfo(aList.getGroupBuyId());
			Map<String, Object>       result           = this.refactorPrice(itemInfo, ApiConstants.SELLER_LAUNCH, shop);
			Map                       groupBuyItemInfo = (Map) result.get("groupBuyItemInfo");
			Map                       normalItemInfo   = (Map) result.get("normalItemInfo");
			// 列表显示原始价格
			aList.setGroupPrice((BigDecimal) (groupBuyItemInfo.get("vcatGroupBuyPrice")));
			aList.setSinglePrice((BigDecimal) (normalItemInfo.get("vcatNormalPrice")));
			// 根据不同身份显示不同的返佣 返佣 = 单个返佣 * 数量
			aList.setEarning(((BigDecimal) groupBuyItemInfo.get("earning")).multiply(new BigDecimal(aList.getHeadCount() - 1)));
		}
		// 根据productId获取文案列表,只取第一条数据
		Pager pager = new Pager();
		pager.setPageOffset(0);
		pager.setPageSize(1);
		for(GroupBuyProductDto productDto : list){
			// 获取此商品的文案列表,只取第一条
			productDto.getCopywriteList().addAll(copywriteService.getCopywrites(productDto.getProductId(),pager));
		}
		return list;
	}

	///**
	// * 获取小店(H5)团购商品数量
	// * @return
	// */
	//public int countSellerGroupBuyProductList(String shopId){
	//	return productDao.countSellerGroupBuyProductList(shopId);
	//}
    //
	///**
	// * 获取小店(H5)团购商品列表
	// * @param
	// * @return
	// */
	//public List<GroupBuyProductDto> getSellerGroupBuyProductList(String shopId,Pager page){
	//	Map<String, Object> requestMap = new HashMap<>();
	//	requestMap.put("page", page);
	//	requestMap.put("shopId", shopId);
	//	List<GroupBuyProductDto> list = productDao.getSellerGroupBuyProductList(requestMap);
	//	// 根据productId获取文案列表,只取第一条数据
	//	Pager pager = new Pager();
	//	pager.setPageOffset(0);
	//	pager.setPageSize(1);
	//	for(GroupBuyProductDto productDto : list){
	//		// 获取此商品的文案列表,只取第一条
	//		productDto.getCopywriteList().addAll(copywriteService.getCopywrites(productDto.getProductId(),pager));
	//	}
	//	return list;
	//}



	/**
	 * 获取团购商品详情
	 * @return
     */
	public Product getGroupBuyProductDetail(String buyerId,String groupBuyId,int type){
		Product product = productDao.getGroupBuyProductDetail(buyerId,groupBuyId,type);
		if(product!=null){
			// 商品图片
			product.setImageList(productImageService.findListByProductId(product.getId()));
			// 商品属性
			product.setPropertyList(productPropertyService.findListByProduct(product));
		}
		return product;
	}

	/**
	 * 获取团购商品项规格
	 * @param groupBuyId
	 * @return
     */
	public List<Map<String, Object>> getGroupBuyProductItemInfo(String groupBuyId) {
		return productItemDao.getGroupBuyProductItemInfo(groupBuyId);
	}

	// 获取更多拼团商品列表
	public List<GroupBuyProductDto> getMoreGroupBuyProductList(String shopId, String from){
		Pager page = new Pager();
		page.setPageNo(1);
		page.setPageSize(12);
		page.setRowCount(this.countGroupBuyProductList(shopId,from));
		page.doPage();
		return this.getGroupBuyProductList(shopId,page,from);
	}

	// 重构价格 卖家根据是否为VIP区分价格;买家价格为原来的价格
	public Map<String, Object> refactorPrice(List<Map<String, Object>> itemInfoList, int type, Shop shop){
		Map<String, Object> resultMap = new HashMap<>();
		Map<String, Object> groupBuyItemInfo = new HashMap<>();
		Map<String, Object> normalItemInfo = new HashMap<>();
		for(Map<String, Object> itemInfo : itemInfoList){
			BigDecimal earning = BigDecimal.ZERO;
			if((Long)itemInfo.get("isGroupBuyProduct") == 1){             // 团购规格
				BigDecimal shopGroupBuyPrice = BigDecimal.ZERO;
				if(type == ApiConstants.SELLER_LAUNCH && shop.getAdvancedShop() == 1) { // 卖家查看且卖家是VIP
					shopGroupBuyPrice = ((BigDecimal) itemInfo.get("groupPrice")).subtract((BigDecimal) itemInfo.get("saleEarning")).subtract(
							(BigDecimal) itemInfo.get("bonusEarning"));
					groupBuyItemInfo.put("type",8); // 下单类型
					earning = ((BigDecimal) itemInfo.get("saleEarning")).add((BigDecimal) itemInfo.get("bonusEarning"));
				}else if(type == ApiConstants.SELLER_LAUNCH && shop.getAdvancedShop() != 1){
					shopGroupBuyPrice = ((BigDecimal) itemInfo.get("groupPrice")).subtract((BigDecimal) itemInfo.get("saleEarning"));
					groupBuyItemInfo.put("type",8); // 下单类型
					earning = ((BigDecimal) itemInfo.get("saleEarning"));
				}else if(type == ApiConstants.BUYER_LAUNCH ){        // 买家开团/参团显示的团购价位原来的团购价
					shopGroupBuyPrice = ((BigDecimal) itemInfo.get("groupPrice"));
					groupBuyItemInfo.put("type",9); // 下单类型
				}
				groupBuyItemInfo.put("earning",earning);
				groupBuyItemInfo.put("shopGroupBuyPrice",shopGroupBuyPrice); // 小店团购价
				groupBuyItemInfo.put("vcatGroupBuyPrice",itemInfo.get("groupPrice")); // 市场团购价
				groupBuyItemInfo.put("mainUrl", QCloudUtils.createThumbDownloadUrl((String)itemInfo.get("mainUrl"), ApiConstants.DEFAULT_PRODUCT_THUMBSTYLE));
				groupBuyItemInfo.put("inventory",itemInfo.get("inventory")); // 库存
				groupBuyItemInfo.put("name",itemInfo.get("itemName")); // 规格名
				groupBuyItemInfo.put("name",itemInfo.get("name")); // 规格名
				groupBuyItemInfo.put("value",itemInfo.get("value")); // 规格名
				groupBuyItemInfo.put("productItemId",itemInfo.get("productItemId")); // 规格Id
				groupBuyItemInfo.put("limitCount",itemInfo.get("limitCount") == null ? 0 : itemInfo.get("limitCount")); // 限购数量
				groupBuyItemInfo.put("headCount",itemInfo.get("headCount")); // 限购数量
			}else {                               // 普通规格
				BigDecimal shopNormalPrice = BigDecimal.ZERO;
				if(type == ApiConstants.SELLER_LAUNCH && shop.getAdvancedShop() == 1) { // 卖家查看且卖家是VIP
					shopNormalPrice = ((BigDecimal) itemInfo.get("singlePrice")).subtract((BigDecimal) itemInfo.get("saleEarning")).subtract(
							(BigDecimal) itemInfo.get("bonusEarning"));
					normalItemInfo.put("type",2); // 下单类型
					earning = ((BigDecimal) itemInfo.get("saleEarning")).add((BigDecimal) itemInfo.get("bonusEarning"));
				}else if(type == ApiConstants.SELLER_LAUNCH && shop.getAdvancedShop() != 1){
					shopNormalPrice = ((BigDecimal) itemInfo.get("singlePrice")).subtract((BigDecimal) itemInfo.get("saleEarning"));
					normalItemInfo.put("type",2); // 下单类型
					earning = ((BigDecimal) normalItemInfo.get("saleEarning"));
				}else if(type == ApiConstants.BUYER_LAUNCH ){        // 买家开团/参团显示的普通价位原来的普通价
					shopNormalPrice = ((BigDecimal) itemInfo.get("singlePrice"));
					normalItemInfo.put("type",1); // 下单类型
				}
				normalItemInfo.put("earning",earning);
				normalItemInfo.put("shopNormalPrice",shopNormalPrice); // 普通规格小店单人价格
				normalItemInfo.put("vcatNormalPrice",itemInfo.get("singlePrice")); // 普通规格特约小店单人价格
				normalItemInfo.put("mainUrl",QCloudUtils.createThumbDownloadUrl((String)itemInfo.get("mainUrl"), ApiConstants.DEFAULT_PRODUCT_THUMBSTYLE));
				normalItemInfo.put("inventory",itemInfo.get("inventory")); // 库存
				normalItemInfo.put("name",itemInfo.get("itemName")); // 规格名
				normalItemInfo.put("name",itemInfo.get("name")); // 规格名
				normalItemInfo.put("value",itemInfo.get("value")); // 规格名
				normalItemInfo.put("limitCount",0); // 无限购
				normalItemInfo.put("productItemId",itemInfo.get("productItemId")); // 规格Id
			}
		}
		normalItemInfo.put("value",groupBuyItemInfo.get("value"));
		resultMap.put("groupBuyItemInfo",groupBuyItemInfo);
		resultMap.put("normalItemInfo",normalItemInfo);
		return resultMap;
	}
}
