package com.vcat.api.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CouponFundDao;
import com.vcat.module.ec.entity.CouponFund;
@Service
@Transactional(readOnly = true)
public class CouponFundService extends CrudService<CouponFund> {

	@Autowired
	private CouponFundDao couponFundDao;
	@Override
	protected CrudDao<CouponFund> getDao() {
		return couponFundDao;
	}
	public void addCoupon(String shopId, BigDecimal couInviterFund) {
	
		couponFundDao.addCoupon(shopId,couInviterFund);
	}
	public void subCoupon(String shopId, BigDecimal fund) {
		couponFundDao.subCoupon(shopId,fund);
	}

}
