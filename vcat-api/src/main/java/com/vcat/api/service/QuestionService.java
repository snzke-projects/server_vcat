package com.vcat.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.QuestionDao;
import com.vcat.module.ec.dto.QuestionDto;
import com.vcat.module.ec.entity.Question;
@Service
@Transactional(readOnly = true)
public class QuestionService extends CrudService<Question> {

	@Autowired
	private QuestionDao questionDao;
	@Override
	protected CrudDao<Question> getDao() {
		return questionDao;
	}
	
	public List<QuestionDto> getPersonalQuestionList(String questionnaireId, String customerId, String activityId) {
		return questionDao.getPersonalQuestionList(questionnaireId,customerId,activityId);
	}

	public List<QuestionDto> getQuestionList(String questionnaireId, String customerId, String activityId) {
		return questionDao.getQuestionList(questionnaireId,customerId,activityId);
	}

}
