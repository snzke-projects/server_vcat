package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

public class RatingSummary extends DataEntity<RatingSummary>{
	private static final long serialVersionUID = 6018439373705411519L;
    private Product product;
	private Integer averageRating;//平均评分

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getAveragaReting() {
		return averageRating;
	}
	public void setAverageRating(Integer averageRating) {
		this.averageRating = averageRating;
	}

}
