package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ReviewDetailDao;
import com.vcat.module.ec.entity.ReviewDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReviewDetailService extends CrudService<ReviewDetailDao, ReviewDetail> {

    @Transactional(readOnly = false)
    public void display(ReviewDetail reviewDetail) {
        dao.display(reviewDetail);
    }
}
