package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ShippingDao;
import com.vcat.module.ec.entity.Shipping;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 运单Service
 */
@Service
@Transactional(readOnly = true)
public class ShippingService extends CrudService<ShippingDao, Shipping> {
}
