package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RefundDto implements Serializable {
	private String refundId;
	private String orderNum;
	private String orderItemId;
	private BigDecimal amount;
	private String reason;
	private String productName;
	private String itemName;
	private int reQuantity;
	private String note;
	private Date createTime;
	private String expressName;
	private String expressCode;
	private String expressNum;
	private String allStatus;
	private String refundStatus;
	private String returnStatus;
	private String isAddRefundShipping;
	private String iscancelRefund;
	private String isUpdateReason;
	private String shippingStatus;
	private BigDecimal totalPrice;
	private String phoneNumber;
	private String hasFreightPrice; // 退款金额是否包含快递费
	private Integer receiptType;

	public BigDecimal getRefund() {
		return amount;
	}

	public Integer getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(Integer receiptType) {
		this.receiptType = receiptType;
	}

	public String getHasFreightPrice() {
		return hasFreightPrice;
	}

	public void setHasFreightPrice(String hasFreightPrice) {
		this.hasFreightPrice = hasFreightPrice;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getShippingStatus() {
		return shippingStatus;
	}

	public void setShippingStatus(String shippingStatus) {
		this.shippingStatus = shippingStatus;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getRefundId() {
		return refundId;
	}
	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getReQuantity() {
		return reQuantity;
	}
	public void setReQuantity(int reQuantity) {
		this.reQuantity = reQuantity;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getExpressName() {
		return expressName;
	}
	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}
	public String getExpressCode() {
		return expressCode;
	}
	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
	}
	public String getExpressNum() {
		return expressNum;
	}
	public void setExpressNum(String expressNum) {
		this.expressNum = expressNum;
	}
	public String getAllStatus() {
		return allStatus;
	}
	public void setAllStatus(String allStatus) {
		this.allStatus = allStatus;
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
	public String getIsAddRefundShipping() {
		return isAddRefundShipping;
	}
	public void setIsAddRefundShipping(String isAddRefundShipping) {
		this.isAddRefundShipping = isAddRefundShipping;
	}
	public String getIscancelRefund() {
		return iscancelRefund;
	}
	public void setIscancelRefund(String iscancelRefund) {
		this.iscancelRefund = iscancelRefund;
	}
	public String getIsUpdateReason() {
		return isUpdateReason;
	}
	public void setIsUpdateReason(String isUpdateReason) {
		this.isUpdateReason = isUpdateReason;
	}
	public String getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}
}
