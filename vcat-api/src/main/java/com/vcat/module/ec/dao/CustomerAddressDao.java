package com.vcat.module.ec.dao;

import java.util.List;
import java.util.Map;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Address;
import com.vcat.module.ec.entity.CustomerAddress;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface CustomerAddressDao extends CrudDao<CustomerAddress> {

	void cancalDefault(String buyerId);

	void updateDefault(CustomerAddress customerAddress);

	Map<String,Object> countAddress(String buyerId);

	void deleteBuyerId(Map<String, Object> map);
	
	List<Address> getAddressList(@Param("buyerId")String buyerId,@Param("page") Pager page,@Param("condition")String condition);
	
	Address getDefaultAddressList(String buyerId);

    CustomerAddress getByCustomerAndAddress(CustomerAddress customerAddress);
	int getAddressCounts(@Param("buyerId")String buyerId,@Param("condition")String condition);
	Address getChooseAddress(@Param("buyerId")String buyerId, @Param("addressId")String addressId);
}
