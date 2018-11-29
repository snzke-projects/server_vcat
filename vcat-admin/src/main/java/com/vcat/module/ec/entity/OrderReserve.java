package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.List;

/**
 * 进货订单库存清理实体
 */
public class OrderReserve extends DataEntity<OrderReserve> {
    private String orderId;         // 订单ID
    private String orderNumber;     // 订单号
    private String productId;       // 商品ID
    private String productName;     // 商品名称
    private Integer orderInventory; // 订单总库存
    private Integer salesInventory; // 已销售库存
    private Integer clearInventory; // 已清理库存
    private String shopName;        // 待清理小店名称
    private String shopPhone;       // 店铺手机号
    private String deliveryName;    // 收货人
    private String deliveryPhone;   // 收货电话
    private String detailAddress;   // 收货地址

    private List<OrderClearInventory> logList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getOrderInventory() {
        return orderInventory;
    }

    public void setOrderInventory(Integer orderInventory) {
        this.orderInventory = orderInventory;
    }

    public Integer getSalesInventory() {
        return salesInventory;
    }

    public void setSalesInventory(Integer salesInventory) {
        this.salesInventory = salesInventory;
    }

    public Integer getClearInventory() {
        return clearInventory;
    }

    public void setClearInventory(Integer clearInventory) {
        this.clearInventory = clearInventory;
    }

    /**
     * 获取剩余库存
     * @return
     */
    public Integer getLastInventory() {
        Integer oi = orderInventory != null ? orderInventory : 0;
        Integer si = salesInventory != null ? salesInventory : 0;
        Integer ci = clearInventory != null ? clearInventory : 0;
        return oi - si - ci;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public List<OrderClearInventory> getLogList() {
        return logList;
    }

    public void setLogList(List<OrderClearInventory> logList) {
        this.logList = logList;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }
}
