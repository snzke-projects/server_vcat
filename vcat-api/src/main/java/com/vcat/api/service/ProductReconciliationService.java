package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ProductReconciliationDao;
import com.vcat.module.ec.dao.ReconciliationDetailDao;
import com.vcat.module.ec.entity.ProductReconciliation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductReconciliationService extends CrudService<ProductReconciliation> {
	@Autowired
    protected ProductReconciliationDao productReconciliationDao;
    @Autowired
    private ReconciliationDetailDao reconciliationDetailDao;
	@Override
	protected CrudDao<ProductReconciliation> getDao() {
		return productReconciliationDao;
	}


    /**
     * 结算需要提前结算的订单项
     * @param productItemId
     * @param orderItemId
     * @param quantity
     * @param orderNumber
     */
    @Transactional(readOnly = false)
    public void reconciliation(String productItemId,String orderItemId,int quantity,String orderNumber) {
        ProductReconciliation reconciliation = new ProductReconciliation();
        reconciliation.setId(productItemId);
        reconciliation = productReconciliationDao.get(reconciliation);
        if(null != reconciliation && reconciliation.getSurplusQuantity() > quantity){
            // 结算该订单项
            reconciliationDetailDao.insertByCheckOrder(orderItemId);
            reconciliation.getSqlMap().put("currentUsedQuantity", quantity + "");
            productReconciliationDao.subtractSurplusQuantity(reconciliation);
            reconciliation.setSurplusQuantity(0 - quantity);
            reconciliation.getSqlMap().put("note", "订单[" + orderNumber + "]划账，系统自动结算");
            productReconciliationDao.insertLog(reconciliation);
        }
    }

    /**
     * 更新订单结算状态
     * @param orderId
     */
    @Transactional(readOnly = false)
    public void updateReconciliationStatus(String orderId) {
        reconciliationDetailDao.updateReconciliationStatus(orderId);
    }
}
