package com.vcat.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CustomerRoleDao;
import com.vcat.module.ec.entity.CustomerRole;
@Service
@Transactional(readOnly = true)
public class CustomerRoleService extends CrudService<CustomerRole> {

	@Autowired
	private CustomerRoleDao customerRoleDao;
	@Override
	protected CrudDao<CustomerRole> getDao() {
		return customerRoleDao;
	}
	public List<CustomerRole> findRoleList(String customerId) {
		return customerRoleDao.findRoleList(customerId);
	}
	public CustomerRole findRole(String customerId, String roleName) {
		
		return customerRoleDao.findRole(customerId,roleName);
	}
	public void deleteRole(String customerId, String roleName) {
		
		customerRoleDao.deleteRole(customerId,roleName);
	}

}
