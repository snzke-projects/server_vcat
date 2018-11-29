package com.vcat.module.ec.dao;

import com.vcat.common.persistence.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface ServerConfigDao {
    String findCfgValue(@Param("cfgName")String cfgName);
}
