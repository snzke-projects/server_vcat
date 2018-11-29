package com.vcat.module.json.product;

import com.vcat.module.core.entity.RequestEntity;

import javax.validation.constraints.NotNull;

/**
 * Created by: dong4j.
 * Date: 2016-11-25.
 * Time: 22:31.
 * Description:
 */
public class MyAgentsRequest extends RequestEntity {
    @NotNull
    private int type;
    @NotNull
    private String pageNo;
    // 查询条件
    private String name = "";
    // 排序方式
    private String monthOrderType ;
    private String allOrderType ;
    private String shopId;
    @NotNull
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonthOrderType() {
        return monthOrderType;
    }

    public void setMonthOrderType(String monthOrderType) {
        this.monthOrderType = monthOrderType;
    }

    public String getAllOrderType() {
        return allOrderType;
    }

    public void setAllOrderType(String allOrderType) {
        this.allOrderType = allOrderType;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
