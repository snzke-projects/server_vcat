package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.InvitationCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface InvitationCodeDao extends CrudDao<InvitationCode> {
    /**
     * 获取未使用的邀请码ID
     * @return
     */
    String getUnusedCodeId();

    /**
     * 标记该邀请码已使用
     * @param codeId
     */
    void updateCodeIsUseful(String codeId);

    /**
     * 批量插入邀请码
     * @param codeList
     */
    void batchInsert(@Param(value = "codeList") List<String> codeList);

    /**
     * 启用/停用邀请码
     * @param invitationCode
     */
    void stopCode(InvitationCode invitationCode);

    /**
     * 获取邀请码ID是否已使用标识
     * @param id
     * @return
     */
    Integer getUsedById(String id);
}
