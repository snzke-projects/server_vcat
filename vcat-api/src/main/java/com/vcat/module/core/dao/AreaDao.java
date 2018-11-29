package com.vcat.module.core.dao;

import java.util.List;

import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.core.entity.Area;
import com.vcat.module.ec.dto.AreaDto;

/**
 * 区域DAO接口
 */
@MyBatisDao
public interface AreaDao extends TreeDao<Area> {

	public List<AreaDto> getAreaList(String areaId);
}
