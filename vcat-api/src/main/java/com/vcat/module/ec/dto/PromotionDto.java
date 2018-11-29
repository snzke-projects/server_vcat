package com.vcat.module.ec.dto;

import java.io.Serializable;


public class PromotionDto implements Serializable {
    private static final long serialVersionUID = -4671692755704268923L;
    private int quantity;
    private int buyCount;
    private int freeCount;
    private String productItemId;

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

    public String getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(String productItemId) {
        this.productItemId = productItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
