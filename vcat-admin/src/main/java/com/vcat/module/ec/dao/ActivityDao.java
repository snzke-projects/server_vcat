package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Activity;
import com.vcat.module.ec.entity.Participation;

import java.util.List;
import java.util.Map;

/**
 * 体验官
 */
@MyBatisDao
public interface ActivityDao extends CrudDao<Activity> {
    Integer activate(Activity activity);

    /**
     * 获取参加体验官人员列表
     * @param activity
     * @return
     */
    List<Participation> findParticipationList(Activity activity);

    /**
     * 计算体验官提交报告总人数
     * @param activityId
     * @return
     */
    Integer countReported(String activityId);

    /**
     * 统计体验官问卷报告
     * @param activityId
     * @return
     */
    List<Map<String,String>> statisticsActivityAnswer(String activityId);

    /**
     * 添加体验官问卷关联
     * @param entity
     */
    void insertActivityQuestionnaire(Activity entity);

    /**
     * 更新体验官反馈发布状态
     * @param activityId
     */
    void feedback(String activityId);
}

