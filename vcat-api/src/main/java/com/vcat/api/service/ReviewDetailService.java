package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ReviewDetailDao;
import com.vcat.module.ec.entity.ReviewDetail;
@Service
@Transactional(readOnly = true)
public class ReviewDetailService extends CrudService<ReviewDetail> {

	@Autowired
	private ReviewDetailDao reviewDetailDao;
	@Override
	protected CrudDao<ReviewDetail> getDao() {
		return reviewDetailDao;
	}
	
	public ReviewDetail getReviewByProduct(String buyerId, String productId,
			String orderItemId) {
		return reviewDetailDao.getReviewByProduct(buyerId,productId,orderItemId);
	}

}
