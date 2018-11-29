package com.vcat.module.ec.dao;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.annotation.MyBatisDao;

import java.util.Date;

@MyBatisDao
public interface ExpressApiDao {
	void insert(@Param("id") String id, @Param("code") String code,
			@Param("no") String no, @Param("data") String data,
			@Param("state") String state, @Param("receivingDate") Date receivingDate);

	String query(@Param("code") String code, @Param("no") String no);
}
