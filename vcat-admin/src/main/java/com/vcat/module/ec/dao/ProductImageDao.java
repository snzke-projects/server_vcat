package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ProductImage;

import java.util.List;

@MyBatisDao
public interface ProductImageDao extends CrudDao<ProductImage> {
    List<ProductImage> findListByProductId(String id);
    int insertProductImage(ProductImage productImage);
    List<String> findImageIdList(String productId);
    int deleteProductImage(String productId);
}

