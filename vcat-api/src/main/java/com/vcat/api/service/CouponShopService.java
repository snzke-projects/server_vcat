package com.vcat.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CouponShopDao;
import com.vcat.module.ec.dto.FundDetailBillDto;
import com.vcat.module.ec.entity.CouponShop;
@Service
@Transactional(readOnly = true)
public class CouponShopService extends CrudService<CouponShop> {

	@Autowired
	private CouponShopDao couponShopDao;
	@Override
	protected CrudDao<CouponShop> getDao() {
		return couponShopDao;
	}
	public Integer getShopCouponDetailBillCount(String month, String shopId) {
		
		return couponShopDao.getShopCouponDetailBillCount(month,shopId);
	}
	public List<FundDetailBillDto> getShopCouponDetailBill(Pager page,
			String shopId, String month) {
		
		return couponShopDao.getShopCouponDetailBill(page, shopId, month);
	}
	public List<FundDetailBillDto> getShopCouponDetailBill2(Pager page,
			String shopId, String month) {
		
		return couponShopDao.getShopCouponDetailBill2(page, shopId, month);
	}

	public boolean canInsertCoupon(String shopId){
		Integer integer = couponShopDao.getInviteeCouponCount(shopId);
		return integer == null ? true : integer == 0;
	}

}
