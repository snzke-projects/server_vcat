package com.vcat.module.ec.dao;

import java.util.List;
import java.util.Map;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Rank;

@MyBatisDao
public interface AllRankDao extends CrudDao<Rank> {
	//清除数据
	void truncate();

	List<Rank> getAllFundRankList(Map<String, Object> map);

	Integer count();

	List<String> getPhoneNumList();
}
