package com.vcat.module.content.dao;

import java.util.List;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.content.entity.Link;

/**
 * 链接DAO接口
 */
@MyBatisDao
public interface LinkDao extends CrudDao<Link> {
	List<Link> findByIdIn(String[] ids);

	int updateExpiredWeight(Link link);
}
