package com.vcat.module.json.order;

import java.util.List;

/**
 * Created by Code.Ai on 16/5/24.
 * Description:
 */
public class OrderIdListRequest {
    private List<MergePayRequest> orderIdList;

    public List<MergePayRequest> getOrderIdList() {
        return orderIdList;
    }

    public void setOrderIdList(List<MergePayRequest> orderIdList) {
        this.orderIdList = orderIdList;
    }
}
