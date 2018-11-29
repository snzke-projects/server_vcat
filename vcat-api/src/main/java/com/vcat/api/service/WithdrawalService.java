package com.vcat.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.WithdrawalDao;
import com.vcat.module.ec.dto.WithdrawDto;
import com.vcat.module.ec.entity.Withdrawal;
@Service
@Transactional(readOnly = true)
public class WithdrawalService extends CrudService<Withdrawal> {

	@Autowired
	private WithdrawalDao withdrawalDao;

	protected CrudDao<Withdrawal> getDao() {
		return withdrawalDao;
	}
	//获取提现记录并分页
	public List<WithdrawDto> getWithdrawalLogList(Pager page, String shopId,
			String month) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("shopId", shopId);
		map.put("month", month);
		return withdrawalDao.getWithdrawalLogList(map);
	}
	//获取店铺提现记录总数
	public int count(String shopId,String month){
		Integer count  = withdrawalDao.count(shopId,month);
		return count==null?0:count;
	}
	public List<WithdrawDto> getWithdrawalLogList2(Pager page, String shopId,
			String month) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("shopId", shopId);
		map.put("month", month);
		return withdrawalDao.getWithdrawalLogList2(map);
	}	
}
