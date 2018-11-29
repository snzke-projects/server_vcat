package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.RefundLogDao;
import com.vcat.module.ec.dto.RefundLogDto;
import com.vcat.module.ec.entity.RefundLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional(readOnly = true)
public class RefundLogService extends CrudService<RefundLog> {

	@Autowired
	private RefundLogDao refundLogDao;
	@Override
	protected CrudDao<RefundLog> getDao() {
		return refundLogDao;
	}
	public List<RefundLogDto> findLogList(RefundLog log) {
	
		return refundLogDao.findLogList(log);
	}

    @Transactional(readOnly = false)
    public int initByOrderId(String orderId){
        return refundLogDao.initByOrderId(orderId);
    }

    @Transactional(readOnly = false)
    public int addApprovalLog(String orderId){
        return refundLogDao.addApprovalLog(orderId);
    }
}
