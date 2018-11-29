package com.vcat.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ActivityFeedBackDao;
import com.vcat.module.ec.entity.ActivityFeedBack;

@Service
@Transactional(readOnly = true)
public class ActivityFeedBackService extends CrudService<ActivityFeedBack> {
	@Autowired
	private ActivityFeedBackDao activityFeedBackDao;
	protected CrudDao<ActivityFeedBack> getDao() {
		return activityFeedBackDao;
	}
	
	public int countFeedBackList() {
		
		return activityFeedBackDao.countFeedBackList();
	}

	public List<ActivityFeedBack> getFeedBackList(Pager page) {
		
		return activityFeedBackDao.getFeedBackList(page);
	}

	public ActivityFeedBack getDetail(String feedBackId) {
		ActivityFeedBack feedBack = activityFeedBackDao.get(feedBackId);
		return feedBack;
	}
}
