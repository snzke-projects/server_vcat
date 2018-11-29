package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CouponOperLogDao;
import com.vcat.module.ec.entity.CouponOperLog;
@Service
@Transactional(readOnly = true)
public class CouponOperLogService extends CrudService<CouponOperLog> {

	@Autowired
	private CouponOperLogDao couponOperLogDao;
	@Override
	protected CrudDao<CouponOperLog> getDao() {
		return couponOperLogDao;
	}

}
