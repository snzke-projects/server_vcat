package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 邮费结算明细
 */
public class ReconciliationFreight extends DataEntity<ReconciliationFreight> {
	private static final long serialVersionUID = 1L;
    private Order order;            // 邮费结算订单

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
