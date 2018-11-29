package com.vcat.api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.FreightConfigDao;
import com.vcat.module.ec.dto.OrderItemDto;
import com.vcat.module.ec.entity.FreightConfig;

@Service
@Transactional(readOnly = true)
public class FreightConfigService extends CrudService<FreightConfig>{

	@Autowired
	private FreightConfigDao freightConfigDao;
	@Override
	protected CrudDao<FreightConfig> getDao() {
		return freightConfigDao;
	}
	public BigDecimal getFreightPrice(String buyerId, String productId) {
		return freightConfigDao.getFreightPrice(buyerId,productId);
	}
	//获取订单的邮费模板
	public FreightConfig getFreightBylist(String buyerId,
			List<OrderItemDto> list) {
		
		return freightConfigDao.getFreightBylist(buyerId,list);
	}
}
