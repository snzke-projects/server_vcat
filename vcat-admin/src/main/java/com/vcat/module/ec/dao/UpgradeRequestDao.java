package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.UpgradeRequest;
import com.vcat.module.ec.entity.UpgradeRequestLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface UpgradeRequestDao extends CrudDao<UpgradeRequest> {
    /**
     * 为店铺添加白金小店升级申请
     * @param shopId
     */
    void add(@Param(value = "id")String id, @Param(value = "shopId")String shopId);

    /**
     * 审核升级申请
     * @param upgradeRequest
     * @return
     */
    int approval(UpgradeRequest upgradeRequest);

    /**
     * 获取审核申请日志
     * @param upgradeRequest
     * @return
     */
    List<UpgradeRequestLog> findLogs(UpgradeRequest upgradeRequest);

    /**
     * 插入审核申请日志
     * @param upgradeRequestLog
     */
    void insertLog(UpgradeRequestLog upgradeRequestLog);

    /**
     * 将店铺升级为钻石小店
     * @param source
     */
    void upgradeShop(UpgradeRequest source);

    /**
     * 为钻石店铺设置我的邀请码
     * @param shopId
     * @param invitationId
     */
    void setInvitationCode(@Param(value = "shopId") String shopId, @Param(value = "invitationId") String invitationId);
}
