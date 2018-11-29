package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;

/**
 * 物流公司
 */
public class Express extends DataEntity<Express> {
	private static final long serialVersionUID = 1L;
	private String name;	// 公司名称
	private String code;	// 公司代码
	private String logoUrl;	// LOGO图片地址
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public String getLogoUrlPath() {
		return QCloudUtils.createOriginalDownloadUrl(logoUrl);
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
}
