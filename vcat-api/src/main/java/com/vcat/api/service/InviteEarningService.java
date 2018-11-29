package com.vcat.api.service;

import com.vcat.module.ec.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.InviteEarningDao;
import com.vcat.module.ec.entity.InviteEarning;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class InviteEarningService extends CrudService<InviteEarning> {

	@Autowired
	private InviteEarningDao inviteEarningDao;
	@Override
	protected CrudDao<InviteEarning> getDao() {
		return inviteEarningDao;
	}
	public InviteEarning getInviteEarning(InviteEarning inviteEarning) {
		
		return inviteEarningDao.getInviteEarning(inviteEarning);
	}

    /**
     * 获取需要发送邀请奖励V猫新动态的小店列表
     * @return
     */
    public List<Customer> getAvailableInviteEarningShopList() {
        return inviteEarningDao.getAvailableInviteEarningShopList();
    }
}
