package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 对账明细
 */
public class Accounting extends DataEntity<Accounting> {
	private static final long serialVersionUID = 1L;
    private Order order;            // 对账订单

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
