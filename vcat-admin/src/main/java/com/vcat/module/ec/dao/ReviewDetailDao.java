package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ReviewDetail;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface ReviewDetailDao extends CrudDao<ReviewDetail> {

    /**
     * 显示或隐藏评论
     * @param reviewDetail
     */
    void display(ReviewDetail reviewDetail);

    /**
     * 添加随机评论
     * @param id
     * @param customerId
     * @param productId
     */
    void addReview(@Param(value = "id")String id,
                   @Param(value = "customerId")String customerId,
                   @Param(value = "productId")String productId);
}
