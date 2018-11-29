package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.InviteEarningLogDao;
import com.vcat.module.ec.entity.InviteEarningLog;
@Service
@Transactional(readOnly = true)
public class InviteEarningLogService extends CrudService<InviteEarningLog> {

	@Autowired
	private InviteEarningLogDao inviteEarningLogDao;
	@Override
	protected CrudDao<InviteEarningLog> getDao() {
		return inviteEarningLogDao;
	}
}
