package com.vcat.module.ec.entity;

import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

public class RatingDetail extends DataEntity<RatingDetail>{

	private static final long serialVersionUID = -2347224118789933327L;

	private Integer rating;//评分
	private String buyerId;//买家id
	private Date submitDate;//评论提交时间
	private String ratingSummaryId;//总评分id
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public Date getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}
	public String getRatingSummaryId() {
		return ratingSummaryId;
	}
	public void setRatingSummaryId(String ratingSummaryId) {
		this.ratingSummaryId = ratingSummaryId;
	}
}
