package com.vcat.module.json.order;

/**
 * Created by Code.Ai on 16/5/22.
 * Description:
 */
public class SellerReviewProductRequest {
    private int rating;         // 分数
    private String reviewText;  // 内容
    private String orderItemId;

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
