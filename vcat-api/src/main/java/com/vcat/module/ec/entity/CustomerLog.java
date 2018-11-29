package com.vcat.module.ec.entity;

import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

public class CustomerLog extends DataEntity<CustomerLog>{

	private static final long serialVersionUID = 3750138859551453693L;
	private String customerId;
	private Date operDate;
	private Integer type;
	private String deviceVersion;
	private Integer deviceType;
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Date getOperDate() {
		return operDate;
	}
	public void setOperDate(Date operDate) {
		this.operDate = operDate;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getDeviceVersion() {
		return deviceVersion;
	}
	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}
	public Integer getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

}
