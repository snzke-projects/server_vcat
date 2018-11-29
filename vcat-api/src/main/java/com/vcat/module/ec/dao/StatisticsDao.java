package com.vcat.module.ec.dao;

import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface StatisticsDao{
    /**
     * 今日概况
     * @return
     */
    Map<String,Object> overview(@Param(value = "st")String st,@Param(value = "et")String et);

    /**
     * 商品销量统计表
     * @param productStatistics
     * @return
     */
    List<ProductStatistics> productStatistics(ProductStatistics productStatistics);

    /**
     * 店铺销量统计表
     * @param shopStatistics
     * @return
     */
    List<ShopStatistics> shopStatistics(ShopStatistics shopStatistics);

    /**
     * 商品购买店主列表
     * @param productBuyer
     * @return
     */
    List<ProductBuyer> buyerList(ProductBuyer productBuyer);

    /**
     * 商品卖家列表
     * @param productSeller
     * @return
     */
    List<ProductSeller> sellerList(ProductSeller productSeller);

    /**
     * 获取KPI柱状图统计结果
     * @param supplier
     * @return
     */
    List<Map<String,Object>> kpiToColumn(Supplier supplier);
}
