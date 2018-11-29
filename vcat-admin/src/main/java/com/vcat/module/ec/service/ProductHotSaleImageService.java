package com.vcat.module.ec.service;

import com.vcat.common.utils.IdGen;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ImageDao;
import com.vcat.module.ec.dao.ProductHotSaleImageDao;
import com.vcat.module.ec.entity.ProductHotSaleImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductHotSaleImageService extends CrudService<ProductHotSaleImageDao, ProductHotSaleImage> {
    @Autowired
    private ImageDao imageDao;

	public List<ProductHotSaleImage> findListByProductId(String id){
		return dao.findListByProductId(id);
	}

	@Transactional(readOnly = false)
	public void save(ProductHotSaleImage productImage) {
        productImage.preInsert();
        imageDao.insert(productImage);
		productImage.getSqlMap().put("productImageId", IdGen.uuid());
		dao.insertProductHotSaleImage(productImage);
    }

	@Transactional(readOnly = false)
	public void save(List<ProductHotSaleImage> productImagesList){
		if(null == productImagesList || productImagesList.isEmpty()){
			return;
		}
		for(int i = 0;i < productImagesList.size(); i++){
			ProductHotSaleImage productImage = productImagesList.get(i);
			productImage.setDisplayOrder((productImagesList.size()+1) * 10 - 10 * (i + 1));
			save(productImage);
		}
	}

	@Transactional(readOnly = false)
	public int deleteProductHotSaleImage(String productId){
        List<String> idList = dao.findHotSaleImageIdList(productId);
		int result = dao.deleteProductHotSaleImage(productId);
		imageDao.deleteImage(idList);
		return result;
	}

}
