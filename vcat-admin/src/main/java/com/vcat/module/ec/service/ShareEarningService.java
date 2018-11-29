package com.vcat.module.ec.service;

import com.vcat.common.persistence.Page;
import com.vcat.common.push.PushService;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ShareEarningDao;
import com.vcat.module.ec.dao.ShareEarningLogDao;
import com.vcat.module.ec.entity.ShareEarning;
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
public class ShareEarningService extends CrudService<ShareEarningDao, ShareEarning> {
	@Autowired
	private MessageService messageService;
    @Autowired
    private ShareEarningLogDao shareEarningLogDao;

    @Override
    public List<ShareEarning> findList(ShareEarning entity) {
        return super.findList(entity);
    }

    public List<ShareEarning> getShareEarningList(String productId){
		return dao.getShareEarningList(productId);
	}

	@Transactional(readOnly = false)
	public Integer activate(ShareEarning shareEarning) throws Exception {
		if(dao.checkSameProductActivated(shareEarning)){
			throw new Exception("该商品正在分享中...");
		}
		Integer i = dao.activate(shareEarning);
		if(1==i){
            shareEarning = get(shareEarning);
            List<String> pushTokenList = dao.getPushTokenListByProductId(shareEarning.getProduct().getId());
			messageService.pushHideNotRead(pushTokenList,PushService.MSG_TYPE_SHARE);
		}
		return i;
	}

    public Page<Map<String,Object>> findLogPage(Page page, ShareEarning shareEarning) {
        shareEarning.setPage(page);
        page.setList(shareEarningLogDao.findLog(shareEarning));
        return page;
    }

    public List<Map<String, Object>> getPieChartList(ShareEarning shareEarning) {
        return dao.getPieChartList(shareEarning.getId());
    }
}
