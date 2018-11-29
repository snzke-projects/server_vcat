package com.vcat.module.ec.dto;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;

import java.io.Serializable;

public class ShopBgImageDto implements Serializable {

	private String  imageId;
	private String bgUrl;
	private Boolean bindStatus;

	public String getBgUrl() {
		return QCloudUtils.createOriginalDownloadUrl(bgUrl);
	}

	public void setBgUrl(String bgUrl) {
		this.bgUrl = bgUrl;
	}

	public Boolean getBindStatus() {
		return bindStatus;
	}

	public void setBindStatus(Boolean bindStatus) {
		this.bindStatus = bindStatus;
	}

	public String getThumbBgUrl() {
		return QCloudUtils.createThumbDownloadUrl(bgUrl,
				ApiConstants.DEFAULT_BG_THUMBSTYLE);
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
}
