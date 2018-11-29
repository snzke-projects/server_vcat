package com.vcat.module.ec.dao;

import java.util.List;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Express;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface ExpressDao extends CrudDao<Express> {

	Express findByCode(String expressCode);
	List<Express> findByName(@Param("name")String name);

	List<Express> getExpressList();

}
