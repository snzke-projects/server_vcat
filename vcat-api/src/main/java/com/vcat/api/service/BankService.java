package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.BankDao;
import com.vcat.module.ec.entity.Bank;
@Service
@Transactional(readOnly = true)
public class BankService extends CrudService<Bank> {

	@Autowired
	private BankDao bankDao;

	@Override
	protected CrudDao<Bank> getDao() {
		return bankDao;
	}
}
