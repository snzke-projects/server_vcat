package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

import java.math.BigDecimal;

/**
 * 供货商结算
 */
public class StatementOfAccount extends DataEntity<StatementOfAccount> {
    private static final long serialVersionUID = 1L;
    private String orderId;
    @ExcelField(title = "订单类型", align = 2, sort = 10)
    private String orderType;
    @ExcelField(title = "供应商", align = 2, sort = 20)
    private String supplierName;
    @ExcelField(title = "品牌", align = 2, sort = 25)
    private String brandName;
    @ExcelField(title = "订单号", align = 2, sort = 30)
    private String orderNumber;
    @ExcelField(title = "下单时间", align = 2, sort = 40)
    private String orderTime;
    @ExcelField(title = "商品名称", align = 2, sort = 45)
    private String productName;
    @ExcelField(title = "规格名称", align = 2, sort = 50)
    private String itemName;
    @ExcelField(title = "商品数量", align = 2, sort = 60)
    private String productCount;
    @ExcelField(title = "买家昵称", align = 2, sort = 70)
    private String buyerName;
    @ExcelField(title = "买家手机号", align = 2, sort = 80)
    private String buyerPhone;
    @ExcelField(title = "支付单号", align = 2, sort = 90)
    private String paymentNumber;
    @ExcelField(title = "支付时间", align = 2, sort = 100)
    private String paymentTime;
    @ExcelField(title = "支付状态", align = 2, sort = 110)
    private String paymentStatus;
    @ExcelField(title = "订单状态", align = 2, sort = 120)
    private String orderStatus;
    @ExcelField(title = "发货状态", align = 2, sort = 130)
    private String shippingStatus;
    @ExcelField(title = "配送方式", align = 2, sort = 140)
    private String deliveryMethod;
    @ExcelField(title = "快递公司", align = 2, sort = 150)
    private String expressName;
    @ExcelField(title = "快递单号", align = 2, sort = 160)
    private String shippingNumber;
    @ExcelField(title = "快递费", align = 2, sort = 170)
    private BigDecimal freightPrice;
    @ExcelField(title = "订单金额", align = 2, sort = 180)
    private BigDecimal orderTotalPrice;
    @ExcelField(title = "销售佣金", align = 2, sort = 190)
    private BigDecimal saleEarning;
    @ExcelField(title = "销售分红", align = 2, sort = 200)
    private BigDecimal bonusEarning;
    @ExcelField(title = "二级分红", align = 2, sort = 210)
    private BigDecimal secondBonusEarning;
    @ExcelField(title = "使用V猫币", align = 2, sort = 220)
    private BigDecimal singleCoupon;
    @ExcelField(title = "平台扣点金额", align = 2, sort = 230)
    private BigDecimal point;
    @ExcelField(title = "结算费", align = 2, sort = 240)
    private BigDecimal balance;

    public static StatementOfAccount getTitle() {
        StatementOfAccount account = new StatementOfAccount();
        account.setOrderType("统计");
        return account;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getProductCount() {
        return productCount;
    }

    public void setProductCount(String productCount) {
        this.productCount = productCount;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getShippingNumber() {
        return shippingNumber;
    }

    public void setShippingNumber(String shippingNumber) {
        this.shippingNumber = shippingNumber;
    }

    public BigDecimal getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(BigDecimal freightPrice) {
        this.freightPrice = freightPrice;
    }

    public BigDecimal getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(BigDecimal orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public BigDecimal getSaleEarning() {
        return saleEarning;
    }

    public void setSaleEarning(BigDecimal saleEarning) {
        this.saleEarning = saleEarning;
    }

    public BigDecimal getBonusEarning() {
        return bonusEarning;
    }

    public void setBonusEarning(BigDecimal bonusEarning) {
        this.bonusEarning = bonusEarning;
    }

    public BigDecimal getSecondBonusEarning() {
        return secondBonusEarning;
    }

    public void setSecondBonusEarning(BigDecimal secondBonusEarning) {
        this.secondBonusEarning = secondBonusEarning;
    }

    public BigDecimal getSingleCoupon() {
        return singleCoupon;
    }

    public void setSingleCoupon(BigDecimal singleCoupon) {
        this.singleCoupon = singleCoupon;
    }

    public BigDecimal getPoint() {
        return point;
    }

    public void setPoint(BigDecimal point) {
        this.point = point;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
