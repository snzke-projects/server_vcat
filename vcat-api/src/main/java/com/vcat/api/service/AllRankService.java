package com.vcat.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.AllRankDao;
import com.vcat.module.ec.entity.Rank;
@Service
@Transactional(readOnly = true)
public class AllRankService extends CrudService<Rank> {

	@Autowired
	private AllRankDao allRankDao;

	@Override
	protected CrudDao<Rank> getDao() {
		return allRankDao;
	}
	@Transactional(readOnly = false)
	public void truncate(){
		allRankDao.truncate();
	}
	/**
	 * 分页获取总收入排行列表
	 * @param map
	 * @return
	 */
	public List<Rank> getAllFundRankList(Map<String, Object> map) {
		
		return allRankDao.getAllFundRankList(map);
	}
	//获取总收入排行总数
	public Integer count() {
		
		return allRankDao.count();
	}
	
	public List<String> getPhoneNumList() {
		
		return allRankDao.getPhoneNumList();
	}

}
