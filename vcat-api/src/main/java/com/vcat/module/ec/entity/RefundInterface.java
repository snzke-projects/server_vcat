package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 退款单退款接口数据
 */
public class RefundInterface extends DataEntity<RefundInterface> {
    private static final long serialVersionUID = 1L;
    private Refund refund;          // 对应退款单
    private Gateway	gateway;		// 账户类型
    private String batchNo;         // 批次号
    private String requestData;     // 请求数据
    private Boolean requestSuccess; // 是否请求成功
    private String responseData;    // 响应数据(回调数据)
    private Boolean result;          // 退款结果
    private Boolean isActivate;     // 是否激活 0表示过期或作废或被覆盖

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public Boolean getRequestSuccess() {
        return requestSuccess;
    }

    public void setRequestSuccess(Boolean requestSuccess) {
        this.requestSuccess = requestSuccess;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Boolean getActivate() {
        return isActivate;
    }

    public void setActivate(Boolean activate) {
        isActivate = activate;
    }
}
