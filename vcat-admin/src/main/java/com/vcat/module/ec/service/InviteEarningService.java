package com.vcat.module.ec.service;

import com.vcat.common.persistence.Page;
import com.vcat.common.push.PushService;
import com.vcat.common.utils.DateUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.InviteEarningDao;
import com.vcat.module.ec.dao.InviteEarningLogDao;
import com.vcat.module.ec.entity.InviteEarning;
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
public class InviteEarningService extends CrudService<InviteEarningDao, InviteEarning> {
    @Autowired
    private InviteEarningLogDao inviteEarningLogDao;

	@Transactional(readOnly = false)
	public Integer activate(InviteEarning inviteEarning) throws Exception {
        inviteEarning = get(inviteEarning);
		if(dao.checkCaneBeActivated(inviteEarning)){
			throw new Exception("该时间段[" + DateUtils.formatDateTime(inviteEarning.getStartTime()) + "到" + DateUtils.formatDateTime(inviteEarning.getEndTime()) + "]内已包含邀请活动，激活失败！");
		}
		return dao.activate(inviteEarning);
	}

    public Page<Map<String,Object>> findLogPage(Page page, InviteEarning inviteEarning) {
        inviteEarning.setPage(page);
        page.setList(inviteEarningLogDao.findLog(inviteEarning));
        return page;
    }

    public List<Map<String, Object>> getPieChartList(InviteEarning inviteEarning) {
        return dao.getPieChartList(inviteEarning.getId());
    }
}
