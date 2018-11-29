package com.vcat.module.ec.entity;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

import java.math.BigDecimal;

public class ProductStatistics extends DataEntity<ProductStatistics> {
	private static final long serialVersionUID = 1L;
    private String supplierId;  // 供应商ID
    private String brandId;     // 品牌ID
    @ExcelField(title = "供应商名称", align = 2, sort = 10)
    private String supplierName;
    @ExcelField(title = "品牌名称", align = 2, sort = 20)
    private String brandName;
    @ExcelField(title = "商品名称", align = 2, sort = 30)
    private String name;
    @ExcelField(title = "商品价格", align = 2, sort = 40)
    private String retailPrice;
    @ExcelField(title = "销售利润", align = 2, sort = 50)
    private String saleEarning;
    @ExcelField(title = "总销售额", align = 2, sort = 60)
    private BigDecimal totalReturns;
    @ExcelField(title = "复购次数", align = 2, sort = 70)
    private BigDecimal repeatCount;
    @ExcelField(title = "购买店主", align = 2, sort = 80)
    private BigDecimal buyerCount;
    @ExcelField(title = "销售店主", align = 2, sort = 90)
    private BigDecimal sellerCount;
    @ExcelField(title = "代理店主", align = 2, sort = 100)
    private BigDecimal proxyCount;
    @ExcelField(title = "总销量", align = 2, sort = 110)
    private Long totalSales;
    @ExcelField(title = "销量排行", align = 2, sort = 120)
    private Long salesRanking;
    @ExcelField(title = "普通订单", align = 2, sort = 130)
    private Long normalOrderCount;
    @ExcelField(title = "拿样订单", align = 2, sort = 140)
    private Long sampleOrderCount;

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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRetailPrice() {
        return StringUtils.merge(retailPrice);
    }

    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getSaleEarning() {
        return StringUtils.merge(saleEarning);
    }

    public void setSaleEarning(String saleEarning) {
        this.saleEarning = saleEarning;
    }

    public BigDecimal getTotalReturns() {
        return totalReturns;
    }

    public void setTotalReturns(BigDecimal totalReturns) {
        this.totalReturns = totalReturns;
    }

    public BigDecimal getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(BigDecimal repeatCount) {
        this.repeatCount = repeatCount;
    }

    public BigDecimal getBuyerCount() {
        return buyerCount;
    }

    public void setBuyerCount(BigDecimal buyerCount) {
        this.buyerCount = buyerCount;
    }

    public BigDecimal getSellerCount() {
        return sellerCount;
    }

    public void setSellerCount(BigDecimal sellerCount) {
        this.sellerCount = sellerCount;
    }

    public BigDecimal getProxyCount() {
        return proxyCount;
    }

    public void setProxyCount(BigDecimal proxyCount) {
        this.proxyCount = proxyCount;
    }

    public Long getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Long totalSales) {
        this.totalSales = totalSales;
    }

    public Long getSalesRanking() {
        return salesRanking;
    }

    public void setSalesRanking(Long salesRanking) {
        this.salesRanking = salesRanking;
    }

    public Long getNormalOrderCount() {
        return normalOrderCount;
    }

    public void setNormalOrderCount(Long normalOrderCount) {
        this.normalOrderCount = normalOrderCount;
    }

    public Long getSampleOrderCount() {
        return sampleOrderCount;
    }

    public void setSampleOrderCount(Long sampleOrderCount) {
        this.sampleOrderCount = sampleOrderCount;
    }
}