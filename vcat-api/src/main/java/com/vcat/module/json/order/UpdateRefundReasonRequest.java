package com.vcat.module.json.order;

/**
 * Created by Code.Ai on 16/5/23.
 * Description:
 */
public class UpdateRefundReasonRequest {
    private String refundId;
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }
}
