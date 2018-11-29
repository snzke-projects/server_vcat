package com.vcat.module.ec.entity;

import java.math.BigDecimal;

import com.vcat.module.core.entity.DataEntity;

/**
 * 资金操作日志
 */
public class ShopFundLog extends DataEntity<ShopFundLog> {
	private static final long serialVersionUID = 1L;
	private String fundType;			// 操作资金类型(佣金|分享奖励|上架奖励)
	private ShopFundOperType fundOperType;	// 资金操作类型(获取佣金|获取分享奖励|获取上架奖励|卖家退款)
	private BigDecimal before;			// 操作前的金额
	private BigDecimal after;			// 操作后的金额
	public String getFundType() {
		return fundType;
	}
	public void setFundType(String fundType) {
		this.fundType = fundType;
	}
	public ShopFundOperType getFundOperType() {
		return fundOperType;
	}
	public void setFundOperType(ShopFundOperType fundOperType) {
		this.fundOperType = fundOperType;
	}
	public BigDecimal getBefore() {
		return before;
	}
	public void setBefore(BigDecimal before) {
		this.before = before;
	}
	public BigDecimal getAfter() {
		return after;
	}
	public void setAfter(BigDecimal after) {
		this.after = after;
	}
	
}
