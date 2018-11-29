package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.OrderReserveDao;
import com.vcat.module.ec.entity.OrderReserve;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderReserveService extends CrudService<OrderReserveDao, OrderReserve> {
}
