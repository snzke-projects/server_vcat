package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Activity;
import com.vcat.module.ec.entity.Order;
import com.vcat.module.ec.entity.Participation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 体验官
 */
@MyBatisDao
public interface ActivityDao extends CrudDao<Activity> {
    Integer activate(Activity activity);

    Map<String, Object> findTopActivity();

    /**
     * 客户端获取 体验官 列表
     * @param activity 体验官查询条件
     * @return
     */
    List<Map<String,Object>> findAppList(Activity activity);

    /**
     * 获取我的体验官列表
     * @param activity
     * @return
     */
    List getActivityOfMy(Activity activity);

    /**
     * 获取参加体验官人员列表
     * @param activity
     * @return
     */
    List<Participation> findParticipationList(Activity activity);

    //判断名额是否已满
	String getSeatStatus(String activityId);

    /**
     * 插入体验官订单
     * @param activityId
     * @param paymentId
     */
    void insertActivityOrder(@Param(value = "activityId")String activityId,@Param(value = "customerId")String customerId,@Param(value = "paymentId")String paymentId);

    Order getActivityOrder(@Param(value = "activityId")String activityId,@Param(value = "customerId")String customerId);

    void deleteActivityCustomer(@Param(value = "activityId")String activityId,@Param(value = "customerId")String customerId);

    Boolean getIsParticipate(@Param(value = "activityId")String activityId,@Param(value = "customerId")String customerId);

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

    String getActivityIdByPayment(@Param("paymentId")String paymentId);
}

