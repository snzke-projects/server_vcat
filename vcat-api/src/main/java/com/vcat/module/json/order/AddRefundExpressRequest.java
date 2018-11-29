package com.vcat.module.json.order;

/**
 * Created by Code.Ai on 16/5/23.
 * Description:
 */
public class AddRefundExpressRequest {
    private String refundId;
    private String shippingNum;
    private String expressCode;

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getShippingNum() {
        return shippingNum;
    }

    public void setShippingNum(String shippingNum) {
        this.shippingNum = shippingNum;
    }
}
