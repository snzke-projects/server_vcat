package com.vcat.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.RatingSummaryDao;
import com.vcat.module.ec.entity.RatingDetail;
import com.vcat.module.ec.entity.RatingSummary;
import com.vcat.module.ec.entity.ReviewDetail;
@Service
@Transactional(readOnly = true)
public class RatingSummaryService extends CrudService<RatingSummary> {

	@Autowired
	private RatingSummaryDao ratingSummaryDao;
	@Autowired
	private RatingDetailService ratingDetailService;
	@Autowired
	private ReviewDetailService reviewDetailService;
	@Override
	protected CrudDao<RatingSummary> getDao() {
		return ratingSummaryDao;
	}
	@Transactional(readOnly = false)
	public void saveReview(String buyerId, String productId,
			String orderItemId, Integer rating, String reviewText) {
		//insert评分
		RatingDetail rate = new RatingDetail();
		rate.preInsert();
		rate.setBuyerId(buyerId);
		rate.setRating(rating);
		rate.setRatingSummaryId(productId);
		ratingDetailService.insert(rate);
		//insert评价内容
		ReviewDetail review = new ReviewDetail();
		review.setId(rate.getId());
		review.setBuyerId(buyerId);
		review.setRatingSummaryId(productId);
		review.setOrderItemId(orderItemId);
		review.setReviewText(reviewText);
		reviewDetailService.insert(review);
		//更新总评分
		ratingSummaryDao.updateRating(productId);
	}
	public List<Map<String, Object>> getReviewList(String productId,Pager page) {
		List<Map<String, Object>> list = ratingSummaryDao.getReviewList(productId,page);
		return list;
	}
	public int countReviewList(String productId) {
		return ratingSummaryDao.countReviewList(productId);
	}

}
