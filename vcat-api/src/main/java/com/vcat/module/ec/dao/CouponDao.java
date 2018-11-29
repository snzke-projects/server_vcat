package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Coupon;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface CouponDao extends CrudDao<Coupon> {

    /**
     * 激活邀请活动
     * @param coupon
     * @return
     */
    Integer activate(Coupon coupon);

    /**
     * 检查该邀请活动是否可激活
     * @param coupon
     * @return
     */
    boolean checkCaneBeActivated(Coupon coupon);

    /**
     * 获取饼状图需要的数据
     * @param id
     * @return
     */
    List<Map<String,Object>> getPieChartList(String id);

}
