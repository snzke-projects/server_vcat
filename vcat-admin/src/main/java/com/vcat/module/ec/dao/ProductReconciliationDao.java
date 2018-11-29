package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ProductReconciliation;

@MyBatisDao
public interface ProductReconciliationDao extends CrudDao<ProductReconciliation> {
    /**
     * 插入提前结算变更日志
     * @param reconciliation
     */
    void insertLog(ProductReconciliation reconciliation);

    /**
     * 添加提前结算数量
     * @param reconciliation
     */
    void addSurplusQuantity(ProductReconciliation reconciliation);
}
