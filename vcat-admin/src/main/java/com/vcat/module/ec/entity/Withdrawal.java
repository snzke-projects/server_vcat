package com.vcat.module.ec.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.module.core.entity.DataEntity;

public class Withdrawal extends DataEntity<Withdrawal> {
	private static final long serialVersionUID = 452605806259595661L;
	// 未提现
	public final static Integer WITHDRAWAL_STATUS_UNTREATED = 0;
	// 提现完成
	public final static Integer WITHDRAWAL_STATUS_SUCCESS = 1;
	// 提现失败
	public final static Integer WITHDRAWAL_STATUS_FAILURE = 2;
	private String shopId;				// shopId
	private Shop shop;					// 提现所属小店
	private Integer withdrawalStatus;	// 提现状态
	private BigDecimal amount;			// 提现金额
	private Date confirmDate;			// 确认时间
	private String note;				// 备注
	private String shopAccountId;       // 提现账户id
    private Account account;            // 本提现单对应提现账户

	public String getWithdrawalStatusColor(){
		if(0 == withdrawalStatus){
			return "cornflowerblue";
		}else if(1 == withdrawalStatus){
			return "green";
		}else if(2 == withdrawalStatus){
			return "red";
		}else{
			return "black";
		}
	}
	public String getWithdrawalStatusLabel(){
		if(WITHDRAWAL_STATUS_UNTREATED == withdrawalStatus){
			return "未处理";
		}else if(WITHDRAWAL_STATUS_SUCCESS == withdrawalStatus){
			return "提现完成";
		}else if(WITHDRAWAL_STATUS_FAILURE == withdrawalStatus){
			return "提现失败";
		}else{
			return "未知状态";
		}
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public Integer getWithdrawalStatus() {
		return withdrawalStatus;
	}
	public void setWithdrawalStatus(Integer withdrawalStatus) {
		this.withdrawalStatus = withdrawalStatus;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Integer getWITHDRAWAL_STATUS_UNTREATED() {
		return WITHDRAWAL_STATUS_UNTREATED;
	}

	public Integer getWITHDRAWAL_STATUS_SUCCESS() {
		return WITHDRAWAL_STATUS_SUCCESS;
	}

	public Integer getWITHDRAWAL_STATUS_FAILURE() {
		return WITHDRAWAL_STATUS_FAILURE;
	}
	public String getShopAccountId() {
		return shopAccountId;
	}
	public void setShopAccountId(String shopAccountId) {
		this.shopAccountId = shopAccountId;
	}

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
