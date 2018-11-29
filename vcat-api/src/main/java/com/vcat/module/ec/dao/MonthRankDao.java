package com.vcat.module.ec.dao;

import java.util.List;
import java.util.Map;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Rank;

@MyBatisDao
public interface MonthRankDao extends CrudDao<Rank> {
	//清除数据
	void truncate();

	List<Rank> getMonthFundRankList(Map<String, Object> map);

	Integer getMyMonthRank(String shopId);

	Integer count();
}
