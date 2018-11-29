package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.SuggestDao;
import com.vcat.module.ec.entity.Suggest;
@Service
@Transactional(readOnly = true)
public class SuggestService extends CrudService<Suggest> {

	@Autowired
	private SuggestDao suggestDao;
	@Override
	protected CrudDao<Suggest> getDao() {
		return suggestDao;
	}

}
