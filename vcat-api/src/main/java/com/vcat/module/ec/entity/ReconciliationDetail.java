package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.entity.User;

/**
 * 对账明细
 */
public class ReconciliationDetail extends DataEntity<ReconciliationDetail> {
	private static final long serialVersionUID = 1L;
    private Order order;            // 对账订单
    private OrderItem orderItem;    // 对账订单项
    private String note;            // 备注
    private User operator;          // 操作人

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

}
