package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.LoadEarning;

/**
 * 上架奖励DAO接口
 */
@MyBatisDao
public interface LoadEarningDao extends CrudDao<LoadEarning> {
    void deleteLastEarning(LoadEarning loadEarning);
}
