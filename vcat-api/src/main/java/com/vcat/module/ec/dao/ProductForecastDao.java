package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ProductForecast;

import java.util.List;
import java.util.Map;

/**
 * 商品上架预告
 */
@MyBatisDao
public interface ProductForecastDao extends CrudDao<ProductForecast> {
    Integer activate(ProductForecast productForecast);

    /**
     * 客户端获取 上架预告 列表
     * @param forecast 上架预告商品查询条件
     * @return
     */
    List<Map<String,Object>> findAppList(ProductForecast forecast);
    /**
     * 客户端获取 上架预告 详情
     * @param forecast 上架预告商品查询条件
     * @return
     */
    Map<String,Object> getForecastDetail(ProductForecast forecast);

    /**
     * 检查该商品是否已参与上架预告
     * @param productForecast
     * @return
     */
    boolean checkSameProductActivated(ProductForecast productForecast);
}

