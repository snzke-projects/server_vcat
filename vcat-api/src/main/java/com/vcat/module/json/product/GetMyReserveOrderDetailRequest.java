package com.vcat.module.json.product;

import com.vcat.module.core.entity.ResponseEntity;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/4/13.
 * Description:
 */
public class GetMyReserveOrderDetailRequest  {
    @NotNull
    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
