package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 提前结算数量变更日志
 */
public class ReconciliationQuantityLog extends DataEntity<ReconciliationQuantityLog> {
	private static final long serialVersionUID = 1L;
    private ProductReconciliation reconciliation;   // 提前结算对象
    private int quantity;                           // 变更数量
    private String note;                            // 变更日志

    public ProductReconciliation getReconciliation() {
        return reconciliation;
    }

    public void setReconciliation(ProductReconciliation reconciliation) {
        this.reconciliation = reconciliation;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
