package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.OrderItemDao;
import com.vcat.module.ec.dao.PaymentLogDao;
import com.vcat.module.ec.entity.OrderItem;
import com.vcat.module.ec.entity.PaymentLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付日志Service
 */
@Service
@Transactional(readOnly = true)
public class PaymentLogService extends CrudService<PaymentLogDao, PaymentLog> {

}
