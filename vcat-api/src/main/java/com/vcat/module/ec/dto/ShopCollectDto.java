package com.vcat.module.ec.dto;

import com.vcat.common.cloud.QCloudUtils;

import java.io.Serializable;

public class ShopCollectDto implements Serializable {

	private String shopId;
	private String shopName;
	private String levelLogo;
	private String avatarUrl;
	private String shopNum;
	private String productType;//收藏商品的类别
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getLevelLogo() {
		return QCloudUtils.createOriginalDownloadUrl(levelLogo);
	}
	public void setLevelLogo(String levelLogo) {
		this.levelLogo = levelLogo;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getShopNum() {
		return shopNum;
	}
	public void setShopNum(String shopNum) {
		this.shopNum = shopNum;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}	
}
