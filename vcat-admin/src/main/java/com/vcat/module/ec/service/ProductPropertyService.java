package com.vcat.module.ec.service;

import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ProductCategoryDao;
import com.vcat.module.ec.dao.ProductPropertyDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductCategory;
import com.vcat.module.ec.entity.ProductProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 商品属性Service
 */
@Service
@Transactional(readOnly = true)
public class ProductPropertyService extends CrudService<ProductPropertyDao, ProductProperty> {
    @Autowired
    private ProductCategoryDao categoryDao;
	public List<ProductProperty> findListByProduct(Product product){
		List<ProductProperty> categoryProperty = findListByCategory(product.getCategory(),true);
		List<ProductProperty> productProperty = dao.findListByProduct(product);
		Map<String,ProductProperty> resultMap = new LinkedHashMap<>();
		List<ProductProperty> result = new ArrayList<>();
		// 倒序排列获取的分类属性
		Collections.sort(categoryProperty,(ProductProperty o1,ProductProperty o2) -> -1);
		for (int i = 0; i < productProperty.size(); i++) {
			resultMap.put(productProperty.get(i).getName(),productProperty.get(i));
		}
		for (int i = 0; i < categoryProperty.size(); i++) {
			ProductProperty p = categoryProperty.get(i);
			if(resultMap.containsKey(p.getName())){
                resultMap.get(p.getName()).setIsCustom(false);
			}else{
                resultMap.put(p.getName(), p);
            }
		}
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()){
			result.add(resultMap.get(it.next()));
		}
		return result;
	}

	private List<ProductProperty> findListByCategory(ProductCategory category,boolean editable) {
		List<ProductProperty> list = dao.findListByCategory(category,editable);
		if(null != category && !category.getIsRoot()){
			list.addAll(findListByCategory(categoryDao.findParent(category),false));
		}
		return list;
	}

	public List<ProductProperty> findCategoryProperty(String categoryId){
		ProductCategory category = new ProductCategory();
		category.setId(categoryId);
		List<ProductProperty> list = findListByCategory(category,true);
		// 倒序排列获取的分类属性
		Collections.sort(list,(ProductProperty o1,ProductProperty o2) -> -1);
		return list;
	}
	public List<ProductProperty> findAll(){
		return dao.findAll();
	}

	@Override
	public ProductProperty get(ProductProperty entity) {
		return super.get(entity);
	}

	@Override
	public ProductProperty get(String id) {
		ProductProperty productProperty = new ProductProperty();
		productProperty.setId(id);
		return get(productProperty);
	}

	@Transactional(readOnly = false)
	public void save(ProductProperty productProperty) {
		if (productProperty.getIsNewRecord()) {
			ProductProperty newProperty = get(productProperty);
			if (null != newProperty && StringUtils.isNotBlank(newProperty.getId())){
				return;
			}
		}
		super.save(productProperty);
	}

	@Transactional(readOnly = false)
	public void saveCategoryProperty(String categoryId,List<ProductProperty> propertyList){
		dao.deleteCategoryProperty(categoryId);
        if(null == propertyList){
            return;
        }
		for (ProductProperty productProperty : propertyList){
			if(StringUtils.isEmpty(productProperty.getId())){
				continue;
			}
			productProperty.getSqlMap().put("categoryId",categoryId);
			productProperty.getSqlMap().put("categoryPropertyId",IdGen.uuid());
			dao.insertCategoryProperty(productProperty);
		}
	}

	@Transactional(readOnly = false)
	public void saveProductProperty(String productId,List<ProductProperty> propertyList){
		dao.deleteProductProperty(productId);
		if(null == propertyList || propertyList.isEmpty()){
			return;
		}
        int index = 1;
		for (ProductProperty productProperty : propertyList){
			if(StringUtils.isEmpty(productProperty.getId()) || StringUtils.isEmpty(productProperty.getValue())){
				continue;
			}
			productProperty.getSqlMap().put("productValueId",IdGen.uuid());
			productProperty.getSqlMap().put("productId",productId);
            productProperty.setDisplayOrder(index);
			dao.insertProductProperty(productProperty);
            index++;
		}
	}
}
