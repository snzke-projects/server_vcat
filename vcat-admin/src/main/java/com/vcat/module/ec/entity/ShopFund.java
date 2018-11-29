package com.vcat.module.ec.entity;

import java.math.BigDecimal;

import com.vcat.module.core.entity.DataEntity;

/**
 * 店铺资金
 */
public class ShopFund extends DataEntity<ShopFund> {
	private static final long serialVersionUID = 1L;
	//TODO 店铺资金属性
	private BigDecimal loadProcessingFund;//上架提现中资金
	private BigDecimal loadAvailableFund;//上架可提现资金
	private BigDecimal loadUsedFund;//上架已提现
	private BigDecimal saleHoldFund;//销售待确认资金
	private BigDecimal saleProcessingFund;//销售提现中资金
	private BigDecimal saleAvailableFund;//销售可提现资金
	private BigDecimal saleUsedFund;//销售已提现资金
	private BigDecimal shareProcessingFund;//上架提现中资金
	private BigDecimal shareAvailableFund;//上架可提现资金
	private BigDecimal shareUsedFund;//上架已提现
	//2015年6月30日13:34:50新加分红收入 cw
	private BigDecimal bonusHoldFund;//分红待确认资金
	private BigDecimal bonusProcessingFund;//分红提现中资金
	private BigDecimal bonusAvailableFund;//分红可提现资金
	private BigDecimal bonusUsedFund;//分红已提现资金
	//2015年7月7日14:31:21 新加邀请收入 暂时不使用
	private BigDecimal inviteProcessingFund;//邀请提现中资金
	private BigDecimal inviteAvailableFund;//邀请可提现资金
	private BigDecimal inviteUsedFund;//邀请已提现
	
	//2015年7月31日 新加入邀请券收入
	private BigDecimal couponAvailableFund;
	private BigDecimal couponUsedFund;
	private BigDecimal couponTotalFund;
	//附加查询
	private BigDecimal monthSaleTotalFund;//本月销售总收入
	private BigDecimal monthBonusTotalFund;//本月分红总收入
	private BigDecimal monthShareTotalFund;//本月分享总收入
	private BigDecimal monthLoadTotalFund;//本月上架总收入
	private BigDecimal monthTotalFund;//本月总收入
	private Integer monthTotalShareCount;//本月累计分享次数
	private Integer totalShopProductCount;//小店所有商品个数
	private BigDecimal totalLoadAward;//累计上架奖励
	private BigDecimal totalSale;//累计销售额
	private BigDecimal totalFund;//总收入
	private BigDecimal totalAvailableFund;
	private BigDecimal totalUsedFund;
	private BigDecimal totalProcessingFund;
	

