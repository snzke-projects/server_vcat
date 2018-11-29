package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Gateway;

@MyBatisDao
public interface GatewayDao extends CrudDao<Gateway> {
    /**
     * 根据编码获取支付方式
     * @param code
     * @return
     */
    Gateway getByCode(String code);
}
