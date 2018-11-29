package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ActivityOffline;
import com.vcat.module.ec.entity.Participation;

import java.util.List;

/**
 * 活动
 */
@MyBatisDao
public interface ActivityOfflineDao extends CrudDao<ActivityOffline> {
    Integer activate(ActivityOffline activityOffline);
    Integer changeOpenStatus(ActivityOffline activityOffline);
    /**
     * 获取参加活动人员列表
     * @param activityOffline
     * @return
     */
    List<Participation> findParticipationList(ActivityOffline activityOffline);
}

