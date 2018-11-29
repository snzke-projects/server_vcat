package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.OrderClearInventoryDao;
import com.vcat.module.ec.entity.OrderClearInventory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderClearInventoryService extends CrudService<OrderClearInventoryDao, OrderClearInventory> {
}
