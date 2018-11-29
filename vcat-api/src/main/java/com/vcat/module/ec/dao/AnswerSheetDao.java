package com.vcat.module.ec.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.AnswerSheet;

@MyBatisDao
public interface AnswerSheetDao extends CrudDao<AnswerSheet> {

	
	List<AnswerSheet> getAnswerSheet(@Param("customerId")String customerId,@Param("activityId")String activityId);
}

