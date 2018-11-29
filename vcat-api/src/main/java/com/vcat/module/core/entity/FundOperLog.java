package com.vcat.module.core.entity;

import com.vcat.module.ec.entity.FundFieldType;

import java.math.BigDecimal;

public class FundOperLog extends DataEntity<FundOperLog> {
	private static final long serialVersionUID = 1L;
	public static final int NORMAL_INCOME = 1;  // 正常收入
	public static final int WITHDRAWALS = 2;    // 提现
	public static final int RETURN = 3;         // 退款
	public static final int TEAM_RED_PACKETS = 4;// 退款
	public static final int SECOND_BONUS = 6;   // 二级分红
	public static final int UPGRADE_BONUS = 7;  // 伯乐奖励
	public static final int SETTLEMENT_TEAM_BONUS = 8;  // 结算分红收入
	private String shopFundId;              // 店铺ID
	private FundFieldType fundFieldType;    // 资金操作类型值
	private BigDecimal fund;                // 操作金额
	private BigDecimal remainFund;          // 操作后金额
	private String note;	                // 日志备注
	private String relateId;                // 关联id(订单ID，分享日志ID，提现ID，退款ID，伯乐奖励申请单ID，分红收入提供者ID)
	private Integer fundType;               // 收入类型

	/**
	 * 实例化资金操作日志对象
	 * @param shopFundId    店铺资金表ID
	 * @param fieldTypeName 操作字段名
	 * @param fund          操作资金
	 * @param remainFund    操作后资金
	 * @param relateId      关联ID
	 * @param fundType      资金操作类型
	 * @param note          资金操作备注
	 * @return
	 */
	public static FundOperLog create(String shopFundId,String fieldTypeName,BigDecimal fund,BigDecimal remainFund,String relateId,int fundType,String note){
		FundOperLog fundOperLog = new FundOperLog();
		fundOperLog.preInsert();
		fundOperLog.setShopFundId(shopFundId);
		fundOperLog.setFundFieldType(new FundFieldType(fieldTypeName));
		fundOperLog.setFund(fund);
		fundOperLog.setRemainFund(remainFund);
		fundOperLog.setRelateId(relateId);
		fundOperLog.setFundType(fundType);
		fundOperLog.setNote(note);
		return fundOperLog;
	}

	public String getShopFundId() {
		return shopFundId;
	}
	public void setShopFundId(String shopFundId) {
		this.shopFundId = shopFundId;
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
	public String getRelateId() {
		return relateId;
	}
	public void setRelateId(String relateId) {
		this.relateId = relateId;
	}
	public FundFieldType getFundFieldType() {
		return fundFieldType;
	}
	public void setFundFieldType(FundFieldType fundFieldType) {
		this.fundFieldType = fundFieldType;
	}
	public Integer getFundType() {
		return fundType;
	}
	public void setFundType(Integer fundType) {
		this.fundType = fundType;
	}

	@Override
	public String toString() {
		return "FundOperLog{" +
				"shopFundId='" + shopFundId + '\'' +
				", fundFieldType=" + fundFieldType +
				", fund=" + fund +
				", remainFund=" + remainFund +
				", note='" + note + '\'' +
				", relateId='" + relateId + '\'' +
				", fundType=" + fundType +
				'}';
	}
}
