package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

import java.math.BigDecimal;

public class ShopStatistics extends DataEntity<ShopStatistics> {
	private static final long serialVersionUID = 1L;
    @ExcelField(title = "店铺名称", align = 2, sort = 10)
    private String name;
    @ExcelField(title = "联系方式", align = 2, sort = 20)
    private String phone;
    @ExcelField(title = "总销售额", align = 2, sort = 30)
    private BigDecimal totalReturns;
    @ExcelField(title = "总销量", align = 2, sort = 40)
    private Long totalSales;
    @ExcelField(title = "销售利润", align = 2, sort = 50)
    private BigDecimal saleEarning;
    @ExcelField(title = "分红", align = 2, sort = 60)
    private BigDecimal bonusEarning;
    @ExcelField(title = "二级分红", align = 2, sort = 70)
    private BigDecimal secondBonusEarning;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getTotalReturns() {
        return totalReturns;
    }

    public void setTotalReturns(BigDecimal totalReturns) {
        this.totalReturns = totalReturns;
    }

    public Long getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Long totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getSaleEarning() {
        return saleEarning;
    }

    public void setSaleEarning(BigDecimal saleEarning) {
        this.saleEarning = saleEarning;
    }

    public BigDecimal getBonusEarning() {
        return bonusEarning;
    }

    public void setBonusEarning(BigDecimal bonusEarning) {
        this.bonusEarning = bonusEarning;
    }

    public BigDecimal getSecondBonusEarning() {
        return secondBonusEarning;
    }

    public void setSecondBonusEarning(BigDecimal secondBonusEarning) {
        this.secondBonusEarning = secondBonusEarning;
    }
}