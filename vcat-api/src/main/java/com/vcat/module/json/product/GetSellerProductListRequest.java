package com.vcat.module.json.product;

/**
 * Created by Code.Ai on 16/5/22.
 * Description:
 */
public class GetSellerProductListRequest {
    private String categoryId;
    private String productType;
    private String shopId;
    private int pageNo;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
