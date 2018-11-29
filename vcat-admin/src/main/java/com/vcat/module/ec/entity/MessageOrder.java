package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单消息
 */
public class MessageOrder extends DataEntity<MessageOrder> {
    private static final long serialVersionUID = 1L;
    public static final String TYPE_PAID_ORDER = "1";       // 已支付订单
    public static final String TYPE_SHIPPED = "2";          // 发货
    public static final String TYPE_DELIVERY = "3";         // 确认收货
    private Shop shop;          // 消息所属小店
    private boolean isRead;     // 是否已读
    private Date readDate;      // 阅读时间
    private String orderNumber; // 订单号
    private String productName; // 商品名称
    private BigDecimal paymentAmount;// 支付金额
    private BigDecimal actualEarning;// 实际收入或待确认收入
    private Date orderTime;     // 下单时间
    private String type;        // 消息类型

    public String getMessageTitle(){
        if(TYPE_PAID_ORDER.equals(type)){
            return "老板，顾客付款下单啦！";
        }else if(TYPE_SHIPPED.equals(type)){
            return "老板，顾客购买的商品已经发货啦！";
        }else if(TYPE_DELIVERY.equals(type)){
            return "老板，顾客已经确认收货啦！";
        }else{
            return "老板，您有新的订单消息！";
        }
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getActualEarning() {
        return actualEarning;
    }

    public void setActualEarning(BigDecimal actualEarning) {
        this.actualEarning = actualEarning;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
