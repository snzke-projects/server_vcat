package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

public class ReviewDetail extends DataEntity<ReviewDetail>{
	private static final long serialVersionUID = -1871756419667373588L;
    private Product product;    // 评论所属商品
    private Customer buyer;     // 评论所属买家
    private RatingDetail rating;// 评分
	private String buyerId;     //买家id
	private Date submitDate;    //评论提交时间
	private String reviewText;
	private String ratingSummaryId;//总评分id
	private String orderItemId;
	private String itemName;
    private Integer isDisplay;  // 是否显示评论
    private Integer from;       // 评论来源

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customer getBuyer() {
        return buyer;
    }

    public void setBuyer(Customer buyer) {
        this.buyer = buyer;
    }

    public RatingDetail getRating() {
        return rating;
    }

    public void setRating(RatingDetail rating) {
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
	public String getReviewText() {
		return reviewText;
	}
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	public String getRatingSummaryId() {
		return ratingSummaryId;
	}
	public void setRatingSummaryId(String ratingSummaryId) {
		this.ratingSummaryId = ratingSummaryId;
	}
	public String getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

    public Integer getIsDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(Integer isDisplay) {
        this.isDisplay = isDisplay;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }
}
