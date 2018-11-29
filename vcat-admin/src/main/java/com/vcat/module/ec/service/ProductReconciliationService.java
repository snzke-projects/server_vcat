package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ProductReconciliationDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductItem;
import com.vcat.module.ec.entity.ProductReconciliation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductReconciliationService extends CrudService<ProductReconciliationDao, ProductReconciliation> {

    @Transactional(readOnly = false)
    public void saveList(String[] itemIdArray,String[] productIdArray,int[] surplusQuantityArray) {
        if(null == itemIdArray || null == productIdArray || null == surplusQuantityArray
                || itemIdArray.length == 0 || productIdArray.length == 0 || surplusQuantityArray.length == 0
                || itemIdArray.length != productIdArray.length || productIdArray.length != surplusQuantityArray.length){
            return;
        }
        for (int i = 0; i < itemIdArray.length; i++) {
            String itemId = itemIdArray[i];
            String productId = productIdArray[i];
            int surplusQuantity = surplusQuantityArray[i];
            ProductReconciliation reconciliation = new ProductReconciliation();
            ProductItem item = new ProductItem();
            Product product = new Product();
            item.setId(itemId);
            reconciliation.setId(itemId);
            product.setId(productId);
            item.setProduct(product);
            reconciliation.setProductItem(item);
            reconciliation.setSurplusQuantity(surplusQuantity);
            reconciliation.getSqlMap().put("note", "首次增加提前结算数量");
            dao.insert(reconciliation);
            dao.insertLog(reconciliation);
        }
    }

    @Transactional(readOnly = false)
    public void addReconciliation(String[] itemIdArray, int[] surplusQuantityArray) {
        if(null == itemIdArray ||  null == surplusQuantityArray
                || itemIdArray.length == 0 || surplusQuantityArray.length == 0
                || itemIdArray.length != surplusQuantityArray.length){
            return;
        }

        for (int i = 0; i < itemIdArray.length; i++) {
            String itemId = itemIdArray[i];
            int surplusQuantity = surplusQuantityArray[i];
            ProductReconciliation reconciliation = new ProductReconciliation();
            ProductItem item = new ProductItem();
            item.setId(itemId);
            reconciliation.setId(itemId);
            reconciliation.setProductItem(item);
            reconciliation.setSurplusQuantity(surplusQuantity);
            reconciliation.getSqlMap().put("note", "手动增加提前结算数量");
            dao.addSurplusQuantity(reconciliation);
            dao.insertLog(reconciliation);
        }
    }
}
