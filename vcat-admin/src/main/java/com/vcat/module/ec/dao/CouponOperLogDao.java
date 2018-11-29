package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Coupon;
import com.vcat.module.ec.entity.CouponOperLog;
import com.vcat.module.ec.entity.InviteEarning;
import com.vcat.module.ec.entity.InviteEarningLog;

import java.util.List;

@MyBatisDao
public interface CouponOperLogDao extends CrudDao<CouponOperLog> {

    /**
     * 获取日志列表
     * @param coupon
     * @return
     */
    List<CouponOperLog> findLog(Coupon coupon);

}
