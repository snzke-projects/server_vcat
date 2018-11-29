package com.vcat.module.json.order;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/16.
 * Description:
 */
public class GetSellerOrderListRequest {
    private String orderType;
    @NotNull
    private int pageNo;
    private String condition;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
}
