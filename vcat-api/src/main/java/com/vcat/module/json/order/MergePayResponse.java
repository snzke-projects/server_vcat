package com.vcat.module.json.order;

import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.module.core.entity.MsgEntity;

/**
 * Created by Code.Ai on 16/5/20.
 * Description:
 */
public class MergePayResponse extends MsgEntity {
    public MergePayResponse() {
        super(ApiMsgConstants.SUCCESS_MSG, ApiMsgConstants.SUCCESS_CODE);
    }
    private String paymentId;
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
