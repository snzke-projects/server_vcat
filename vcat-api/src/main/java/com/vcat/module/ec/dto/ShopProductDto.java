package com.vcat.module.ec.dto;

import com.vcat.common.cloud.QCloudUtils;

import java.io.Serializable;

/**
 * Created by Code.Ai on 16/4/12.
 * Description:
 */
public class ShopProductDto implements Serializable {
    private String name;
    private String mainUrl;
    private Integer inventory;
    private String size;
    private String productId;
    private String productItemId;

    public String getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(String productItemId) {
        this.productItemId = productItemId;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public String getMainUrl() {
        return QCloudUtils.createThumbDownloadUrl(mainUrl, 200);
    }
    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
