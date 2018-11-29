package com.vcat.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.FavoritesDao;
import com.vcat.module.ec.dto.ProductDto;
import com.vcat.module.ec.dto.ShopCollectDto;
import com.vcat.module.ec.entity.Favorites;
@Service
@Transactional(readOnly = true)
public class FavoritesService extends CrudService<Favorites> {

	@Autowired
	private FavoritesDao favoritesDao;
	@Autowired
	private ProductService productService;
	@Override
	protected CrudDao<Favorites> getDao() {
		return favoritesDao;
	}
	public int countShopCollect(String shopId) {
		return favoritesDao.countShopCollect(shopId);
	}
	public List<ShopCollectDto> getShopCollect(String buyerId, Pager page) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("buyerId", buyerId);
		map.put("page", page);
		return favoritesDao.getShopCollect(map);
	}
	public int countProductCollect(String shopId) {
		return favoritesDao.countProductCollect(shopId);
	}
	public List<ProductDto> getProductCollect(String buyerId, Pager page) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("buyerId", buyerId);
		map.put("page", page);
		return favoritesDao.getProductCollect(map);
	}
	@Transactional(readOnly = false)
	public void saveProduct(Favorites favorite) {
		favoritesDao.insert(favorite);
		int count = 1;
		//更新商品的收藏数加1
		productService.updateCollectCount(favorite.getProduct().getId(),count);
	}
	@Transactional(readOnly = false)
	public void deleteProduct(Favorites favorite) {
		favoritesDao.delete(favorite);
		int count = -1;
		//更新商品的收藏数加1
		productService.updateCollectCount(favorite.getProduct().getId(),count);
	}

}
