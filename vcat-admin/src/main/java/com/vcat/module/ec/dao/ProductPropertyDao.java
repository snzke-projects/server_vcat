package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductCategory;
import com.vcat.module.ec.entity.ProductProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface ProductPropertyDao extends CrudDao<ProductProperty> {
    int insertCategoryProperty(ProductProperty productProperty);
    int insertProductProperty(ProductProperty productProperty);
    int deleteCategoryProperty(String categoryId);
    int deleteProductProperty(String productId);
    List<ProductProperty> findListByProduct(Product product);
    List<ProductProperty> findAll();
    List<ProductProperty> findListByCategory(@Param(value = "category")ProductCategory category,@Param(value = "editable")boolean editable);
}
