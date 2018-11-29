package com.vcat.api.service;

import com.vcat.module.ec.entity.OrderReserveLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.OrderLogDao;
import com.vcat.module.ec.entity.OrderLog;
@Service
@Transactional(readOnly = true)
public class OrderLogService extends CrudService<OrderLog> {
	@Autowired
	private OrderLogDao orderLogDao;
	@Override
	protected CrudDao<OrderLog> getDao() {
		return orderLogDao;
	}

	public void insertReserveLog(OrderReserveLog reserveLog){
		orderLogDao.insertReserveLog(reserveLog);
	}
}
