package com.vcat.module.json.product;

import com.vcat.module.core.entity.RequestEntity;

public class GetVcatShopProductListRequest extends RequestEntity {


    /**
     * productType : hot
     * sortType : default
     * categoryId : 19bc19f97dd744bc8a4caec09a496110
     * pageNo : 1
     */

    private String productType;
    private String sortType;
    private String categoryId;
    private String pageNo;
    private String brandId;
    private String couponType;
    private String childCategoryId;

    public String getChildCategoryId() {
        return childCategoryId;
    }

    public void setChildCategoryId(String childCategoryId) {
        this.childCategoryId = childCategoryId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public void setProductType(String productType) { this.productType = productType;}

    public void setSortType(String sortType) { this.sortType = sortType;}

    public void setCategoryId(String categoryId) { this.categoryId = categoryId;}

    public void setPageNo(String pageNo) { this.pageNo = pageNo;}

    public String getProductType() { return productType;}

    public String getSortType() { return sortType;}

    public String getCategoryId() { return categoryId;}

    public String getPageNo() { return pageNo;}
}
