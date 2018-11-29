package com.vcat.module.ec.entity;

import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

public class AppDomain extends DataEntity<AppDomain>{
	private static final long serialVersionUID = 1L;
	String version;
	String serviceDomain;
	String type;
	Date createDate;
	String discription;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getServiceDomain() {
		return serviceDomain;
	}
	public void setServiceDomain(String serviceDomain) {
		this.serviceDomain = serviceDomain;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getDiscription() {
		return discription;
	}
	public void setDiscription(String discription) {
		this.discription = discription;
	}
	
}
