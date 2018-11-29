package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;


public class HeadImage extends DataEntity<HeadImage>{
	private static final long serialVersionUID = 1L;
	private String name;		// 图片名称
	private String url;			// 图片url
	private String displayOrder;// 图片排序
	private String isActivate;	// 是否激活
	private String type;        // 1 首页轮播图 2 V猫商场轮播图
	private String pageUrl;     // 链接地址
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
	public String getIsActivate() {
		return isActivate;
	}
	public void setIsActivate(String isActivate) {
		this.isActivate = isActivate;
	}

	public String getActivateLabel(){
		if (ACTIVATED.equals(isActivate)){
			return "已激活";
		}else if(NOT_ACTIVATED.equals(isActivate)){
			return "未激活";
		}else{
			return "未知状态";
		}
	}

	public String getActivateColor(){
		if (ACTIVATED.equals(isActivate)){
			return "green";
		}else if(NOT_ACTIVATED.equals(isActivate)){
			return "red";
		}else{
			return "black";
		}
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
}
