package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ProductForecast;

/**
 * 商品上架预告
 */
@MyBatisDao
public interface ProductForecastDao extends CrudDao<ProductForecast> {
    Integer activate(ProductForecast productForecast);

    /**
     * 检查该商品是否已参与上架预告
     * @param productForecast
     * @return
     */
    boolean checkSameProductActivated(ProductForecast productForecast);
}

