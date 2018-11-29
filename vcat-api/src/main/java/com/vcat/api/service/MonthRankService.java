package com.vcat.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.MonthRankDao;
import com.vcat.module.ec.entity.Rank;
@Service
@Transactional(readOnly = true)
public class MonthRankService extends CrudService<Rank> {

	@Autowired
	private MonthRankDao monthRankDao;

	@Override
	protected CrudDao<Rank> getDao() {
		return monthRankDao;
	}
	@Transactional(readOnly = false)
	public void truncate(){
		monthRankDao.truncate();
	}
	/**
	 * 分页获取月收入排行
	 * @param map
	 * @return
	 */
	public List<Rank> getMonthFundRankList(Map<String, Object> map) {
		return monthRankDao.getMonthFundRankList(map);
	}
	/**
	 * 获取我的收入排行位置
	 * @param shopId
	 * @return
	 */
	public Integer getMyMonthRank(String shopId) {
		
		return monthRankDao.getMyMonthRank(shopId);
	}
	//计算月收入排行总数
	public Integer count() {
		
		return monthRankDao.count();
	}
}
