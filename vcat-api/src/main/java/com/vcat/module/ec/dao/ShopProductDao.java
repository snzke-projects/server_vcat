package com.vcat.module.ec.dao;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ShopProduct;

import java.util.List;

@MyBatisDao
public interface ShopProductDao extends CrudDao<ShopProduct> {

	//上架或者下架商品
	void upOrDownProduct(ShopProduct sp);

	void upProduct(ShopProduct sp);

	void insertProduct(ShopProduct sp);

	void addBrandProduct(@Param("shopId") String shopId, @Param("brandId")String brandId);

    /**
     * 获取可自动拿样一次的商品库对象
     * @param productId 商品ID
     * @return
     */
    List<ShopProduct> findAutoTakeList(String productId);

    /**
     * 获取需取消拿样的商品库对象
     * @param productId 商品ID
     * @return
     */
    List<ShopProduct> findCancelTakeList(String productId);

    /**
     * 取消拿样商品
     * @param shopProduct
     */
    void cancelTake(ShopProduct shopProduct);
 	//下架之前的店铺下的所有商品。去除拿样
	void batchDelete(String shopId);
	//更新品牌下的所有商品
	void updateBrandProduct(@Param("shopId") String shopId, @Param("brandId")String brandId);
	//更新预售商品库存
	void updateQuantiy(@Param("shopId") String shopId,@Param("productItemId")String productItemId,@Param("quantity") int quantity);
	Integer getInventory(@Param("shopId") String shopId,@Param("productId")String productId);
}
