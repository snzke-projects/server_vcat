package com.vcat.module.ec.dao;

import com.vcat.common.dao.LRTreeDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ProductCategory;

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

}
