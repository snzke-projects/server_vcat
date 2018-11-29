package com.vcat.api.service;

import java.util.List;
import java.util.Map;

import com.vcat.module.ec.dto.ProductItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ProductItemDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductItem;
@Service
@Transactional(readOnly = true)
public class ProductItemService extends CrudService<ProductItem> {

	@Autowired
	private ProductItemDao productItemDao;

	@Override
	protected CrudDao<ProductItem> getDao() {
		return productItemDao;
	}

	public List<ProductItem> findListByProductId(Product product) {
		
		return productItemDao.findListByProductForBuyer(product);
	}

	public String getSupplierId(String productItemId) {
		
		return productItemDao.getSupplierId(productItemId);
	}

	public void updateQuantiy(String productItemId, int quantity, String checkOrderType) {
		productItemDao.updateQuantiy(productItemId,quantity,checkOrderType);
	}
	//普通下单获取产品项的下架情况
	public ProductItem getItem(String productItemId, String shopId) {
		return productItemDao.getItem(productItemId,shopId);
	}

	public ProductItem getCouponItem(String productItemId, String buyerId) {
		
		return productItemDao.getCouponItem(productItemId,buyerId);
	}

	public long getWeight(String productItemId) {

		return productItemDao.getWeight(productItemId);
	}
	/**
	 * 通过商品项获取商品的发货商id
	 * @param productItemId
	 * @return
	 */
	public String getDistributionId(String productItemId) {
		
		return productItemDao.getDistributionId(productItemId);
	}

	//通过商品项ID 获取对应商品的的优惠规则
	public Map<String,Object> getRuleByProductItemId(String productItemId){
        return productItemDao.getRuleByProductItemId(productItemId);
    }
	//通过商品项获取此商品是否为预售商品
	public List<Map<String,String>> getProductRecommendType(String productItem){
		return productItemDao.getProductRecommendType(productItem);
	}
	public List<ProductItem> getProductItemsByPayment(String paymentId){
		return productItemDao.getProductItemsByPayment(paymentId);
	}
	public ProductItemDto getProductItemDto(String orderItemId){
		return productItemDao.getProductItemDto(orderItemId);
	}
}


