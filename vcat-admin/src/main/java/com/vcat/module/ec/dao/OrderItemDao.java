package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.OrderItem;

@MyBatisDao
public interface OrderItemDao extends CrudDao<OrderItem> {
	Integer getUnCompletedRefundCountByOrderId(String orderId);
}
