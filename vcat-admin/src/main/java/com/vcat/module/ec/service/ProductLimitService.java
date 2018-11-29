package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.service.ServiceException;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.dao.ProductLimitDao;
import com.vcat.module.ec.entity.ProductLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductLimitService extends CrudService<ProductLimitDao, ProductLimit> {
    @Autowired
    private ProductItemService productItemService;
    @Transactional(readOnly = false)
    public void save(ProductLimit entity) {
        String productItemId = entity.getProductItem().getId();
        String[] idArray = productItemId.split("\\|");
        if(idArray.length > 2 && entity.getIsNewRecord()){
            for (String id : idArray) {
                if(!StringUtils.isBlank(id)){
                    entity.setId(null);
                    entity.getProductItem().setId(id);
                    saveEntity(entity);
                }
            }
        }else {
            entity.getProductItem().setId(idArray.length == 2 ? idArray[1] : idArray[0]);
            saveEntity(entity);
        }
    }
    private void saveEntity(ProductLimit entity){
        entity.setProductItem(productItemService.get(entity.getProductItem()));
        if(dao.hasSameLimit(entity)){
            throw new ServiceException(DictUtils.getDictLabel(entity.getProductType().toString(),"ec_product_limit_type","该区域") + " 中已经设置【"
                    + entity.getProductItem().getProduct().getName() + "】【" + entity.getProductItem().getName() + "】商品限购，不能重复设置！");
        }
        super.save(entity);
    }

    @Transactional(readOnly = false)
    public void deleteByGroupBuying(String productItemId){
        dao.deleteByGroupBuying(productItemId);
    }
}
