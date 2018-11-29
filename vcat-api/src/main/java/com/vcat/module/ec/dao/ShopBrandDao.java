package com.vcat.module.ec.dao;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ShopBrand;

@MyBatisDao
public interface ShopBrandDao extends CrudDao<ShopBrand> {

	void deleteBrand(@Param("shopId") String shopId, @Param("brandId")String suId);

	void updateBrand(@Param("shopId") String shopId, @Param("brandId")String suId);

	void batchDelete(@Param("shopId") String shopId);
}
