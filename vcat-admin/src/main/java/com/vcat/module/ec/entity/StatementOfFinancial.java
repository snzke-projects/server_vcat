package com.vcat.module.ec.entity;

import com.vcat.common.utils.StatusColor;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

import java.math.BigDecimal;

/**
 * 供货商结算
 */
public class StatementOfFinancial extends DataEntity<StatementOfFinancial> {
	private static final long serialVersionUID = 1L;
    private String orderId;
    private String paymentId;
    @ExcelField(title="商户订单号", align=2, sort=10)
    private String paymentNo;
    @ExcelField(title="订单号", align=2, sort=20)
    private String orderNumber;
    @ExcelField(title="供应商", align=2, sort=30)
    private String supplierName;
    @ExcelField(title="品牌", align=2, sort=35)
    private String brandName;
    @ExcelField(title="商品名称", align=2, sort=40)
    private String productName;
    @ExcelField(title="购买数量", align=2, sort=45)
    private String productQuantity;
    @ExcelField(title="订单类型", align=2, sort=50)
    private String orderType;
    @ExcelField(title="发货人", align=2, sort=55)
    private String sender;
    @ExcelField(title="支付时间", align=2, sort=60)
    private String paymentTime;
    @ExcelField(title="支付方式", align=2, sort=70)
    private String paymentType;
    @ExcelField(title="支付金额", align=2, sort=80)
    private BigDecimal paymentAmount;
    @ExcelField(title="快递费", align=2, sort=90)
    private BigDecimal freightPrice;
    @ExcelField(title="销售佣金", align=2, sort=100)
    private BigDecimal saleEarning;
    @ExcelField(title="销售分红", align=2, sort=110)
    private BigDecimal bonusEarning;
    @ExcelField(title="一级团队分红", align=2, sort=115)
    private BigDecimal firstBonusEarning;
    @ExcelField(title="二级团队分红", align=2, sort=120)
    private BigDecimal secondBonusEarning;
    @ExcelField(title="供应商结算费", align=2, sort=130)
    private BigDecimal supplierBalance;
    @ExcelField(title="平台结算费", align=2, sort=140)
    private BigDecimal terraceBalance;
    @ExcelField(title="退款金额", align=2, sort=150)
    private String refundAmount;
    @ExcelField(title="退款执行时间", align=2, sort=155)
    private String refundOperDate;
    @ExcelField(title="邮费结算状态", align=2, sort=165)
    private String reconciliationFreightStatus;
    @ExcelField(title="结算状态", align=2, sort=160)
    private String reconciliationStatus;
    @ExcelField(title="记账标识", align=2, sort=170)
    private String accountingStatus;

    public String getAccountingStatusColor(){
        if(accountingStatus.contains("未")){
            return StatusColor.CORNFLOWER_BLUE;
        }else if(accountingStatus.contains("已")){
            return StatusColor.GREEN;
        }else{
            return StatusColor.BLACK;
        }
    }

    public String getReconciliationStatusColor(){
        if(reconciliationStatus.contains("未")){
            return StatusColor.RED;
        }else if(reconciliationStatus.contains("部分")){
            return StatusColor.CORNFLOWER_BLUE;
        }else if(reconciliationStatus.contains("已")){
            return StatusColor.GREEN;
        }else{
            return StatusColor.BLACK;
        }
    }

    public String getReconciliationFreightStatusColor(){
        if(reconciliationFreightStatus.contains("未")){
            return StatusColor.RED;
        }else if(reconciliationFreightStatus.contains("已")){
            return StatusColor.GREEN;
        }else{
            return StatusColor.BLACK;
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(BigDecimal freightPrice) {
        this.freightPrice = freightPrice;
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

    public BigDecimal getFirstBonusEarning() {
        return firstBonusEarning;
    }

    public void setFirstBonusEarning(BigDecimal firstBonusEarning) {
        this.firstBonusEarning = firstBonusEarning;
    }

    public BigDecimal getSecondBonusEarning() {
        return secondBonusEarning;
    }

    public void setSecondBonusEarning(BigDecimal secondBonusEarning) {
        this.secondBonusEarning = secondBonusEarning;
    }

    public BigDecimal getSupplierBalance() {
        return supplierBalance;
    }

    public void setSupplierBalance(BigDecimal supplierBalance) {
        this.supplierBalance = supplierBalance;
    }

    public BigDecimal getTerraceBalance() {
        return terraceBalance;
    }

    public void setTerraceBalance(BigDecimal terraceBalance) {
        this.terraceBalance = terraceBalance;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getReconciliationFreightStatus() {
        return reconciliationFreightStatus;
    }

    public void setReconciliationFreightStatus(String reconciliationFreightStatus) {
        this.reconciliationFreightStatus = reconciliationFreightStatus;
    }

    public String getReconciliationStatus() {
        return reconciliationStatus;
    }

    public void setReconciliationStatus(String reconciliationStatus) {
        this.reconciliationStatus = reconciliationStatus;
    }

    public String getAccountingStatus() {
        return accountingStatus;
    }

    public void setAccountingStatus(String accountingStatus) {
        this.accountingStatus = accountingStatus;
    }

    public String getRefundOperDate() {
        return refundOperDate;
    }

    public void setRefundOperDate(String refundOperDate) {
        this.refundOperDate = refundOperDate;
    }
}
