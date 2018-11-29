package com.vcat.module.ec.dao;

import com.vcat.common.persistence.annotation.MyBatisDao;

import java.util.Map;

/**
 * Created by ylin on 2015/10/22.
 */
@MyBatisDao
public interface ChatDao {
    boolean isMessageLogged(String id);
    void insertChatLogs(Map<String, Object> params);
}
