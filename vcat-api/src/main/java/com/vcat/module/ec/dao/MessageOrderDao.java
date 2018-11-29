package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.MessageEarning;
import com.vcat.module.ec.entity.MessageOrder;

import java.util.Map;

@MyBatisDao
public interface MessageOrderDao extends CrudDao<MessageOrder> {
    void read(String shopId);
}
