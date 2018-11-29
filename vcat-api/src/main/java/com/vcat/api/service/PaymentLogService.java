package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.PaymentLogDao;
import com.vcat.module.ec.entity.PaymentLog;
@Service
@Transactional(readOnly = true)
public class PaymentLogService extends CrudService<PaymentLog> {

	@Autowired
	private PaymentLogDao paymentLogDao;
	@Override
	protected CrudDao<PaymentLog> getDao() {
		return paymentLogDao;
	}
	@Transactional(readOnly = false)
	public void insertBypayment(PaymentLog log) {
		paymentLogDao.insertBypayment(log);
	}
	
	public PaymentLog getPaymentLog(PaymentLog log) {
		
		return paymentLogDao.getPaymentLog(log);
	}

}
