package com.vcat.module.ec.dto;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;

import java.io.Serializable;

public class ReviewDto implements Serializable {

	private String productItemId;
	private String productId;
	private String orderItemId;
	private String productName;
	private String itemName;
	private String mainUrl;
	private Integer isReview;
	private Integer rating;
	private String reviewText;
	public String getProductItemId() {
		return productItemId;
	}
	public void setProductItemId(String productItemId) {
		this.productItemId = productItemId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getMainUrl() {
		return QCloudUtils.createThumbDownloadUrl(mainUrl, ApiConstants.DEFAULT_PRODUCT_THUMBSTYLE);
	}
	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}
	public String getReviewText() {
		return reviewText;
	}
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	public Integer getIsReview() {
		return isReview;
	}
	public void setIsReview(Integer isReview) {
		this.isReview = isReview;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
}
