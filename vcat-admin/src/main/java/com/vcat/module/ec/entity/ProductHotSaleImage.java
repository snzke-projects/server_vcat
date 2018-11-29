package com.vcat.module.ec.entity;

/**
 * 爆品图片Entity
 */
public class ProductHotSaleImage extends Image<ProductHotSaleImage> {
	private static final long serialVersionUID = 1L;
	private Integer displayOrder;// 图片排序

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
}
