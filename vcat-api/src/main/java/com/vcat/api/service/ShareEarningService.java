package com.vcat.api.service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import com.vcat.common.lock.DistLockHelper;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ShareEarningDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ShareEarning;
import com.vcat.module.ec.entity.ShareEarningLog;
import com.vcat.module.ec.entity.ThirdLoginType;
@Service
@Transactional(readOnly = true)
public class ShareEarningService extends CrudService<ShareEarning> {

	@Autowired
	private ShareEarningDao shareEarningDao;
	@Autowired
	private ShareEarningLogService shareEarningLogService;

	@Override
	protected CrudDao<ShareEarning> getDao() {
		return shareEarningDao;
	}

	public ShareEarning getAvaShareEarning(ShareEarning shareEarning) {
		
		return shareEarningDao.getAvaShareEarning(shareEarning);
	}

	//添加分享奖励
	@Transactional(readOnly = false)
	public void addShareEarning(String shopId, String productId, String shareType) {
		Product product = new Product();
		product.setId(productId);
		ShareEarning shareEarning = new ShareEarning();
		shareEarning.setProduct(product);
		RLock lock = DistLockHelper.getLock("addShareEarning");
		try {
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
			ShareEarning earning = shareEarningDao.getAvaShareEarning(shareEarning);
			if (earning != null
					&& earning.getAvailableShare() > earning.getSharedCount()
					&& earning.getFund().compareTo(BigDecimal.ZERO) > 0) {
				logger.debug("该分享还没结束，更新用户分享收入: "+ earning.toString());
				// 组装分享日志
				ShareEarningLog shareLog = new ShareEarningLog();
				shareLog.setShopId(shopId);
				shareLog.setShareId(earning.getId());

					ShareEarningLog log = shareEarningLogService.get(shareLog);
					if (log == null) {
						ThirdLoginType type = new ThirdLoginType();
						shareLog.preInsert();
						type.setCode(shareType);
						shareLog.setThirdLoginType(type);
						shareEarningLogService.saveLog(earning, shareLog, earning
								.getProduct().getName());
					}

			}else{
				logger.debug("该分享已经结束");
			}
		} finally {
			if(lock.isLocked())
				lock.unlock();
		}
	}
}
