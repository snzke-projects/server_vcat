package com.vcat.api.service;

import com.vcat.module.ec.dao.OrderReserveDao;
import com.vcat.module.ec.dto.OrderReserveDto;
import com.vcat.module.ec.entity.OrderReserve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderReserveService {
    @Autowired
    private OrderReserveDao orderReserveDao;
    @Transactional(readOnly = false)

    /**
     * 购买预售商品后,插入预购商品订单表
     */
    public void insertReserve(OrderReserveDto reserve) {
        orderReserveDao.insertReserve(reserve);
    }

    public OrderReserve getOrderReserve(OrderReserve orderReserve){
        return orderReserveDao.getOrderReserve(orderReserve);
    }
}
