package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.OfflineActivityDto;
import com.vcat.module.ec.entity.ActivityOffline;
import com.vcat.module.ec.entity.Participation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 活动
 */
@MyBatisDao
public interface ActivityOfflineDao extends CrudDao<ActivityOffline> {
    Integer activate(ActivityOffline activityOffline);
    Integer changeOpenStatus(ActivityOffline activityOffline);
    Map<String, Object> findTopActivity();
    /**
     * 获取参加活动人员列表
     * @param activityOffline
     * @return
     */
    List<Participation> findParticipationList(ActivityOffline activityOffline);

    //根据ID查询ActivityOffline
    ActivityOffline getOfflineActivity(@Param("offlineActivityId")String offlineActivityId);
    //判断用户是否参加活动
    boolean isJoin(@Param("offlineActivityId")String offlineActivityId, @Param("cid")String cid);
    //判断活动是否进行
    //boolean inProgress(@Param("offlineActivityId")String offlineActivityId);
    //判断名额是否已满
    String getSeatStatus(@Param("offlineActivityId")String offlineActivityId);


    List<OfflineActivityDto> getOfflineActivityList(@Param("page")Pager page, @Param("cid")String cid);
    int countOfflineActivityList();
    //修改线下活动开启状态
    void updateStatus(@Param("id")String id);
}

