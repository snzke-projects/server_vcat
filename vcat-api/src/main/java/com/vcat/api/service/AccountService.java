package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.AccountDao;
import com.vcat.module.ec.entity.Account;
@Service
@Transactional(readOnly = true)
public class AccountService extends CrudService<Account> {

	@Autowired
	private AccountDao AccountDao;
	@Override
	protected CrudDao<Account> getDao() {
		return AccountDao;
	}
	@Transactional(readOnly = false)
	public void updateDefault(String shopId) {
		AccountDao.updateDefault(shopId);
	}

}