	public ShopFund(BigDecimal defaltVale) {
		this.loadProcessingFund = defaltVale;
		this.loadAvailableFund = defaltVale;
		this.loadUsedFund = defaltVale;
		this.saleProcessingFund = defaltVale;
		this.saleAvailableFund = defaltVale;
		this.saleUsedFund = defaltVale;
		this.saleHoldFund = defaltVale;
		this.bonusProcessingFund = defaltVale;
		this.bonusAvailableFund = defaltVale;
		this.bonusUsedFund = defaltVale;
		this.bonusHoldFund = defaltVale;
		this.shareProcessingFund = defaltVale;
		this.shareAvailableFund = defaltVale;
		this.shareUsedFund = defaltVale;
		this.inviteProcessingFund = defaltVale;
		this.inviteAvailableFund = defaltVale;
		this.inviteUsedFund = defaltVale;
		this.monthSaleTotalFund = defaltVale;
		this.monthShareTotalFund = defaltVale;
		this.monthLoadTotalFund = defaltVale;
		this.monthTotalFund = defaltVale;
		this.monthTotalShareCount = 0;
		this.totalShopProductCount = 0;
		this.totalLoadAward = defaltVale;
		this.totalSale = defaltVale;
		this.totalFund = defaltVale;
		this.totalAvailableFund = defaltVale;
		this.totalUsedFund = defaltVale;
		this.totalProcessingFund = defaltVale;
	}
	public ShopFund(){
		
	}
	public BigDecimal getLoadProcessingFund() {
		return loadProcessingFund;
	}
	public void setLoadProcessingFund(BigDecimal loadProcessingFund) {
		this.loadProcessingFund = loadProcessingFund;
	}
	public BigDecimal getLoadAvailableFund() {
		return loadAvailableFund;
	}
	public void setLoadAvailableFund(BigDecimal loadAvailableFund) {
		this.loadAvailableFund = loadAvailableFund;
	}
	public BigDecimal getLoadUsedFund() {
		return loadUsedFund;
	}
	public void setLoadUsedFund(BigDecimal loadUsedFund) {
		this.loadUsedFund = loadUsedFund;
	}
	public BigDecimal getSaleHoldFund() {
		return saleHoldFund;
	}
	public void setSaleHoldFund(BigDecimal saleHoldFund) {
		this.saleHoldFund = saleHoldFund;
	}
	public BigDecimal getSaleProcessingFund() {
		return saleProcessingFund;
	}
	public void setSaleProcessingFund(BigDecimal saleProcessingFund) {
		this.saleProcessingFund = saleProcessingFund;
	}
	public BigDecimal getSaleAvailableFund() {
		return saleAvailableFund;
	}
	public void setSaleAvailableFund(BigDecimal saleAvailableFund) {
		this.saleAvailableFund = saleAvailableFund;
	}
	public BigDecimal getSaleUsedFund() {
		return saleUsedFund;
	}
	public void setSaleUsedFund(BigDecimal saleUsedFund) {
		this.saleUsedFund = saleUsedFund;
	}
	public BigDecimal getShareProcessingFund() {
		return shareProcessingFund;
	}
	public void setShareProcessingFund(BigDecimal shareProcessingFund) {
		this.shareProcessingFund = shareProcessingFund;
	}
	public BigDecimal getShareAvailableFund() {
		return shareAvailableFund;
	}
	public void setShareAvailableFund(BigDecimal shareAvailableFund) {
		this.shareAvailableFund = shareAvailableFund;
	}
	public BigDecimal getShareUsedFund() {
		return shareUsedFund;
	}
	public void setShareUsedFund(BigDecimal shareUsedFund) {
		this.shareUsedFund = shareUsedFund;
	}
	public BigDecimal getMonthSaleTotalFund() {
		return monthSaleTotalFund;
	}
	public void setMonthSaleTotalFund(BigDecimal monthSaleTotalFund) {
		this.monthSaleTotalFund = monthSaleTotalFund;
	}
	public BigDecimal getMonthShareTotalFund() {
		return monthShareTotalFund;
	}
	public void setMonthShareTotalFund(BigDecimal monthShareTotalFund) {
		this.monthShareTotalFund = monthShareTotalFund;
	}
	public Integer getMonthTotalShareCount() {
		return monthTotalShareCount;
	}
	public void setMonthTotalShareCount(Integer monthTotalShareCount) {
		this.monthTotalShareCount = monthTotalShareCount;
	}
	public BigDecimal getTotalLoadAward() {
		return totalLoadAward;
	}
	public void setTotalLoadAward(BigDecimal totalLoadAward) {
		this.totalLoadAward = totalLoadAward;
	}
	public BigDecimal getTotalSale() {
		return totalSale;
	}
	public void setTotalSale(BigDecimal totalSale) {
		this.totalSale = totalSale;
	}
	public Integer getTotalShopProductCount() {
		return totalShopProductCount;
	}
	public void setTotalShopProductCount(Integer totalShopProductCount) {
		this.totalShopProductCount = totalShopProductCount;
	}
	public BigDecimal getMonthLoadTotalFund() {
		return monthLoadTotalFund;
	}
	public void setMonthLoadTotalFund(BigDecimal monthLoadTotalFund) {
		this.monthLoadTotalFund = monthLoadTotalFund;
	}
	//计算总收入
	public void countTotalFund() {
		this.setTotalFund(loadProcessingFund.add(loadAvailableFund)
                .add(loadUsedFund)
                .add(saleProcessingFund).add(saleAvailableFund)
                .add(saleUsedFund).add(shareProcessingFund)
                .add(shareAvailableFund).add(shareUsedFund)
                .add(bonusProcessingFund).add(bonusAvailableFund)
                .add(bonusUsedFund).add(inviteProcessingFund).add(inviteAvailableFund)
                .add(inviteUsedFund));
	}
	//计算本月总收入
	public void countMonthTotalFund(){
		monthSaleTotalFund = monthSaleTotalFund == null ? new BigDecimal(0)
				: monthSaleTotalFund;
		monthShareTotalFund = monthShareTotalFund == null ? new BigDecimal(0)
				: monthShareTotalFund;
		monthLoadTotalFund = monthLoadTotalFund == null ? new BigDecimal(0)
				: monthLoadTotalFund;
		monthBonusTotalFund = monthBonusTotalFund == null ? new BigDecimal(0)
		: monthBonusTotalFund;
		this.setMonthTotalFund(monthSaleTotalFund.add(monthShareTotalFund)
                .add(monthLoadTotalFund).add(monthBonusTotalFund));
	}
	//计算总可用收入
	public void countTotalAvailableFund(){
		this.setTotalAvailableFund(loadAvailableFund.add(saleAvailableFund)
                .add(shareAvailableFund).add(bonusAvailableFund).add(inviteAvailableFund));
	}
	//计算总已提现收入
	public void countTotalUseDFund(){
		this.setTotalUsedFund(loadUsedFund.add(saleUsedFund)
                .add(shareUsedFund).add(bonusUsedFund).add(inviteUsedFund));
	}
	//计算总提现中收入
	public void countTotalProcessingFund(){
		this.setTotalProcessingFund (loadProcessingFund.add(saleProcessingFund)
									.add(shareProcessingFund).add(bonusProcessingFund).add(inviteProcessingFund));
	}
	public BigDecimal getTotalFund() {
		return totalFund;
	}
	public void setTotalFund(BigDecimal totalFund) {
		this.totalFund = totalFund;
	}
	public BigDecimal getMonthTotalFund() {
		return monthTotalFund;
	}
	public void setMonthTotalFund(BigDecimal monthTotalFund) {
		this.monthTotalFund = monthTotalFund;
	}
	public BigDecimal getTotalAvailableFund() {
		return totalAvailableFund;
	}
	public void setTotalAvailableFund(BigDecimal totalAvailableFund) {
		this.totalAvailableFund = totalAvailableFund;
	}
	public BigDecimal getTotalUsedFund() {
		return totalUsedFund;
	}
	public void setTotalUsedFund(BigDecimal totalUsedFund) {
		this.totalUsedFund = totalUsedFund;
	}
	public BigDecimal getTotalProcessingFund() {
		return totalProcessingFund;
	}
	public void setTotalProcessingFund(BigDecimal totalProcessingFund) {
		this.totalProcessingFund = totalProcessingFund;
	}
	public BigDecimal getMonthBonusTotalFund() {
		return monthBonusTotalFund;
	}
	public void setMonthBonusTotalFund(BigDecimal monthBonusTotalFund) {
		this.monthBonusTotalFund = monthBonusTotalFund;
	}
	public BigDecimal getBonusHoldFund() {
		return bonusHoldFund;
	}
	public void setBonusHoldFund(BigDecimal bonusHoldFund) {
		this.bonusHoldFund = bonusHoldFund;
	}
	public BigDecimal getBonusProcessingFund() {
		return bonusProcessingFund;
	}
	public void setBonusProcessingFund(BigDecimal bonusProcessingFund) {
		this.bonusProcessingFund = bonusProcessingFund;
	}
	public BigDecimal getBonusAvailableFund() {
		return bonusAvailableFund;
	}
	public void setBonusAvailableFund(BigDecimal bonusAvailableFund) {
		this.bonusAvailableFund = bonusAvailableFund;
	}
	public BigDecimal getBonusUsedFund() {
		return bonusUsedFund;
	}
	public void setBonusUsedFund(BigDecimal bonusUsedFund) {
		this.bonusUsedFund = bonusUsedFund;
	}
	public BigDecimal getCouponAvailableFund() {
		return couponAvailableFund;
	}
	public void setCouponAvailableFund(BigDecimal couponAvailableFund) {
		this.couponAvailableFund = couponAvailableFund;
	}
	public BigDecimal getCouponUsedFund() {
		return couponUsedFund;
	}
	public void setCouponUsedFund(BigDecimal couponUsedFund) {
		this.couponUsedFund = couponUsedFund;
	}
	public BigDecimal getCouponTotalFund() {
		return couponTotalFund;
	}
	public void setCouponTotalFund(BigDecimal couponTotalFund) {
		this.couponTotalFund = couponTotalFund;
	}
}
