package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.RefundLogDto;
import com.vcat.module.ec.entity.RefundLog;

import java.util.List;

@MyBatisDao
public interface RefundLogDao extends CrudDao<RefundLog> {

	List<RefundLogDto> findLogList(RefundLog log);

    int initByOrderId(String orderId);

    int addApprovalLog(String orderId);
}
