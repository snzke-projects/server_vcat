package com.vcat.module.json.product;

import com.vcat.module.core.entity.RequestEntity;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/4/12.
 * Description:
 */
public class GetMyShopProductsRequest  {

    /**
     * productType : all
     * pageNo : 1
     * loadType : 0
     * sortType : sales
     */
    private String productType = "all";
    @NotNull
    private String pageNo;
    private String loadType;
    private String sortType = "new"; //sales , all , new , collection,time,saleEarning
    private String categoryId;
    private String condition;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setProductType(String productType) { this.productType = productType;}

    public void setPageNo(String pageNo) { this.pageNo = pageNo;}

    public void setLoadType(String loadType) { this.loadType = loadType;}

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getProductType() { return productType;}

    public String getPageNo() { return pageNo;}

    public String getLoadType() { return loadType;}

    public String getSortType() { return sortType;}
}
