package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.PaymentLog;

@MyBatisDao
public interface PaymentLogDao extends CrudDao<PaymentLog> {

	void insertBypayment(PaymentLog log);

	PaymentLog getPaymentLog(PaymentLog log);

}
