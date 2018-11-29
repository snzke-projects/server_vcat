package com.vcat.module.ec.entity;

/**
 * 商品图片Entity
 */
public class ProductImage extends Image<ProductImage> {
	private static final long serialVersionUID = 1L;
	private Integer displayOrder;// 图片排序

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
}
