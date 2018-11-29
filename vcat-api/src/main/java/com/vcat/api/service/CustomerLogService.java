package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CustomerLogDao;
import com.vcat.module.ec.entity.CustomerLog;
@Service
@Transactional(readOnly = true)
public class CustomerLogService extends CrudService<CustomerLog> {

	@Autowired
	private CustomerLogDao customerLogDao;
	@Override
	protected CrudDao<CustomerLog> getDao() {
		return customerLogDao;
	}

}
