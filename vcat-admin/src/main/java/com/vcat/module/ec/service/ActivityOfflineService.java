package com.vcat.module.ec.service;

import com.vcat.common.persistence.Page;
import com.vcat.common.push.PushService;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ActivityOfflineDao;
import com.vcat.module.ec.entity.ActivityOffline;
import com.vcat.module.ec.entity.Participation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 线下活动Service
 */
@Service
@Transactional(readOnly = true)
public class ActivityOfflineService extends CrudService<ActivityOfflineDao, ActivityOffline> {
	@Autowired
	private MessageService messageService;

	@Transactional(readOnly = false)
	public Integer activate(ActivityOffline activity){
		Integer i = dao.activate(activity);
		if(1==i){
			// 推送未读隐匿消息
			messageService.pushAllHideNotRead(PushService.MSG_TYPE_ACTIVITY_OFFLINE);
		}
		return i;
	}
    @Transactional(readOnly = false)
    public Integer changeOpenStatus(ActivityOffline activity){
        return dao.changeOpenStatus(activity);
    }

	public Page<Participation> participationList(Page page, ActivityOffline activity){
		activity.setPage(page);
		page.setList(dao.findParticipationList(activity));
		return page;
	}
}
