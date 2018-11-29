package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ProductHotSaleImage;

import java.util.List;

@MyBatisDao
public interface ProductHotSaleImageDao extends CrudDao<ProductHotSaleImage> {
    List<ProductHotSaleImage> findListByProductId(String id);
    int insertProductHotSaleImage(ProductHotSaleImage productImage);
    List<String> findHotSaleImageIdList(String productId);
    int deleteProductHotSaleImage(String productId);
}

