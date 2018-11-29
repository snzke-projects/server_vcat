package com.vcat.module.json.order;

import java.util.List;

/**
 * Created by Code.Ai on 16/5/24.
 * Description:
 */
public class ReviewList {
    List<SellerReviewProductRequest> reviewList ;

    public List<SellerReviewProductRequest> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<SellerReviewProductRequest> reviewList) {
        this.reviewList = reviewList;
    }
}
