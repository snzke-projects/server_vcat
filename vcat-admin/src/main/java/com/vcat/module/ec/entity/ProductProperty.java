package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 商品属性
 */
public class ProductProperty extends DataEntity<ProductProperty> {
    private static final long serialVersionUID = 1L;
    private ProductCategory category;   // 属性所属分类
    private Product product;            // 属性所属商品
    private String name;                // 属性名
    private String value;               // 属性值
    private boolean isCustom;           // 是否为商品专属属性
    private boolean editable;           // 该属性是否可编辑（删除）
    private int displayOrder;           // 商品属性排序

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
