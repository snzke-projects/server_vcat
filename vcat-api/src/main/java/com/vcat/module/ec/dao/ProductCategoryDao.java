package com.vcat.module.ec.dao;

import com.vcat.common.dao.LRTreeDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Brand;
import com.vcat.module.ec.entity.ProductCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品分类DAO接口
 */
@MyBatisDao
public interface ProductCategoryDao extends LRTreeDao<ProductCategory> {
	int addProductCategory(ProductCategory productCategory);
	int deleteProductCategory(String productId);

    /**
     * 获取子分类中最高排序
     * @param parentId
     * @return
     */
    int getHighestOrder(String parentId);

    /**
     * (取消)激活商品分类
     * @param productCategory
     */
    void activate(ProductCategory productCategory);

    /**
     * 下架分类下所有商品
     * @param productCategory
     */
    void archivedProductWhenCancelActivate(ProductCategory productCategory);
    //获取小店里面有商品的类别
	List<ProductCategory> getListByCustomer(String customerId);
	//获取子分类
	List<Map<String, Object>> findCategoryList(String categoryId);
	//获取店主的商品子类别
	List<ProductCategory> getListByShopId(@Param(value = "shopId")String shopId,@Param(value = "parentCategoryId")String parentCategoryId);
	
	List<Brand> findBrandList(String parentCategoryId);

}
