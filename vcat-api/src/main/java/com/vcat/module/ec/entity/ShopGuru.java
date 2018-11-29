package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

public class ShopGuru extends DataEntity<ShopGuru>{
	private static final long serialVersionUID = -5419796352485110905L;
    private String title;           // 标题
    private String intro;           // 简介
    private String details;         // 详情
    private String templateUrl;     // 详情页面地址
    private String imgUrl;          // 头图地址
    private Integer displayOrder;   // 排序

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
