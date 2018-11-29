package com.vcat.module.ec.dao;

import java.util.List;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Payment;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface PaymentDao extends CrudDao<Payment> {

	void insertAll(Payment payment);

	Payment getByOrderId(String orderId);

	void deleteById(String paymentId);
	
	String getPaymentIdByPaymentNum(String out_trade_no);

	List<String> getOrderIds(String paymentId);
	Payment getPayment(@Param("paymentId")String paymentId);

	String getSellerId(String paymentNum);
}
