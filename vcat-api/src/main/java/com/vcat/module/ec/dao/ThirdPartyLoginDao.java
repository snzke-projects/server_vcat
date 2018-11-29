package com.vcat.module.ec.dao;

import java.util.Map;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ThirdPartyLogin;

@MyBatisDao
public interface ThirdPartyLoginDao extends CrudDao<ThirdPartyLogin> {

	ThirdPartyLogin selectByTypeCodeAndUniqueId(Map<?,?> map);
	
	ThirdPartyLogin selectByTypeCodeAndCustomerId(Map<?,?> map);
	
	}
