package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.AppConfigDao;
import com.vcat.module.ec.entity.AppConfig;
@Service
@Transactional(readOnly = true)
public class AppConfigService extends CrudService<AppConfig> {

	@Autowired
	private AppConfigDao appConfigDao;
	@Override
	protected CrudDao<AppConfig> getDao() {
		return appConfigDao;
	}

}
