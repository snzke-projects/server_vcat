package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Address;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@MyBatisDao
public interface AddressDao extends CrudDao<Address> {

	void deleteById(String addressId);
	Address getOrderAddress(@Param("orderId")String orderId,@Param("buyerId")String buyerId);
}
