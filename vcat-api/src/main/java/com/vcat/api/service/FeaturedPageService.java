package com.vcat.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.FeaturedPageDao;
import com.vcat.module.ec.entity.FeaturedPage;
@Service
@Transactional(readOnly = true)
public class FeaturedPageService extends CrudService<FeaturedPage> {

	@Autowired
	private FeaturedPageDao featuredPageDao;
	@Override
	protected CrudDao<FeaturedPage> getDao() {
		return featuredPageDao;
	}
	public List<FeaturedPage> getByType(String type) {
		return featuredPageDao.getByType(type);
	}

}
