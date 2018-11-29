package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CouponDao;
import com.vcat.module.ec.entity.Coupon;
@Service
@Transactional(readOnly = true)
public class CouponService extends CrudService<Coupon> {

	@Autowired
	private CouponDao couponDao;
	@Override
	protected CrudDao<Coupon> getDao() {
		return couponDao;
	}

}
