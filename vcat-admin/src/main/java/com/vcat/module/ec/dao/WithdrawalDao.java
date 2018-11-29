package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Withdrawal;
import com.vcat.module.ec.entity.WithdrawalExcel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface WithdrawalDao extends CrudDao<Withdrawal> {
	Integer count(@Param("shopId")String shopId,@Param("month")String month);

	Withdrawal getById(String id);

	void handler(Withdrawal withdrawal);

    List<WithdrawalExcel> exprotList(Withdrawal withdrawal);
}
