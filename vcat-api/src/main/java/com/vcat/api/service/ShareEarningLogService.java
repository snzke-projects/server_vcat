package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.ec.dao.ShareEarningLogDao;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class ShareEarningLogService extends CrudService<ShareEarningLog> {

	@Autowired
	private ShareEarningLogDao shareEarningLogDao;
	@Autowired
	private ShopFundService shopFundService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private FundOperLogService fundOperLogService;
	@Override
	protected CrudDao<ShareEarningLog> getDao() {
		return shareEarningLogDao;
	}
	@Transactional(readOnly = false)
	public void saveLog(ShareEarning earning, ShareEarningLog shareLog, String productName) {
		//记录日志
		shareEarningLogDao.insert(shareLog);
		//添加总资金
		ShopFund shopFund = shopFundService.get(shareLog.getShopId());
		shopFund.setShareAvailableFund(shopFund.getShareAvailableFund().add(
				earning.getFund()));
		shopFundService.update(shopFund);
		//记录资金操作日志
        fundOperLogService.insert(shopFund.getId(),FundFieldType.SHARE_AVAILABLE_FUND,earning.getFund(),shopFund.getShareAvailableFund(),shareLog.getId(), FundOperLog.NORMAL_INCOME,
                "分享成功，增加分享可提现[" + earning.getFund() + "]");
        // 向小店店主推送财富消息 - 分享消息
        MessageEarning shareEarning = new MessageEarning();
        Shop shop = new Shop();
        shop.setId(shopFund.getId());
        shareEarning.setShop(shop);
        shareEarning.setType(MessageEarning.TYPE_SHARE);
        shareEarning.setConsumer("V猫");
        shareEarning.setProductName(productName);
        shareEarning.setQuantity(1);
        shareEarning.setEarning(earning.getFund());
        shareEarning.setShareName(earning.getTitle());
        messageService.pushEarningMsgTask(shareEarning);
	}

}
