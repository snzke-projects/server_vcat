package com.vcat.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.PaymentDao;
import com.vcat.module.ec.entity.Payment;
@Service
@Transactional(readOnly = true)
public class PaymentService extends CrudService<Payment> {

	@Autowired
	private PaymentDao paymentDao;
	@Override
	protected CrudDao<Payment> getDao() {
		return paymentDao;
	}
	public void insertAll(Payment payment) {
		paymentDao.insertAll(payment);
	}
	public Payment getByOrderId(String orderId) {
		
		return paymentDao.getByOrderId(orderId);
	}
	@Transactional(readOnly = false)
	public void deleteById(String paymentId) {
		paymentDao.deleteById(paymentId);
		
	}
	//通过支付单号获取订单号
	public String getPaymentIdByPaymentNum(String out_trade_no) {
		
		return paymentDao.getPaymentIdByPaymentNum(out_trade_no);
	}
	
	public List<String> getOrderIds(String paymentId) {
		
		return paymentDao.getOrderIds(paymentId);
	}
	public Payment getPayment(String id){
		return paymentDao.getPayment(id);
	}

	public String getSellerId(String paymentNum){
		return paymentDao.getSellerId(paymentNum);
	}
}
