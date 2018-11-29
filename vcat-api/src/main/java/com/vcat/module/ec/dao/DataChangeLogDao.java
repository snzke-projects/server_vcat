package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.DataChangeLog;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface DataChangeLogDao extends CrudDao<DataChangeLog> {
    /**
     * 获取变更日志
     * @param dataChangeLog
     * @return
     */
    List<Map<String,Object>> findLog(DataChangeLog dataChangeLog);
}
