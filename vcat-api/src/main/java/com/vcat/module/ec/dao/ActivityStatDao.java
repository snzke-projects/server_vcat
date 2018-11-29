package com.vcat.module.ec.dao;

import java.util.List;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.QuestionDto;
import com.vcat.module.ec.entity.ActivityStat;

@MyBatisDao
public interface ActivityStatDao extends CrudDao<ActivityStat> {

	List<QuestionDto> getQuestionList(String feedBackId);

}
