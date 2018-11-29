package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

public class ReviewLibrary extends DataEntity<ReviewLibrary>{
	private static final long serialVersionUID = -1871756419667373588L;
    private ProductCategory category;   // 评论所属分类
    private Integer rating;             // 评分
	private String reviewText;          // 评论内容

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
