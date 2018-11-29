package com.vcat.module.ec.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface AppVersionDao {
	Map<String, Object> queryLastVersion(@Param("type") int type);
}
