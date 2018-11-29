package com.vcat.module.ec.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.CustomerRole;

@MyBatisDao
public interface CustomerRoleDao extends CrudDao<CustomerRole> {

	List<CustomerRole> findRoleList(String customerId);

	CustomerRole findRole(@Param("customerId")String customerId, @Param("roleName")String roleName);

	void deleteRole(@Param("customerId")String customerId, @Param("roleName")String roleName);
}
