package com.vcat.module.ec.dto;

import java.io.Serializable;

/**
 * Created by Code.Ai on 16/4/14.
 * Description:
 */
public class OrderReserveLogDto implements Serializable {
    private String orderId;
    private String type;
    private int quantity;
    private String addTime;

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getQuantity() {
        if(quantity > 0) return "+" + quantity;
        else return quantity + "";
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
