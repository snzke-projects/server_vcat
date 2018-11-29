package com.vcat.module.core.dao;

import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.core.entity.Office;

/**
 * 机构DAO接口
 */
@MyBatisDao
public interface OfficeDao extends TreeDao<Office> {
	
}
