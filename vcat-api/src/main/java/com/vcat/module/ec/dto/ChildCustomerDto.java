package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;

public class ChildCustomerDto implements Serializable {

	private String id;
	private String userName;
	private String avatarUrl;
	private String guruName;
	private BigDecimal bonusfund = BigDecimal.ZERO;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAvatarUrl() {
		if(avatarUrl!=null&&avatarUrl.contains("http://")){
			return avatarUrl;
		}else
			return QCloudUtils.createThumbDownloadUrl(avatarUrl,ApiConstants.DEFAULT_AVA_THUMBSTYLE);
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getGuruName() {
		return guruName;
	}
	public void setGuruName(String guruName) {
		this.guruName = guruName;
	}
	public BigDecimal getBonusfund() {
		return bonusfund;
	}
	public void setBonusfund(BigDecimal bonusfund) {
		this.bonusfund = bonusfund;
	}
}
