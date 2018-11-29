package com.vcat.module.json.product;

import com.vcat.module.core.entity.RequestEntity;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/4/11.
 * Description:
 */
public class SetDefaultCopywriteRequest  {
    @NotNull
    private String type;
    @NotNull
    private String productId;

    private String copywriteId;

    public String getCopywriteId() {
        return copywriteId;
    }

    public void setCopywriteId(String copywriteId) {
        this.copywriteId = copywriteId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
