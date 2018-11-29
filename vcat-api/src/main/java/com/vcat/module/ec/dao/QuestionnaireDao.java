package com.vcat.module.ec.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Questionnaire;

@MyBatisDao
public interface QuestionnaireDao extends CrudDao<Questionnaire> {

	List<Questionnaire> getQuestionnaireList(String activityId);

	void insertActivityQuestionnaire(@Param("activityId")String activityId,@Param("answerSheetId")String answerSheetId);
}

