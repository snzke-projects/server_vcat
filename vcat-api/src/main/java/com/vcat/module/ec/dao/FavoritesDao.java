package com.vcat.module.ec.dao;

import java.util.List;
import java.util.Map;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.ProductDto;
import com.vcat.module.ec.dto.ShopCollectDto;
import com.vcat.module.ec.entity.Favorites;

@MyBatisDao
public interface FavoritesDao extends CrudDao<Favorites> {

	int countShopCollect(String shopId);

	List<ShopCollectDto> getShopCollect(Map<String, Object> map);

	int countProductCollect(String shopId);

	List<ProductDto> getProductCollect(Map<String, Object> map);

}
