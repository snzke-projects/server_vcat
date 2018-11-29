package com.vcat.module.core.dao;

import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.core.entity.Area;

/**
 * 区域DAO接口
 */
@MyBatisDao
public interface AreaDao extends TreeDao<Area> {
}
