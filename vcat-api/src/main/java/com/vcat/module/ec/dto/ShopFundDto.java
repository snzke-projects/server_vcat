package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Code.Ai on 16/6/20.
 * Description:
 */
public class ShopFundDto implements Serializable {
    private String id;
    private BigDecimal saleHoldFund;
    private String orderItemId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public BigDecimal getSaleHoldFund() {
        return saleHoldFund;
    }

    public void setSaleHoldFund(BigDecimal saleHoldFund) {
        this.saleHoldFund = saleHoldFund;
    }

    @Override
    public String toString() {
        return "ShopFundDto{" +
                "id='" + id + '\'' +
                ", saleHoldFund=" + saleHoldFund +
                ", orderItemId='" + orderItemId + '\'' +
                '}';
    }
}
