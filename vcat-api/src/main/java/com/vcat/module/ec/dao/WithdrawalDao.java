package com.vcat.module.ec.dao;

import java.util.List;
import java.util.Map;

import com.vcat.module.ec.entity.WithdrawalExcel;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.WithdrawDto;
import com.vcat.module.ec.entity.Withdrawal;

@MyBatisDao
public interface WithdrawalDao extends CrudDao<Withdrawal> {

	List<WithdrawDto> getWithdrawalLogList(Map<String, Object> map);
	
	Integer count(@Param("shopId")String shopId,@Param("month")String month);

	Withdrawal getById(String id);

	void handler(Withdrawal withdrawal);

    List<WithdrawalExcel> exprotList(Withdrawal withdrawal);

	List<WithdrawDto> getWithdrawalLogList2(Map<String, Object> map);
}
