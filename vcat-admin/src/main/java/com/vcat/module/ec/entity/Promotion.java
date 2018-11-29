package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.entity.User;

import java.util.Date;
import java.util.List;

public class Promotion extends DataEntity<Promotion> {
	private static final long serialVersionUID = 1L;
    private List<Product> productList;
    private String name;    // 促销名
    private int buyCount;   // 购买量
    private int freeCount;  // 赠送量
    private int isActivate; // 是否激活
    private User operBy;    // 修改人
    private Date operDate;  // 修改时间

    public List<Product> getProductList() {
        return productList;
    }

    public String getProductIdArray(){
        if(null == productList || productList.isEmpty()){
            return "";
        }
        StringBuffer productIdArray = new StringBuffer();
        productList.forEach(product -> productIdArray.append(product.getId() + "|"));
        return productIdArray.toString();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(int buyCount) {
        this.buyCount = buyCount;
    }

    public int getFreeCount() {
        return freeCount;
    }

    public void setFreeCount(int freeCount) {
        this.freeCount = freeCount;
    }

    public int getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(int isActivate) {
        this.isActivate = isActivate;
    }

    public User getOperBy() {
        return operBy;
    }

    public void setOperBy(User operBy) {
        this.operBy = operBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }
}