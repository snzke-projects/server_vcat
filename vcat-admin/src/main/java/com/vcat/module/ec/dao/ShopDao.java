package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Shop;

import java.util.List;

@MyBatisDao
public interface ShopDao extends CrudDao<Shop> {
	//计算数量
	Integer count();

	List<String> getFriendsById(String pid);
}
