package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ThirdLoginTypeDao;
import com.vcat.module.ec.entity.ThirdLoginType;

@Service
@Transactional(readOnly = true)
public class ThirdLoginTypeService extends CrudService<ThirdLoginType> {

	@Autowired
	private ThirdLoginTypeDao ThirdLoginTypeDao;
	@Override
	protected CrudDao<ThirdLoginType> getDao() {
		return ThirdLoginTypeDao;
	}

}
