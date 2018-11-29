package com.vcat.module.ec.entity;

import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

/**
 * 分享奖励日志
 */
public class ShareEarningLog extends DataEntity<ShareEarningLog>{
    private static final long serialVersionUID = 1L;
	private String shopId;
    private String shareId;
    private Date shareTime;
    private ThirdLoginType thirdLoginType;
  
    public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getShareId() {
		return shareId;
	}
	public void setShareId(String shareId) {
		this.shareId = shareId;
	}
	public Date getShareTime() {
		return shareTime;
	}
	public void setShareTime(Date shareTime) {
		this.shareTime = shareTime;
	}
	public ThirdLoginType getThirdLoginType() {
		return thirdLoginType;
	}
	public void setThirdLoginType(ThirdLoginType thirdLoginType) {
		this.thirdLoginType = thirdLoginType;
	}

}
