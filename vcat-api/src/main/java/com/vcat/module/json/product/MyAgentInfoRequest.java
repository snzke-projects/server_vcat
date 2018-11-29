package com.vcat.module.json.product;

import com.vcat.module.core.entity.RequestEntity;

import javax.validation.constraints.NotNull;

/**
 * Created by: dong4j.
 * Date: 2016-12-02.
 * Time: 22:59.
 * Description:
 */
public class MyAgentInfoRequest extends RequestEntity {
    @NotNull
    private String shopId;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
