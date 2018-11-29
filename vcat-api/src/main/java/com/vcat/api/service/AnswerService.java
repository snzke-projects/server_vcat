package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.AnswerDao;
import com.vcat.module.ec.entity.Answer;
@Service
@Transactional(readOnly = true)
public class AnswerService extends CrudService<Answer> {

	@Autowired
	private AnswerDao answerDao;
	@Override
	protected CrudDao<Answer> getDao() {
		return answerDao;
	}

}
