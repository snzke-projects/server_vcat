package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BuyerInfoDto implements Serializable {

	private String buyerName;//买家姓名
	private String phoneNum;//买家收货电话号码
	private String avatarUrl;//买家头像
	private String orderCount;//订单数
	private BigDecimal saleFund;//销售收入
	private Date lastBuyDate;//最后购买时间
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(String orderCount) {
		this.orderCount = orderCount;
	}
	public BigDecimal getSaleFund() {
		return saleFund;
	}
	public void setSaleFund(BigDecimal saleFund) {
		this.saleFund = saleFund;
	}
	public Date getLastBuyDate() {
		return lastBuyDate;
	}
	public void setLastBuyDate(Date lastBuyDate) {
		this.lastBuyDate = lastBuyDate;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	
	
}
