package com.vcat.module.ec.dto;

import com.vcat.common.cloud.QCloudUtils;

import java.io.Serializable;

public class AccountDto implements Serializable {

	private String id;
	private String accountNum;
	private String accountType;
	private String city;
	private String province;
	private String branchName;
	private String name;
	private String accountTypeName;
	private String bankName;
	private String isDefault;
	private String minLogo;
	private String maxLogo;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAccountNum() {
		return accountNum;
	}
	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccountTypeName() {
		return accountTypeName;
	}
	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	public String getMinLogo() {
		return QCloudUtils.createOriginalDownloadUrl(minLogo);
	}
	public void setMinLogo(String minLogo) {
		this.minLogo = minLogo;
	}
	public String getMaxLogo() {
		return QCloudUtils.createOriginalDownloadUrl(maxLogo);
	}
	public void setMaxLogo(String maxLogo) {
		this.maxLogo = maxLogo;
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
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
}
