package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.module.core.entity.DataEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付单
 */
public class Payment extends DataEntity<Payment> {
	private static final long serialVersionUID = 1L;
	private BigDecimal amount;		// 支付金额
	private Gateway	gateway;		// 账户类型
	private Date paymentDate;		// 支付时间
	private String transactionNo;	// 支付平台交易号
	private String paymentNo;	//支付单号
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Gateway getGateway() {
		return gateway;
	}
	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getTransactionNo() {
		return transactionNo;
	}
	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}
	public String getPaymentNo() {
		return paymentNo;
	}
	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	@Override
	public String toString() {
		return "Payment{" +
				"amount=" + amount +
				", gateway=" + gateway +
				", paymentDate=" + paymentDate +
				", transactionNo='" + transactionNo + '\'' +
				", paymentNo='" + paymentNo + '\'' +
				'}';
	}
}
