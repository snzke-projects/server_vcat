package com.vcat.module.ec.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.FundDetailBillDto;
import com.vcat.module.ec.entity.CouponShop;

@MyBatisDao
public interface CouponShopDao extends CrudDao<CouponShop> {

	Integer getShopCouponDetailBillCount(@Param("month")String month, @Param("shopId")String shopId);

	List<FundDetailBillDto> getShopCouponDetailBill(@Param("page")Pager page, @Param("shopId")String shopId,
			@Param("month")String month);

	List<FundDetailBillDto> getShopCouponDetailBill2(@Param("page")Pager page, @Param("shopId")String shopId,
			@Param("month")String month);

	Integer getInviteeCouponCount(String shopId);
}
