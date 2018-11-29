package com.vcat.api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.AppDomainDao;
import com.vcat.module.ec.entity.AppDomain;

import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AppDomainService extends CrudService<AppDomain> {

	@Autowired
	private AppDomainDao appDomainDao;
	@Override
	protected CrudDao<AppDomain> getDao() {
		return appDomainDao;
	}
	
	public Map<String, Object> getByDeviceTypeAndAppVersion(String deviceType,
			String appVersion) {
		return appDomainDao.getByDeviceTypeAndAppVersion(deviceType,appVersion);
	}
}
