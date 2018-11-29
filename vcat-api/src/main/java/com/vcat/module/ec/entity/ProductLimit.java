package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * 商品限购
 */
public class ProductLimit extends DataEntity<ProductLimit>{
    private static final long serialVersionUID = 1L;
    private ProductItem productItem;// 商品规格
    private Integer interval;       // 间隔
    private Integer times;          // 次数
    private Date startTime;         // 开始时间
    private Date endTime;           // 结束时间
    private Integer productType;    // 限购类型（订单类型）
    private Integer userType;       // 限购目标（0 店铺和买家 1 店铺 2 买家）

    public ProductItem getProductItem() {
        return productItem;
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }
}
