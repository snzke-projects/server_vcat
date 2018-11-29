package com.vcat.module.ec.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.QuestionDto;
import com.vcat.module.ec.entity.Question;

import java.util.Map;

@MyBatisDao
public interface QuestionDao extends CrudDao<Question> {
	

	List<QuestionDto> getPersonalQuestionList(@Param("questionnaireId")String questionnaireId,
			@Param("customerId")String customerId,@Param("activityId") String activityId);
	
	List<QuestionDto> getQuestionList(@Param("questionnaireId")String questionnaireId,
			@Param("customerId")String customerId,@Param("activityId") String activityId);
    /**
     * 获取答卷所属问题集合
     * @param answerSheetId
     * @return
     */
    List<Question> findListBySheetId(String answerSheetId);

    /**
     * 获取简答题所有回答
     * @param question
     * @return
     */
    List<Map<String,String>> findShortAnswerList(Question question);
}

