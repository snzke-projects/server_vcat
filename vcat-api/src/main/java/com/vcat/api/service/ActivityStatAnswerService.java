package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ActivityStatAnswerDao;
import com.vcat.module.ec.entity.ActivityStatAnswer;

@Service
@Transactional(readOnly = true)
public class ActivityStatAnswerService extends CrudService<ActivityStatAnswer> {
	@Autowired
	private ActivityStatAnswerDao activityStatAnswerDao;
	protected CrudDao<ActivityStatAnswer> getDao() {
		return activityStatAnswerDao;
	}
}
