package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.CouponFund;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by ylin on 2015/10/22.
 */
@MyBatisDao
public interface ChatDao {
    boolean isMessageLogged(String id);
    void insertChatLogs(Map<String, Object> params);
    void insertGroup(Map<String, Object> params);
    void insertGroupCustomer(Map<String, Object> params);
    int modifyChatGroups(Map<String, Object> params);
    int modifyGroupAvatar(Map<String, Object> params);
    void deleteGroupsCustomer(String id);
    void deleteGroups(String id);
    List<Map<String, Object>> getGroupUsers(String gid);
    Map<String, Object> getGroupInfo(String gid);
    List<Map<String, Object>> getJoinGroups(String cid);
    void deleteUserFromGroup(Map<String, Object> params);
    List<String> getBannedList(String gid);
    Boolean isOwner(Map<String, Object> params);
    int banUser(Map<String, Object> params);
    int unbanUser(Map<String, Object> params);
    List<Map<String, Object>> getMemberList(String pid);
    Map<String, Object> getMemberInfo(String cid);
    Map<String, Object> getParentInfo(String cid);
    List<Map<String, Object>> getReferrers();
    List<Map<String, Object>> getGroupsByOwner(Map<String, Object> params);
    Integer findGroupUserById(Map<String, Object> params);
    void updateAlias(Map<String, Object> params);
    String getGroupOwner(String groupId);
    String getAlias(Map<String, Object> params);
    Map<String, Object> getShopInfo(String cid);
    List<String> getParentGroups(String cid);
    boolean isExceeded(String groupid);
    int getCurrentSize(String groupid);
    int isTeamMember(@Param("cid") String cid, @Param("pid") String pid );
    Map<String,Object> getDetail(@Param("recommendId")String recommendId);
}
