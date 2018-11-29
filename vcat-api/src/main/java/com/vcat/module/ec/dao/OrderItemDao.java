package com.vcat.module.ec.dao;

import java.util.List;
import java.util.Map;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.OrderItemDto;
import com.vcat.module.ec.entity.OrderItem;
import com.vcat.module.ec.entity.Payment;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface OrderItemDao extends CrudDao<OrderItem> {
	List<String> findDeliveryerList(String orderNum);
	void insertItem(OrderItemDto item);
	OrderItemDto checkReFund(String orderItemId);
	Integer getUnCompletedRefundCountByOrderId(String orderId);
	List<Map<String,Object>> getPaymentInfo(@Param("paymentId")String paymentId);
	void deleteById(String orderId);
	int getOrderItemType(@Param("productItemId")String productItemId,@Param("orderId")String orderId);
}
