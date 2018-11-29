package com.vcat.module.ec.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ActivityFeedBack;
import com.vcat.module.ec.entity.Question;

@MyBatisDao
public interface ActivityFeedBackDao extends CrudDao<ActivityFeedBack> {

	int countFeedBackList();

	List<ActivityFeedBack> getFeedBackList(@Param("page") Pager page);


    /**
     * 插入问题答案统计报告
     * @param statisticsId
     * @param answer
     * @param count
     */
    void insertStatisticsAnswer(@Param(value = "statisticsId")String statisticsId
            ,@Param(value = "answer")String answer, @Param(value = "count")int count);

    /**
     * 插入统计报告问题
     * @param question
     */
    void insertStatistics(Question question);

    /**
     * 激活统计报告
     * @param feedbackId
     * @param isActivate
     */
    void activate(@Param(value = "id")String feedbackId,
                  @Param(value = "isActivate")Integer isActivate);

    /**
     * 删除统计报告
     * @param feedbackId
     */
    void deleteStatisticsByFeedBackId(String feedbackId);

    /**
     * 删除统计报告
     * @param feedbackId
     */
    void deleteStatisticsAnswerByFeedBackId(String feedbackId);

    /**
     * 获取该报告的统计题目ID
     * @param feedbackId
     * @return
     */
    String getSelectedQuestionId(String feedbackId);
}

