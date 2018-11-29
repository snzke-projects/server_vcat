package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.core.entity.FundOperLog;

@MyBatisDao
public interface FundOperLogDao extends CrudDao<FundOperLog> {
    int insertLoad(FundOperLog fundOperLog);
    int insertSale(FundOperLog fundOperLog);
    int insertShare(FundOperLog fundOperLog);
    int insertBonus(FundOperLog fundOperLog);
    int insertInvite(FundOperLog fundOperLog);
}
