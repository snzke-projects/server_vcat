package com.vcat.module.json.product;

import com.vcat.module.core.entity.RequestEntity;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/4/12.
 * Description:
 */
public class GetMyReserveProductsRequest extends RequestEntity {
    private String pageNo;
    private String productItemId;

    public String getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(String productItemId) {
        this.productItemId = productItemId;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }
}
