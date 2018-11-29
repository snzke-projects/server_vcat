package com.vcat.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.AnswerSheetDao;
import com.vcat.module.ec.entity.AnswerSheet;
@Service
@Transactional(readOnly = true)
public class AnswerSheetService extends CrudService<AnswerSheet> {

	@Autowired
	private AnswerSheetDao answerSheetDao;
	@Override
	protected CrudDao<AnswerSheet> getDao() {
		return answerSheetDao;
	}
	
	public List<AnswerSheet> getAnswerSheet(String customerId, String activityId) {
		return answerSheetDao.getAnswerSheet(customerId,activityId);
	}

}
