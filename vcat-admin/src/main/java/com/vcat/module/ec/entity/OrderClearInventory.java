package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.entity.User;

import java.util.Date;

/**
 * 预购订单库存清理记录
 */
public class OrderClearInventory extends DataEntity<OrderClearInventory> {
    private String orderId;         // 预购进货订单
    private String deliveryName;    // 收货人
    private String deliveryPhone;   // 收货电话
    private String detailAddress;   // 收货地址
    private Integer clearInventory; // 本次清理库存
    private Express express;        // 物流公司
    private Date shippingDate;      // 发货日期
    private String shippingNumber;  // 物流单号
    private String note;            // 备注
    private User operBy;            // 清理库存操作人
    private Date operDate;          // 操作时间

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public Integer getClearInventory() {
        return clearInventory;
    }

    public void setClearInventory(Integer clearInventory) {
        this.clearInventory = clearInventory;
    }

    public Express getExpress() {
        return express;
    }

    public void setExpress(Express express) {
        this.express = express;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(Date shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getShippingNumber() {
        return shippingNumber;
    }

    public void setShippingNumber(String shippingNumber) {
        this.shippingNumber = shippingNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getOperBy() {
        return operBy;
    }

    public void setOperBy(User operBy) {
        this.operBy = operBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }
}
