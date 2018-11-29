package com.vcat.module.json.order;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/16.
 * Description:
 */
public class SellerCancelOrderRequest {
    public static final int DONTBUY   = 1;
    public static final int INFOERROR = 2;
    public static final int PAYFAIL   = 3;
    public static final int OTHER     = 4;
    public static final int TIMEOUT     = 5;
    @NotNull
    private int    type;
    @NotNull
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
