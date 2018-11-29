package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ProductCategoryDao;
import com.vcat.module.ec.entity.Brand;
import com.vcat.module.ec.entity.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProductCategoryService extends CrudService<ProductCategory>{

	@Autowired
	private ProductCategoryDao productCategoryDao;
	@Override
	protected CrudDao<ProductCategory> getDao() {
		return productCategoryDao;
	}
	public List<ProductCategory> getListByCustomer(String customerId) {
		
		return productCategoryDao.getListByCustomer(customerId);
	}
	
	public List<Map<String, Object>> findCategoryList(String categoryId) {
		
		return productCategoryDao.findCategoryList(categoryId);
	}
	
	public List<ProductCategory> getListByShopId(String shopId) {
		List<ProductCategory> list = getListByCustomer(shopId);
		if(list!=null&&list.size()>0){
			for(ProductCategory category:list){
				List<ProductCategory> childs = productCategoryDao.getListByShopId(shopId, category.getId());
				category.setChilds(childs);
			}
		}
		return list;
	}
	public List<Brand> findBrandList(String parentCategoryId) {
		return productCategoryDao.findBrandList(parentCategoryId);
	}

}
