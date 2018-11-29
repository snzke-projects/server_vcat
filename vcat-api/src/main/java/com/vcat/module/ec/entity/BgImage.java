package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;


public class BgImage extends DataEntity<BgImage>{
	private static final long serialVersionUID = 1L;
	private String name;
	private String url;
	private String displayOrder;
    private Integer isActivate;
	public String getUrlPath(){
		return QCloudUtils.createOriginalDownloadUrl(url);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(String displayOrder) {
		this.displayOrder = displayOrder;
	}

    public Integer getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(Integer isActivate) {
        this.isActivate = isActivate;
    }
}
