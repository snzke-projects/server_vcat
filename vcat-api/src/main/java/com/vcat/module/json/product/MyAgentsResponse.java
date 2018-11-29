package com.vcat.module.json.product;

import com.vcat.common.persistence.Pager;
import com.vcat.module.core.entity.ResponseEntity;

/**
 * Created by: dong4j.
 * Date: 2016-11-25.
 * Time: 22:50.
 * Description:
 */
public class MyAgentsResponse extends ResponseEntity {
    private String monthOrderType;
    private String allOrderType;
    private Pager page;

    public Pager getPage() {
        return page;
    }

    public void setPage(Pager page) {
        this.page = page;
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
}
