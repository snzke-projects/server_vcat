package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 电话
 */
public class Phone extends DataEntity<Phone> {
	private static final long serialVersionUID = 1L;
	private boolean active;		// 是否激活
	private boolean def;		// 是否默认
	private String phoneNumber;	// 电话号码
	private String phoneName;	// 电话名称
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isDef() {
		return def;
	}
	public void setDef(boolean def) {
		this.def = def;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPhoneName() {
		return phoneName;
	}
	public void setPhoneName(String phoneName) {
		this.phoneName = phoneName;
	}
	
}
