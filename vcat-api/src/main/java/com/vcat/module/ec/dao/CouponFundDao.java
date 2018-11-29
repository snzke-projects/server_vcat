package com.vcat.module.ec.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.CouponFund;

@MyBatisDao
public interface CouponFundDao extends CrudDao<CouponFund> {

	void addCoupon(@Param("shopId")String shopId, @Param("fund")BigDecimal fund);

	void subCoupon(@Param("shopId")String shopId, @Param("fund")BigDecimal fund);

}
