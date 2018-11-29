package com.vcat.module.ec.service;

import com.google.common.collect.Maps;
import com.vcat.common.persistence.Page;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ActivityDao;
import com.vcat.module.ec.entity.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 体验官Service
 */
@Service
@Transactional(readOnly = true)
public class ActivityService extends CrudService<ActivityDao, Activity> {
	@Autowired
	private MessageService messageService;

	@Transactional(readOnly = false)
	public Integer activate(Activity activity){
		Integer i = dao.activate(activity);
//		if(1==i){
//			// 推送未读隐匿消息
//			messageService.pushAllHideNotRead(PushService.MSG_TYPE_ACTIVITY);
//		}
		return i;
	}

	public Page participationList(Page page,Activity activity){
		activity.setPage(page);
		page.setList(dao.findParticipationList(activity));
		return page;
	}

    public Integer countReported(String activityId){
        return dao.countReported(activityId);
    }
    public Map<String,Integer> statisticsActivityAnswer(String activityId){
        Map<String,Integer> questionMap = Maps.newHashMap();
        List<Map<String,String>> list = dao.statisticsActivityAnswer(activityId);
        if(null != list && !list.isEmpty()){
            for (int i = 0; i < list.size(); i++) {
                Map row = list.get(i);
                Object questionId = row.get("questionId");
                Object count = row.get("count");
                questionMap.put(questionId+"",Integer.parseInt(count+""));
            }
        }
        return questionMap;
    }

    @Transactional(readOnly = false)
    public void save(Activity entity) {
        if (entity.getIsNewRecord()){
            entity.preInsert();
            dao.insert(entity);
            dao.insertActivityQuestionnaire(entity);
        }else{
            entity.preUpdate();
            dao.update(entity);
        }
    }

    @Transactional(readOnly = false)
    public void feedback(String activityId) {
        dao.feedback(activityId);
    }
}
