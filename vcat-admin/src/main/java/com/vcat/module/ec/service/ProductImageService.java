package com.vcat.module.ec.service;

import com.vcat.common.utils.IdGen;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ImageDao;
import com.vcat.module.ec.dao.ProductImageDao;
import com.vcat.module.ec.entity.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductImageService extends CrudService<ProductImageDao, ProductImage> {
    @Autowired
    private ImageDao imageDao;

	public List<ProductImage> findListByProductId(String id){
		return dao.findListByProductId(id);
	}

	@Transactional(readOnly = false)
	public void save(ProductImage productImage) {
        productImage.preInsert();
        imageDao.insert(productImage);
		productImage.getSqlMap().put("productImageId", IdGen.uuid());
		dao.insertProductImage(productImage);
    }

	@Transactional(readOnly = false)
	public void save(List<ProductImage> productImagesList){
		if(null == productImagesList || productImagesList.isEmpty()){
			return;
		}
		for(int i = 0;i < productImagesList.size(); i++){
			ProductImage productImage = productImagesList.get(i);
			productImage.setDisplayOrder((productImagesList.size()+1) * 10 - 10 * (i + 1));
			save(productImage);
		}
	}

	@Transactional(readOnly = false)
	public int deleteProductImage(String productId){
        List<String> idList = dao.findImageIdList(productId);
		int result = dao.deleteProductImage(productId);
		imageDao.deleteImage(idList);
		return result;
	}

}
