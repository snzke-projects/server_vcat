package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.OrderLog;
import com.vcat.module.ec.entity.OrderReserveLog;

@MyBatisDao
public interface OrderLogDao extends CrudDao<OrderLog> {
    void insertReserveLog(OrderReserveLog reserveLog);
}
