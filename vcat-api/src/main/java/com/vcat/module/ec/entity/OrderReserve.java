package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

public class OrderReserve extends DataEntity<OrderReserve> {
    private static final long serialVersionUID = 2113274485520629829L;
    private Order order;
    private OrderItem orderItem;
    private RecommendEntity recommendEntity;

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

    public RecommendEntity getRecommendEntity() {
        return recommendEntity;
    }

    public void setRecommendEntity(RecommendEntity recommendEntity) {
        this.recommendEntity = recommendEntity;
    }
}
