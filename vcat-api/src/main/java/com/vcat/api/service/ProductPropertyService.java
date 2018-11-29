package com.vcat.api.service;

import java.util.List;

import com.vcat.config.CacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ProductPropertyDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductProperty;
@Service
@Transactional(readOnly = true)
public class ProductPropertyService extends CrudService<ProductProperty> {

	@Autowired
	private ProductPropertyDao productPropertyDao;

	@Override
	protected CrudDao<ProductProperty> getDao() {
		return productPropertyDao;
	}

	@Cacheable(value = CacheConfig.FIND_LIST_BY_PRODUCT_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public List<ProductProperty> findListByProduct(Product product) {
		return productPropertyDao.findListByProduct(product);
	}
}
