package com.vcat.module.ec.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

public class CouponOperLog extends DataEntity<CouponOperLog>{
	private static final long serialVersionUID = -5858475173327154210L;
	private String couponFundId;
	private CouponFund couponFund;
	private BigDecimal fund;//操作金额
	private BigDecimal remainFund;//操作后余额
	private String note;	//日志备注
	private String operBy;	//操作人
	private Date operDate;	//操作时间
	private String type;//1 上家邀请劵 2下家邀请劵 3消费
	private String orderId;//订单号
	private String couponId;//劵id
	public String getCouponFundId() {
		return couponFundId;
	}
	public void setCouponFundId(String couponFundId) {
		this.couponFundId = couponFundId;
	}
	public BigDecimal getFund() {
		return fund;
	}
	public void setFund(BigDecimal fund) {
		this.fund = fund;
	}
	public BigDecimal getRemainFund() {
		return remainFund;
	}
	public void setRemainFund(BigDecimal remainFund) {
		this.remainFund = remainFund;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getOperBy() {
		return operBy;
	}
	public void setOperBy(String operBy) {
		this.operBy = operBy;
	}
	public Date getOperDate() {
		return operDate;
	}
	public void setOperDate(Date operDate) {
		this.operDate = operDate;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getCouponId() {
		return couponId;
	}
	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public CouponFund getCouponFund() {
		return couponFund;
	}
	public void setCouponFund(CouponFund couponFund) {
		this.couponFund = couponFund;
	}
}
