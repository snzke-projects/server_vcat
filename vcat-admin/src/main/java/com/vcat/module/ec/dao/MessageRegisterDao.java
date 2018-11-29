package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.MessageRegister;

@MyBatisDao
public interface MessageRegisterDao extends CrudDao<MessageRegister> {
    Integer activate(MessageRegister messageRegister);
}

