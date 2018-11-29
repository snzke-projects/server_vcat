package com.vcat.module.ec.dao;


import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.InviteEarning;
import com.vcat.module.ec.entity.InviteEarningLog;

import java.util.List;

@MyBatisDao
public interface InviteEarningLogDao extends CrudDao<InviteEarningLog> {

    /**
     * 获取日志列表
     * @param inviteEarning
     * @return
     */
    List<InviteEarningLog> findLog(InviteEarning inviteEarning);
}
