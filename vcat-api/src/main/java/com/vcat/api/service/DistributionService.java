package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.DistributionDao;
import com.vcat.module.ec.entity.Distribution;
@Service
@Transactional(readOnly = true)
public class DistributionService extends CrudService<Distribution> {

	@Autowired
	private DistributionDao distributionDao;
	@Override
	protected CrudDao<Distribution> getDao() {
		return distributionDao;
	}

}
