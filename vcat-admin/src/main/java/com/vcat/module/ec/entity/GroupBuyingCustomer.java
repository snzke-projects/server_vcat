package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * 团购参加信息
 */
public class GroupBuyingCustomer extends DataEntity<GroupBuyingCustomer> {
	private static final long serialVersionUID = 7309007870868246266L;

    private GroupBuyingSponsor sponsor; // 所属小团购
    private Order order;                // 团购订单
    private OrderItem orderItem;        // 团购订单项
    private Date joinedDate;            // 参加团购时间
    private Customer customer;          // 参加团购人
    private Boolean isSponsor;          // 是否为发起人
    private Integer status;             // 状态 0 未支付 1已支付

    public GroupBuyingSponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(GroupBuyingSponsor sponsor) {
        this.sponsor = sponsor;
    }

    public Boolean getIsSponsor() {
        return isSponsor;
    }

    public void setIsSponsor(Boolean isSponsor) {
        this.isSponsor = isSponsor;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Date getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Date joinedDate) {
        this.joinedDate = joinedDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
