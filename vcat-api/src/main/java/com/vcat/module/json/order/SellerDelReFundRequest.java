package com.vcat.module.json.order;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/19.
 * Description:
 */
public class SellerDelReFundRequest {
    @NotNull
    private String refundId;

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }
}
