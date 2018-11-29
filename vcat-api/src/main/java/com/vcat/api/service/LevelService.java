package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.LevelDao;
import com.vcat.module.ec.entity.Level;
@Service
@Transactional(readOnly = true)
public class LevelService extends CrudService<Level> {

	@Autowired
	private LevelDao levelDao;

	@Override
	protected CrudDao<Level> getDao() {
		return levelDao;
	}
}
