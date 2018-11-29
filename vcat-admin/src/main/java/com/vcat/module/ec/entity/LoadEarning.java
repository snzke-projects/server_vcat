package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.math.BigDecimal;

/**
 * 上架奖励
 */
public class LoadEarning extends DataEntity<LoadEarning> {
    private static final long serialVersionUID = 1L;
    private Product product;                            // 商品
    private BigDecimal fund = BigDecimal.ZERO;	        // 上架奖励金额
    private BigDecimal oldFund = BigDecimal.ZERO;	    // 修改前销售奖励金额
    private BigDecimal convertFund = BigDecimal.ZERO;   // 销售额度(达到此额度时才能获得上架奖励)
    private BigDecimal oldConvertFund = BigDecimal.ZERO;// 修改前销售额度
    private String isActivate = ACTIVATED;	            // 是否激活
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getOldFund() {
        return oldFund;
    }

    public void setOldFund(BigDecimal oldFund) {
        this.oldFund = oldFund;
    }

    public BigDecimal getFund() {
        return fund;
    }

    public void setFund(BigDecimal fund) {
        this.fund = fund;
    }

    public String getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(String isActivate) {
        this.isActivate = isActivate;
    }

    public BigDecimal getConvertFund() {
        return convertFund;
    }

    public void setConvertFund(BigDecimal convertFund) {
        this.convertFund = convertFund;
    }

    public BigDecimal getOldConvertFund() {
        return oldConvertFund;
    }

    public void setOldConvertFund(BigDecimal oldConvertFund) {
        this.oldConvertFund = oldConvertFund;
    }
}
