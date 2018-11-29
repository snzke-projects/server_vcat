package com.vcat.module.content.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.content.entity.OaNotify;

/**
 * 通知通告DAO接口
 */
@MyBatisDao
public interface OaNotifyDao extends CrudDao<OaNotify> {
	
	/**
	 * 获取通知数目
	 * @param oaNotify
	 * @return
	 */
	public Long findCount(OaNotify oaNotify);

	//根据订单获取发货通知
	public OaNotify findDeliveryNotifyByOrderNum(String orderNum);
	
}