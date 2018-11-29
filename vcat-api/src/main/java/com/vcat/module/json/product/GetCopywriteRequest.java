package com.vcat.module.json.product;
import com.vcat.module.core.entity.RequestEntity;

import javax.validation.constraints.NotNull;
/**
 * Created by Code.Ai on 16/4/11.
 * Description:
 */
public class GetCopywriteRequest extends RequestEntity {

    /**
     * productId : 9e9f2191368e48d7943700831c932dd4
     */
    @NotNull
    private String productId;
    @NotNull
    private String pageNo;

    public void setProductId(String productId) { this.productId = productId;}

    public String getProductId() { return productId;}

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }
}
