package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ShopFund;

@MyBatisDao
public interface FundDao extends CrudDao<ShopFund> {

}
