package com.vcat.module.ec.dao;


import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.OrderReserveDto;
import com.vcat.module.ec.entity.OrderReserve;

/**
 * desc:
 */
@MyBatisDao
public interface OrderReserveDao {
    void insertReserve(OrderReserveDto reserve);
    OrderReserve getOrderReserve(OrderReserve orderReserve);
}
