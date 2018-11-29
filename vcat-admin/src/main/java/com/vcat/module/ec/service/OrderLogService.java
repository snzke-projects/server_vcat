package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.OrderLogDao;
import com.vcat.module.ec.entity.Order;
import com.vcat.module.ec.entity.OrderLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单日志Service
 */
@Service
@Transactional(readOnly = true)
public class OrderLogService extends CrudService<OrderLogDao, OrderLog> {
	void addLog(Order order,String action,boolean isSucc){
		String result = isSucc ? "成功" : "失败";
        addLog(order.getId(), action, UserUtils.getUser().getName() + action + order.getOrderNumber() + result, isSucc);
	}

    void addLog(String orderId, String action, String note, boolean isSucc){
        OrderLog log = new OrderLog();
        Order order = new Order();
        order.setId(orderId);
        log.setOrder(order);
        log.setAction(action);
        log.setResult(isSucc ? "成功" : "失败");
        log.setNote(note);
        super.save(log);
    }

}
