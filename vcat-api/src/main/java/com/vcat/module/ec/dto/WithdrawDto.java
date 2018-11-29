package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class WithdrawDto implements Serializable{


	private static final long serialVersionUID = 1L;
	private Date createDate;
	private BigDecimal amount;
	private int WithdrawalStatus;
	private String WithdrawalStatusName;
	private String payTypeName;
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public int getWithdrawalStatus() {
		return WithdrawalStatus;
	}
	public void setWithdrawalStatus(int withdrawalStatus) {
		WithdrawalStatus = withdrawalStatus;
	}
	public String getPayTypeName() {
		return payTypeName;
	}
	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}
	public String getWithdrawalStatusName() {
		return WithdrawalStatusName;
	}
	public void setWithdrawalStatusName(String withdrawalStatusName) {
		WithdrawalStatusName = withdrawalStatusName;
	}
	
}
