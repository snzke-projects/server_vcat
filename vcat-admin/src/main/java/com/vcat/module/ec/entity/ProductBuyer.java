package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

public class ProductBuyer extends DataEntity<ProductBuyer> {
	private static final long serialVersionUID = 1L;
    private String supplierId;  // 供应商ID
    private String brandId;     // 品牌ID
    private String buyerId;
    private String orderId;
    @ExcelField(title = "商品名称", align = 2, sort = 10)
    private String productName;
    @ExcelField(title = "购买用户", align = 2, sort = 20)
    private String buyerName;
    @ExcelField(title = "用户手机", align = 2, sort = 30)
    private String buyerPhone;
    @ExcelField(title = "复购次数", align = 2, sort = 50)
    private Long repeatCount;

    private Boolean isSeller;   // 是否为店主
    private Boolean isBuyer;    // 是否为买家

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    @ExcelField(title = "用户身份", align = 2, sort = 40)
    public String getBuyerRole() {
        return isSeller ? "店主" : (isBuyer ? "买家" : "未知");
    }

    public Long getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Long repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Boolean getSeller() {
        return isSeller;
    }

    public void setSeller(Boolean seller) {
        isSeller = seller;
    }

    public Boolean getBuyer() {
        return isBuyer;
    }

    public void setBuyer(Boolean buyer) {
        isBuyer = buyer;
    }
}