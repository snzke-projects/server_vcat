package com.vcat.module.ec.service;

import com.vcat.common.utils.IdGen;
import com.vcat.module.core.service.LRTreeService;
import com.vcat.module.ec.dao.ProductCategoryDao;
import com.vcat.module.ec.entity.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品Service
 */
@Service
@Transactional(readOnly = true)
public class ProductCategoryService extends LRTreeService<ProductCategoryDao, ProductCategory> {
	@Autowired
	private ProductPropertyService productPropertyService;

    @Transactional(readOnly = false)
	public void save(ProductCategory category) {
        super.save(category);
        productPropertyService.saveCategoryProperty(category.getId(), category.getPropertyList());
	}

	@Transactional(readOnly = false)
	public void addProductCategory(ProductCategory productCategory){
		productCategory.getSqlMap().put("uuid", IdGen.uuid());
		dao.deleteProductCategory(productCategory.getSqlMap().get("productId"));
		dao.addProductCategory(productCategory);
	}

    public int getHighestOrder(String parentId) {
        return dao.getHighestOrder(parentId);
    }

    /**
     * 激活或取消激活该商品分类
     * @param productCategory
     */
    @Transactional(readOnly = false)
    public void activate(ProductCategory productCategory) {
        Integer activate = productCategory.getIsActivate();
        // 如果取消激活，则
        if(0 == activate){
            dao.archivedProductWhenCancelActivate(productCategory);
        }
        productCategory = get(productCategory);
        productCategory.setIsActivate(activate);
        dao.activate(productCategory);
    }
}
