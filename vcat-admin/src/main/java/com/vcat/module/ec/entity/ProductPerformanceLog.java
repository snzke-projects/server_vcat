package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

public class ProductPerformanceLog extends DataEntity<ProductPerformanceLog>{
	private static final long serialVersionUID = -1871756419667373588L;
    private Product product;    // 日志所属商品
    private Integer sales;      // 新增销量
    private Integer shelves;    // 新增上架数
    private Integer reviewCount;// 新增评论数量

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getShelves() {
        return shelves;
    }

    public void setShelves(Integer shelves) {
        this.shelves = shelves;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
