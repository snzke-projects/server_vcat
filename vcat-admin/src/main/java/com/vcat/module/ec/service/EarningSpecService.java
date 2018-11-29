package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.CustomerDao;
import com.vcat.module.ec.dao.EarningSpecDao;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.EarningSpec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 上架奖励提现率Service
 */
@Service
@Transactional(readOnly = true)
public class EarningSpecService extends CrudService<EarningSpecDao, EarningSpec> {

}
