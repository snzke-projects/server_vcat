package com.vcat.module.json.product;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
public class CheckGroupBuyProductRequest {
    @NotNull
    private int type; // 零元开团=0,卖家开团=8,买家开团/参团=9
    private int productCount;
    private String groupBuyId;
    private String shopId;
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

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
