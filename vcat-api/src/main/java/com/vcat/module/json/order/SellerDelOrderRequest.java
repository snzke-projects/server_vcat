package com.vcat.module.json.order;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/17.
 * Description:
 */
public class SellerDelOrderRequest {
    @NotNull
    private String orderId;
    private String type;  // buyer :买家删除

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
