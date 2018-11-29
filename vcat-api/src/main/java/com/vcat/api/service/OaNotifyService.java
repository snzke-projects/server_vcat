package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.content.dao.OaNotifyDao;
import com.vcat.module.content.dao.OaNotifyRecordDao;
import com.vcat.module.content.entity.OaNotify;
@Service
@Transactional(readOnly = true)
public class OaNotifyService extends CrudService<OaNotify> {

	@Autowired
	private OaNotifyDao oaNotifyDao;
	@Autowired
	private OaNotifyRecordDao oaNotifyRecordDao;
	@Override
	protected CrudDao<OaNotify> getDao() {
		return oaNotifyDao;
	}

	public OaNotify findDeliveryNotifyByOrderNum(String orderNum) {
		
		return oaNotifyDao.findDeliveryNotifyByOrderNum(orderNum);
	}
	
	@Transactional(readOnly = false)
	public void save(OaNotify oaNotify) {
		oaNotifyDao.insert(oaNotify);
		if(oaNotify.getOaNotifyRecordList()!=null&&oaNotify.getOaNotifyRecordList().size()>0)
			oaNotifyRecordDao.insertAll(oaNotify.getOaNotifyRecordList());
	}
}
