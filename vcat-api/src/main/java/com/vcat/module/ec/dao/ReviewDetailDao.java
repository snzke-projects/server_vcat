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
    /**
     * 获取订单的评论
     * @param buyerId
     * @param productId
     * @param orderItemId
     * @return
     */
	ReviewDetail getReviewByProduct(@Param(value = "buyerId")String buyerId, @Param(value = "productId")String productId,
			@Param(value = "orderItemId")String orderItemId);

    /**
     * 根据订单添加随机评论
     * @param id 评论ID
     * @param itemId 订单项ID
     * @return 添加评论数量
     */
    int addRandomReviewByItemId(@Param(value = "id")String id, @Param(value = "itemId")String itemId);

    /**
     * 获取订单项评论数量
     * @param id
     * @return
     */
    int getReviewCount(String id);
}
