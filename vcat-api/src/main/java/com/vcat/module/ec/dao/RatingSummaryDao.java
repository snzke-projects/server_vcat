package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.RatingSummary;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface RatingSummaryDao extends CrudDao<RatingSummary> {
	//更新总平均分
	void updateRating(String productId);
	//获取评价列表
	List<Map<String, Object>> getReviewList(@Param("productId")String productId,@Param("page") Pager page);
	int countReviewList(String productId);

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
