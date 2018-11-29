package com.vcat.module.ec.service;

import com.vcat.common.chart.Histogram;
import com.vcat.common.persistence.Page;
import com.vcat.module.core.service.BaseService;
import com.vcat.module.ec.dao.StatisticsDao;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 统计Service
 */
@Service
@Transactional(readOnly = true)
public class StatisticsService extends BaseService{

    @Autowired
    private StatisticsDao statisticsDao;

    /**
     * 今日概况
     * @return
     */
    public Map<String,Object> overview(String st,String et){
        return statisticsDao.overview(st,et);
    }

    /**
     * 商品销量统计表
     * @param page
     * @param productStatistics
     * @return
     */
    public Page<ProductStatistics> productStatistics(Page page, ProductStatistics productStatistics) {
        productStatistics.setPage(page);
        page.setList(statisticsDao.productStatistics(productStatistics));
        return page;
    }

    /**
     * 店铺销量统计表
     * @param page
     * @param shopStatistics
     * @return
     */
    public Page<ShopStatistics> shopStatistics(Page page, ShopStatistics shopStatistics) {
        shopStatistics.setPage(page);
        page.setList(statisticsDao.shopStatistics(shopStatistics));
        return page;
    }

    /**
     * 商品购买店主列表
     * @param page
     * @param productBuyer
     * @return
     */
    public Page<ProductBuyer> buyerPage(Page<ProductBuyer> page, ProductBuyer productBuyer) {
        productBuyer.setPage(page);
        page.setList(statisticsDao.buyerList(productBuyer));
        return page;
    }

    public Page<ProductSeller> sellerPage(Page<ProductSeller> page, ProductSeller productSeller) {
        productSeller.setPage(page);
        page.setList(statisticsDao.sellerList(productSeller));
        return page;
    }

    /**
     * 获取KPI柱状图统计结果
     * @param supplier
     * @return
     */
    public Histogram kpiToColumn(Supplier supplier) {
        return new Histogram(statisticsDao.kpiToColumn(supplier));
    }
}
