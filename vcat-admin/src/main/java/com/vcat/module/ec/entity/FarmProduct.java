package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.List;

public class FarmProduct extends DataEntity<FarmProduct> {
	private static final long serialVersionUID = 1L;
    private Product farm;               // 庄园虚拟商品
    private List<Product> productList;  // 庄园对应商品集合
    private String productNames;        // 商品名称集合
    private String productIds;          // 商品ID集合
    private String wechatNo;            // 服务微信号

    public Product getFarm() {
        return farm;
    }

    public void setFarm(Product farm) {
        this.farm = farm;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public String getProductIds() {
        return productIds;
    }

    public void setProductIds(String productIds) {
        this.productIds = productIds;
    }

    public String getWechatNo() {
        return wechatNo;
    }

    public void setWechatNo(String wechatNo) {
        this.wechatNo = wechatNo;
    }
}