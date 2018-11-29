package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CloudImageDao;
import com.vcat.module.ec.entity.CloudImage;

@Service
@Transactional(readOnly = true)
public class CloudImageService extends CrudService<CloudImage> {

	@Autowired
	private CloudImageDao dao;

	@Override
	protected CrudDao<CloudImage> getDao() {
		return dao;
	}
}
