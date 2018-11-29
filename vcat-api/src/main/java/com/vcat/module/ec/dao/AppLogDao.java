package com.vcat.module.ec.dao;

import com.vcat.common.persistence.annotation.MyBatisDao;

import java.util.Map;

/**
 * Created by ylin on 2015/12/09.
 */
@MyBatisDao
public interface AppLogDao {
    void insertLog(Map<String, Object> map);
}
