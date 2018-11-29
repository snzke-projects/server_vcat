package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * 商品上架预告
 */
public class ProductForecast extends DataEntity<ProductForecast>{
    private static final long serialVersionUID = 1L;
    private Product product;        // 商品
    private String title;           // 分享活动主题
    private String imgUrl;          // 活动图片
    private Date forecastDate;      // 预计上架时间
    private String intro;           // 预告简介
    private String templateUrl;     // HTML5上架预告详情地址
    private String isActivate = NOT_ACTIVATED;	// 是否激活
    private Date activateTime;      // 激活时间

    public String getImgUrlPath(){
        return QCloudUtils.createOriginalDownloadUrl(imgUrl);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(Date forecastDate) {
        this.forecastDate = forecastDate;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public String getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(String isActivate) {
        this.isActivate = isActivate;
    }

    public Date getActivateTime() {
        return activateTime;
    }

    public void setActivateTime(Date activateTime) {
        this.activateTime = activateTime;
    }
}
