package com.vcat.module.ec.dao;


import java.util.List;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.InviteEarning;
import com.vcat.module.ec.entity.InviteEarningLog;

@MyBatisDao
public interface InviteEarningLogDao extends CrudDao<InviteEarningLog> {

   
    /**
     * 获取日志列表
     * @param inviteEarning
     * @return
     */
    List<InviteEarningLog> findLog(InviteEarning inviteEarning);
}
