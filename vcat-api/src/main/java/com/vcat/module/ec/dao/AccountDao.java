package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Account;

@MyBatisDao
public interface AccountDao extends CrudDao<Account> {

	void updateDefault(String shopId);

}
