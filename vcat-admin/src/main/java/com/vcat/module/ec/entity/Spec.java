package com.vcat.module.ec.entity;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;

/**
 * 规格属性
 */
public class Spec extends DataEntity<Spec> {
	private static final long serialVersionUID = 1L;
	private ProductCategory category;	// 规格所属分类
	private ProductItem productItem;	// 所属商品规格
	private String name;				// 属性名称
	private String value;				// 属性值
    private boolean editable;           // 属性是否可编辑

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public ProductItem getProductItem() {
		return productItem;
	}

	public void setProductItem(ProductItem productItem) {
		this.productItem = productItem;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String[] getValueArray() {
        if(StringUtils.isEmpty(value)){
            return new String[]{};
        }
        return value.split(",");
    }
}