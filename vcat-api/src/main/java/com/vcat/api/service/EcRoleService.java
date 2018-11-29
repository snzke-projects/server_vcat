package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.EcRoleDao;
import com.vcat.module.ec.entity.EcRole;
@Service
@Transactional(readOnly = true)
public class EcRoleService extends CrudService<EcRole> {

	@Autowired
	private EcRoleDao RoleDao;

	@Override
	protected CrudDao<EcRole> getDao() {
		return RoleDao;
	}
}
