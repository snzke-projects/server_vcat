package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 规格提前结算
 */
public class ProductReconciliation extends DataEntity<ProductReconciliation> {
	private static final long serialVersionUID = 1L;
    private ProductItem productItem;    // 对应规格
    private int surplusQuantity;        // 剩余提前结算数量
    private int usedQuantity;           // 已提前结算数量

    public ProductItem getProductItem() {
        return productItem;
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public int getSurplusQuantity() {
        return surplusQuantity;
    }

    public void setSurplusQuantity(int surplusQuantity) {
        this.surplusQuantity = surplusQuantity;
    }

    public int getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(int usedQuantity) {
        this.usedQuantity = usedQuantity;
    }
}
