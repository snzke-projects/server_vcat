package com.vcat.module.json.product;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/10.
 * Description:
 */
public class GetGroupBuyProductDetailRequest {
    @NotNull
    private int    type;  // 1:店主开团商品详情页;2:买家开团商品详情页;3:买家参团商品详情页
    private String shopId;
    private String groupBuyId;
    private String groupBuySponsorId;

    public String getGroupBuySponsorId() {
        return groupBuySponsorId;
    }

    public void setGroupBuySponsorId(String groupBuySponsorId) {
        this.groupBuySponsorId = groupBuySponsorId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getGroupBuyId() {
        return groupBuyId;
    }

    public void setGroupBuyId(String groupBuyId) {
        this.groupBuyId = groupBuyId;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
