package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ProductItemDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductItem;
import com.vcat.module.ec.entity.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductItemService extends CrudService<ProductItemDao, ProductItem> {
    @Autowired
    private SpecService specService;
    @Autowired
    private DataChangeLogService dataChangeLogService;

    @Transactional(readOnly = false)
    public void save(ProductItem productItem, String saveSource) {
        ProductItem before = get(productItem);
        Integer type = productItem.getProductItemType();
        if(null == type){
            productItem.setProductItemType(0);
        }
        dataChangeLogService.saveLog("ec_product_item", saveSource, before, productItem);
        super.save(productItem);
        if (null != productItem && null != productItem.getSpecList() && !productItem.getSpecList().isEmpty()) {
            specService.deleteByProductItemId(productItem.getId());
            for (Spec spec : productItem.getSpecList()) {
                spec.setProductItem(productItem);
                specService.insertSpecValue(spec);
            }
        }
    }

    public List<ProductItem> findListByProductId(Product product) {
        if (null == product.getSqlMap().get("productItemType")){
            product.getSqlMap().put("productItemType", "0");
        }
        return dao.findListByProduct(product);
    }

    @Transactional(readOnly = false)
    public void delete(ProductItem productItem) {
        specService.deleteByProductItemId(productItem.getId());
        dao.delete(productItem);
    }

    @Transactional(readOnly = false)
    public void deleteByProduct(Product product) {
        specService.deleteByProductId(product.getId());
        dao.deleteByProduct(product);
    }

    public List<ProductItem> findListByIds(String[] idArray) {
        return dao.findListByIds(idArray);
    }

    /**
     * 退货完成时增加规格库存
     * @param id
     * @param quantity
     */
    public void addQuantityByRefund(String id, Integer quantity) {
        dao.addQuantityByRefund(id,quantity);
    }

    @Override
    public List<ProductItem> findList(ProductItem entity) {
        if (null == entity.getProductItemType()){
            entity.setProductItemType(0);
        }
        return super.findList(entity);
    }
}
