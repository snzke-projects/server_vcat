package com.vcat.module.json.product;

import com.drew.lang.annotations.NotNull;
import com.vcat.module.core.entity.RequestEntity;

public class GetSellerProductDetailRequest extends RequestEntity {

    /**
     * shopId :
     * productId :
     */
    @NotNull
    private String shopId;
    @NotNull
    private String productId;

    public void setShopId(String shopId) { this.shopId = shopId;}

    public void setProductId(String productId) { this.productId = productId;}

    public String getShopId() { return shopId;}

    public String getProductId() { return productId;}
}
