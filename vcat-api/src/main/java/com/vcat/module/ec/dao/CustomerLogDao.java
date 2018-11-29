package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.CustomerLog;

@MyBatisDao
public interface CustomerLogDao extends CrudDao<CustomerLog> {

}
