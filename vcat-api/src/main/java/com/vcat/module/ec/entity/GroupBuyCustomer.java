package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * Created by Code.Ai on 16/5/11.
 * Description: 参团人
 */
public class GroupBuyCustomer extends DataEntity<GroupBuyCustomer> {
    public static final Integer NO_PAY           = 0;
    public static final Integer YET_PAY          = 1;
    private static final long    serialVersionUID = 1209200354895189471L;
    private GroupBuySponsor groupBuySponsor;
    private Order           order;
    private OrderItem       orderItem;
    private Date            joinedDate;
    private Customer        customer;
    private Boolean         isSponsor;
    private Integer         status;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public GroupBuySponsor getGroupBuySponsor() {
        return groupBuySponsor;
    }

    public void setGroupBuySponsor(GroupBuySponsor groupBuySponsor) {
        this.groupBuySponsor = groupBuySponsor;
    }

    public Boolean getSponsor() {
        return isSponsor;
    }

    public void setSponsor(Boolean sponsor) {
        isSponsor = sponsor;
    }

    public Date getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Date joinedDate) {
        this.joinedDate = joinedDate;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
