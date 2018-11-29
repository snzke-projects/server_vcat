package com.vcat.module.ec.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Brand;

@MyBatisDao
public interface BrandDao extends CrudDao<Brand> {
	List<Map<String, Object>> getBrandList(@Param("shopId")String shopId,@Param("guruId")String guruId);
}
