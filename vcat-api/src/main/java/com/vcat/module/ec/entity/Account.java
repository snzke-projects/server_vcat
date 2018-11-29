package com.vcat.module.ec.entity;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;

/**
 * 店铺提现账户
 */
public class Account extends DataEntity<Account> {
	private static final long serialVersionUID = 1L;
	private String shopId;
	private String accountNumber;	// 帐号
	private String accountName;		// 账户名
	private Bank bank;				// 银行
	private String branchName;		// 开户行支行名称
	private boolean active;			// 是否激活
	private String city;			// 城市
	private String province;		// 省份
	private Gateway gateWay;		// 账户类型
	private String isDefault;
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public Gateway getGateWay() {
		return gateWay;
	}
	public void setGateWay(Gateway gateWay) {
		this.gateWay = gateWay;
	}
	public Bank getBank() {
		return bank;
	}
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getBankAddress() {
        if(StringUtils.isEmpty(province)
                && StringUtils.isEmpty(city)
                && StringUtils.isEmpty(branchName)){
            return "无";
        }
		return province + "-" + city + "-" + branchName;
	}
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
}
