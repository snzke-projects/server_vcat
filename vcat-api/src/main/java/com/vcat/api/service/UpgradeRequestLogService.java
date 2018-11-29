package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.UpgradeRequestDao;
import com.vcat.module.ec.dao.UpgradeRequestLogDao;
import com.vcat.module.ec.entity.UpgradeRequestLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UpgradeRequestLogService extends CrudService<UpgradeRequestLog> {
    @Autowired
    private UpgradeRequestLogDao upgradeRequestLogDao;
    @Override
    protected CrudDao<UpgradeRequestLog> getDao() {
        return upgradeRequestLogDao;
    }
}
