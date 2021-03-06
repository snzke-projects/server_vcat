package com.vcat.module.ec.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 爆品图片Entity
 */
public class ProductHotSaleImage extends Image<ProductHotSaleImage> {
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.JSON_STYLE);
	}

	private static final long serialVersionUID = 1L;
	private Integer displayOrder;// 图片排序

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
}
