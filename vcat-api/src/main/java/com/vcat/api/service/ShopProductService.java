package com.vcat.api.service;

import com.vcat.module.ec.dto.ShopProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ShopProductDao;
import com.vcat.module.ec.entity.ShopProduct;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ShopProductService extends CrudService<ShopProduct> {

	@Autowired
	private ShopProductDao shopProductDao;
	@Override
	protected CrudDao<ShopProduct> getDao() {
		return shopProductDao;
	}
	//上架或者下架商品
	@Transactional(readOnly = false)
	public void upOrDownProduct(ShopProduct sp) {
		
		shopProductDao.upOrDownProduct(sp);
	}
	//支付完成后，更新拿样状态和时间
	public void upProduct(ShopProduct sp) {
		shopProductDao.upProduct(sp);	
	}
	//支付完成后，添加商品和拿样状态和时间
	public void insertProduct(ShopProduct sp) {
		shopProductDao.insertProduct(sp);
	}
	//上架品牌下的所有可以上架的商品
	public void addBrandProduct(String shopId, String supplierId) {
		
		shopProductDao.addBrandProduct(shopId,supplierId);
	}
	//删除店铺下的所有商品
	@Transactional(readOnly = false)
	public void batchDelete(String shopId) {
		
		shopProductDao.batchDelete(shopId);
	}	
	//更新店铺下的所有商品
	@Transactional(readOnly = false)
	public void updateBrandProduct(String shopId, String supplierId) {
		shopProductDao.updateBrandProduct(shopId,supplierId);
	}
	//更新预售的商品库存
	public void updateQuantiy(String shopId, String productItemId, int quantity) {
        shopProductDao.updateQuantiy(shopId,productItemId,quantity);
	}
	//获取店铺中预售商品的库存
    public int getInventory(String shopId, String productId){
        return shopProductDao.getInventory(shopId,productId) == null ? 0 : shopProductDao.getInventory(shopId,productId);
    }
}
