package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.GatewayDao;
import com.vcat.module.ec.entity.Gateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户类型Service
 */
@Service
@Transactional(readOnly = true)
public class GatewayService extends CrudService<GatewayDao, Gateway> {


}
