package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * Created by Code.Ai on 16/4/13.
 * Description:
 */
public class OrderReserveLog extends DataEntity<OrderReserveLog> {
    private static final long serialVersionUID = -8774780815990021942L;
    private OrderItem  orderItem;	    // 所属订单
    private Date   addDate;	    // 操作时间
    private int    quantity;    // 操作数量
    private int    type;        // 操作类型 0:店主进货;1:店主提货;2:店主取消提货;3:客户购买;4:客户取消订单

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
