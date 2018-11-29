package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.OrderItemDao;
import com.vcat.module.ec.entity.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单项Service
 */
@Service
@Transactional(readOnly = true)
public class OrderItemService extends CrudService<OrderItemDao, OrderItem> {

}
