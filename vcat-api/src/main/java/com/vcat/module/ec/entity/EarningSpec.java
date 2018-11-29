package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.math.BigDecimal;

/**
 * 上架提现率
 */
public class EarningSpec extends DataEntity<EarningSpec> {
	private static final long serialVersionUID = 1L;
	private BigDecimal saleVolume;	// 销售额
	private BigDecimal withdrawal;	// 提现额
	public BigDecimal getSaleVolume() {
		return saleVolume;
	}

	public void setSaleVolume(BigDecimal saleVolume) {
		this.saleVolume = saleVolume;
	}

	public BigDecimal getWithdrawal() {
		return withdrawal;
	}

	public void setWithdrawal(BigDecimal withdrawal) {
		this.withdrawal = withdrawal;
	}
}
