package com.vcat.module.ec.entity;

import com.vcat.common.utils.StatusColor;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

public class ProductSeller extends DataEntity<ProductSeller> {
	private static final long serialVersionUID = 1L;
    private String supplierId;  // 供应商ID
    private String brandId;     // 品牌ID
    private String sellerId;
    @ExcelField(title = "商品名称", align = 2, sort = 10)
    private String productName;
    @ExcelField(title = "用户名", align = 2, sort = 20)
    private String sellerName;
    @ExcelField(title = "手机号", align = 2, sort = 30)
    private String phone;
    @ExcelField(title = "销量", align = 2, sort = 60)
    private Long sales;

    private Boolean isProxy;   // 是否代理
    private Boolean isSale;    // 是否销售

    @ExcelField(title = "是否代理", align = 2, sort = 50)
    public String getIsProxy() {
        return isProxy ? "已代理" : "未代理";
    }
    @ExcelField(title = "是否销售", align = 2, sort = 50)
    public String getIsSale() {
        return isSale ? "已销售" : "未销售";
    }
    public String getIsProxyColor() {
        return isProxy ? StatusColor.GREEN : StatusColor.RED;
    }
    public String getIsSaleColor() {
        return isSale ? StatusColor.GREEN : StatusColor.RED;
    }

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

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getSales() {
        return sales;
    }

    public void setSales(Long sales) {
        this.sales = sales;
    }

    public Boolean getProxy() {
        return isProxy;
    }

    public void setProxy(Boolean proxy) {
        isProxy = proxy;
    }

    public Boolean getSale() {
        return isSale;
    }

    public void setSale(Boolean sale) {
        isSale = sale;
    }
}