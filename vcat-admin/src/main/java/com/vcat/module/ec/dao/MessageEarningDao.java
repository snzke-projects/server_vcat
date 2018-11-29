package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.MessageEarning;
import com.vcat.module.ec.entity.Refund;

import java.util.Map;

@MyBatisDao
public interface MessageEarningDao extends CrudDao<MessageEarning> {
    Map<String,Object> queryNotReadMessageCount(String shopId);
    void read(String shopId);
}
