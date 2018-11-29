package com.vcat.module.ec.entity;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付日志
 */
public class PaymentLog extends DataEntity<PaymentLog> {
	private static final long serialVersionUID = 1L;
	private Payment payment;				//支付单
	private Customer customer;			// 支付账户
	private String deviceToken;			// 设备唯一标识
	private BigDecimal amount;			// 支付金额
	private String transactionNo;		// 支付平台交易号
	private String transactionSuccess;	// 支付状态
	private Date transactionDate;		// 支付日期
	private String note;				//记录
	private String gatewayCode;			//第三方支付类型
	private String gateway;				// 支付方式
	public String getColor(){
		if("1".equals(transactionSuccess)){
			return "green";
		}else{
			return "red";
		}
	}
	public String getLabel(){
        switch (transactionSuccess){
            case "1" : return "支付成功";
            case "3" : return "创建支付单";
            default : return "支付失败";
        }
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getTransactionStatus() {
		return StringUtils.isNotEmpty(transactionSuccess) && PaymentLog.ACTIVATED.equals(transactionSuccess) ? "已支付" : "未支付";
	}
	public String getTransactionSuccess() {
		return transactionSuccess;
	}
	public void setTransactionSuccess(String transactionSuccess) {
		this.transactionSuccess = transactionSuccess;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getTransactionNo() {
		return transactionNo;
	}
	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}
	public Payment getPayment() {
		return payment;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getGatewayCode() {
		return gatewayCode;
	}
	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
}
