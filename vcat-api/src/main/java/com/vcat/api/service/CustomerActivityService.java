package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CustomerActivityDao;
import com.vcat.module.ec.entity.CustomerActivity;
@Service
@Transactional(readOnly = true)
public class CustomerActivityService extends CrudService<CustomerActivity> {

	@Autowired
	private CustomerActivityDao customerActivityDao;
	@Override
	protected CrudDao<CustomerActivity> getDao() {
		return customerActivityDao;
	}
}
