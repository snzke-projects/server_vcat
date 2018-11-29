package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 第三方登录
 */
public class ThirdPartyLogin extends DataEntity<ThirdPartyLogin> {
	private static final long serialVersionUID = 1L;
	private ThirdLoginType thirdLoginType;	// 登录类型
	private String name;	// 第三方登录名称
	private Customer customer;//用户
	private String uniqueId; //第三方登录唯一标识
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public ThirdLoginType getThirdLoginType() {
		return thirdLoginType;
	}
	public void setThirdLoginType(ThirdLoginType thirdLoginType) {
		this.thirdLoginType = thirdLoginType;
	}
	
}
