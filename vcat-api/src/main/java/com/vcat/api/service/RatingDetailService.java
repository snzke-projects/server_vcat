package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.RatingDetailDao;
import com.vcat.module.ec.entity.RatingDetail;
@Service
@Transactional(readOnly = true)
public class RatingDetailService extends CrudService<RatingDetail> {

	@Autowired
	private RatingDetailDao ratingDetailDao;
	@Override
	protected CrudDao<RatingDetail> getDao() {
		return ratingDetailDao;
	}

}
