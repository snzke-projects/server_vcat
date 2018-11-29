package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.EarningSpec;

/**
 * 上架奖励提现率DAO
 */
@MyBatisDao
public interface EarningSpecDao extends CrudDao<EarningSpec> {
}
