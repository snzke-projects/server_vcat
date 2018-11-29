package com.vcat.module.ec.dao;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.AppDomain;

import java.util.Map;

@MyBatisDao
public interface AppDomainDao extends CrudDao<AppDomain> {
	

	Map<String, Object> getByDeviceTypeAndAppVersion(@Param("deviceType") String deviceType, @Param("appVersion")String appVersion);


}
