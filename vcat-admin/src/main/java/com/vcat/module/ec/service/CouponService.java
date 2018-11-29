package com.vcat.module.ec.service;

import com.vcat.common.persistence.Page;
import com.vcat.common.utils.DateUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.CouponDao;
import com.vcat.module.ec.dao.CouponOperLogDao;
import com.vcat.module.ec.entity.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 上架奖励Service
 */
@Service
@Transactional(readOnly = true)
public class CouponService extends CrudService<CouponDao, Coupon> {
    @Autowired
    private CouponOperLogDao couponOperLogDao;

    @Override
    public Coupon get(Coupon entity) {
        entity.getSqlMap().put("all","true");
        return super.get(entity);
    }

    @Transactional(readOnly = false)
	public Integer activate(Coupon coupon) throws Exception {
        coupon = get(coupon);
		if(dao.checkCaneBeActivated(coupon)){
			throw new Exception("该时间段[" + DateUtils.formatDateTime(coupon.getStartTime()) + "到" + DateUtils.formatDateTime(coupon.getEndTime()) + "]内已存在相同类型券，激活失败！");
		}
		return dao.activate(coupon);
	}

    public Page<Map<String,Object>> findLogPage(Page page, Coupon coupon) {
        coupon.setPage(page);
        page.setList(couponOperLogDao.findLog(coupon));
        return page;
    }

    public List<Map<String, Object>> getPieChartList(Coupon coupon) {
        return dao.getPieChartList(coupon.getId());
    }
}
