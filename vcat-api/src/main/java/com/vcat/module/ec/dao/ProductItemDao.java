package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.ProductItemDto;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品规格DAO接口
 */
@MyBatisDao
public interface ProductItemDao extends CrudDao<ProductItem> {
    List<ProductItem> findListByProduct(Product product);
    // 买家接口
	List<ProductItem> findListByProductForBuyer(Product product);
    //获取规格id
	String getSupplierId(String productItemId);
	//下单更新库存，减去下单个数
	void updateQuantiy(@Param("productItemId") String productItemId, @Param("quantity") int quantity, @Param("checkOrderType") String checkOrderType);
    // 根据商品逻辑删除规格
    void deleteByProduct(Product product);

    //获取商品项
	ProductItem getItem(@Param("productItemId")String productItemId, @Param("shopId")String shopId);

    // 根据商品获取规格价格相关
    List<Map<String,Object>> getProductItemList(@Param("productId")String productId, @Param("type")String type);
    // 根据商品获取规格标题
    List<Map<String,Object>> getProductItemTitleList(String productId);
    
	ProductItem getCouponItem(@Param("productItemId")String productItemId, @Param("shopId")String buyerId);
	
	void updateCouponTime(@Param("productItemId")String productItemId, @Param("quantity")int quantity);
	
	long getWeight(String productItemId);
	
	String getDistributionId(String productItemId);

    List<ProductItem> findListByIds(@Param("idArray")String[] idArray);

    Map<String,Object> getRuleByProductItemId(@Param("productItemId")String productItemId);

    List<Map<String,String>> getProductRecommendType(@Param("productItemId")String productItemId);
    List<ProductItem> getProductItemsByPayment(@Param("paymentId")String paymentId);
    List<Map<String, Object>> getGroupBuyProductItemInfo(@Param("groupBuyId") String groupBuyId);
    ProductItemDto getProductItemDto(String orderItemId);
}
