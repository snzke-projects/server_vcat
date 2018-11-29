package com.vcat.module.json.product;


import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/12.
 * Description:
 */
public class GetSellerGroupBuyProductRequest {
    @NotNull
    private int type;  // 1:店主开团商品详情页;2:买家开团商品详情页;3:买家参团商品详情页
    @NotNull
    private String productId;
    @NotNull
    private String productItemId;
    @NotNull
    private String groupBuyId;
    @NotNull
    private String shopId;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(String productItemId) {
        this.productItemId = productItemId;
    }

    public String getGroupBuyId() {
        return groupBuyId;
    }

    public void setGroupBuyId(String groupBuyId) {
        this.groupBuyId = groupBuyId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
