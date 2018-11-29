package com.vcat.api.service;

import java.util.List;

import com.vcat.config.CacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ProductImageDao;
import com.vcat.module.ec.entity.ProductImage;
@Service
@Transactional(readOnly = true)
public class ProductImageService extends CrudService<ProductImage> {

	@Autowired
	private ProductImageDao productDao;

	@Override
	protected CrudDao<ProductImage> getDao() {
		return productDao;
	}

	@Cacheable(value = CacheConfig.FIND_LIST_BY_PRODUCT_ID_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public List<ProductImage> findListByProductId(String id) {
		return productDao.findListByProductId(id);
	}
}
