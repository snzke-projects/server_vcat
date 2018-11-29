package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.RatingSummary;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface RatingSummaryDao extends CrudDao<RatingSummary> {
    /**
     * 更新商品总评分
     * @param productId
     */
    void updateRating(String productId);
    /**
     * 添加随机评分
     * @param id
     * @param customerId
     * @param productId
     */
    int addSummary(@Param(value = "id")String id,
                    @Param(value = "customerId")String customerId,
                    @Param(value = "productId")String productId);
}
