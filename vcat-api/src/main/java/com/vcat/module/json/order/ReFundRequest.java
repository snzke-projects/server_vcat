package com.vcat.module.json.order;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by Code.Ai on 16/5/19.
 * Description:
 */
public class ReFundRequest {
    @NotNull
    private int receiptType;  // 1:未收货 2:已收货
    @NotNull
    private String reason;
    @NotNull
    private String orderItemId;
    @NotNull
    private BigDecimal amount;
    private String describe;
    private String phone;
    private String refundId;
    private String hasFreightPrice;

    public String getHasFreightPrice() {
        return hasFreightPrice;
    }

    public void setHasFreightPrice(String hasFreightPrice) {
        this.hasFreightPrice = hasFreightPrice;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public int getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(int receiptType) {
        this.receiptType = receiptType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
