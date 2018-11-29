package com.vcat.module.ec.entity;

import java.math.BigDecimal;

import com.vcat.module.core.entity.DataEntity;

public class CouponFund extends DataEntity<CouponFund>{
	private static final long serialVersionUID = -4955899489189052833L;
	
	private Shop shop;//店铺
	private BigDecimal availableFund = BigDecimal.ZERO;//可用劵金额
	private BigDecimal usedFund = BigDecimal.ZERO;//已用劵金额
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	public BigDecimal getAvailableFund() {
		return availableFund;
	}
	public void setAvailableFund(BigDecimal availableFund) {
		this.availableFund = availableFund;
	}
	public BigDecimal getUsedFund() {
		return usedFund;
	}
	public void setUsedFund(BigDecimal usedFund) {
		this.usedFund = usedFund;
	}
}
