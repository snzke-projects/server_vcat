package com.vcat.api.service;

import com.vcat.module.ec.dao.CustomerOfflineActivityDao;
import com.vcat.module.ec.entity.CustomerOfflineActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;

@Service
@Transactional(readOnly = true)
public class CustomerOfflineActivityService extends CrudService<CustomerOfflineActivity> {
    @Autowired
    private CustomerOfflineActivityDao customerOfflineActivityDao;
    @Override
    protected CrudDao<CustomerOfflineActivity> getDao() {
        return customerOfflineActivityDao;
    }
}



