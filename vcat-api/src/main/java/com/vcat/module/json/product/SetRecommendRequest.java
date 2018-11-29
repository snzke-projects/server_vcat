package com.vcat.module.json.product;

import com.vcat.module.core.entity.RequestEntity;

import javax.validation.constraints.NotNull;

public class SetRecommendRequest extends RequestEntity {
    @NotNull
    private String type;
    @NotNull
    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SetRecommendRequest(){

    }

    public SetRecommendRequest(String productId, String type) {
        this.productId = productId;
        this.type = type;
    }
}
