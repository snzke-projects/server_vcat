package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by: dong4j.
 * Date: 2016-11-26.
 * Time: 13:18.
 * Description: 封装销售分红,一级团队分红,二级团队分红
 */
public class ShopFundExpandDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal bonusTotalFund = new BigDecimal(0); // 累计销售分红总收入
    private BigDecimal monthBonusTotalFund = new BigDecimal(0); // 本月销售分红总收入
    private BigDecimal bonusHoldFund = new BigDecimal(0); // 销售分红待确认收入

    private BigDecimal firstBonusTotalFund = new BigDecimal(0); // 累计一级团队分红总收入
    private BigDecimal monthFirstBonusTotalFund = new BigDecimal(0); // 本月一级团队分红总收入
    private BigDecimal firstBonusHoldFund = new BigDecimal(0); // 一级团队分红待确认收入

    private BigDecimal secondBonusTotalFund = new BigDecimal(0); // 累计二级团队分红总收入
    private BigDecimal monthSecondBonusTotalFund = new BigDecimal(0); // 本月二级团队分红总收入
    private BigDecimal secondBonusHoldFund = new BigDecimal(0);//二级团队分红待确认资金

    public ShopFundExpandDto() {

    }
    public ShopFundExpandDto(BigDecimal defaltVale) {
        this.bonusTotalFund = defaltVale;
        this.monthBonusTotalFund = defaltVale;
        this.bonusHoldFund = defaltVale;
        this.firstBonusTotalFund = defaltVale;
        this.monthFirstBonusTotalFund = defaltVale;
        this.firstBonusHoldFund = defaltVale;
        this.secondBonusTotalFund = defaltVale;
        this.monthSecondBonusTotalFund = defaltVale;
        this.secondBonusHoldFund = defaltVale;
    }

    public BigDecimal getBonusTotalFund() {
        return bonusTotalFund;
    }

    public void setBonusTotalFund(BigDecimal bonusTotalFund) {
        this.bonusTotalFund = bonusTotalFund;
    }

    public BigDecimal getMonthBonusTotalFund() {
        return monthBonusTotalFund;
    }

    public void setMonthBonusTotalFund(BigDecimal monthBonusTotalFund) {
        this.monthBonusTotalFund = monthBonusTotalFund;
    }

    public BigDecimal getBonusHoldFund() {
        return bonusHoldFund;
    }

    public void setBonusHoldFund(BigDecimal bonusHoldFund) {
        this.bonusHoldFund = bonusHoldFund;
    }

    public BigDecimal getFirstBonusTotalFund() {
        return firstBonusTotalFund;
    }

    public void setFirstBonusTotalFund(BigDecimal firstBonusTotalFund) {
        this.firstBonusTotalFund = firstBonusTotalFund;
    }

    public BigDecimal getMonthFirstBonusTotalFund() {
        return monthFirstBonusTotalFund;
    }

    public void setMonthFirstBonusTotalFund(BigDecimal monthFirstBonusTotalFund) {
        this.monthFirstBonusTotalFund = monthFirstBonusTotalFund;
    }

    public BigDecimal getFirstBonusHoldFund() {
        return firstBonusHoldFund;
    }

    public void setFirstBonusHoldFund(BigDecimal firstBonusHoldFund) {
        this.firstBonusHoldFund = firstBonusHoldFund;
    }

    public BigDecimal getSecondBonusTotalFund() {
        return secondBonusTotalFund;
    }

    public void setSecondBonusTotalFund(BigDecimal secondBonusTotalFund) {
        this.secondBonusTotalFund = secondBonusTotalFund;
    }

    public BigDecimal getMonthSecondBonusTotalFund() {
        return monthSecondBonusTotalFund;
    }

    public void setMonthSecondBonusTotalFund(BigDecimal monthSecondBonusTotalFund) {
        this.monthSecondBonusTotalFund = monthSecondBonusTotalFund;
    }

    public BigDecimal getSecondBonusHoldFund() {
        return secondBonusHoldFund;
    }

    public void setSecondBonusHoldFund(BigDecimal secondBonusHoldFund) {
        this.secondBonusHoldFund = secondBonusHoldFund;
    }

    @Override
    public String toString() {
        return "ShopFundExpandDto{" +
                "bonusTotalFund=" + bonusTotalFund +
                ", monthBonusTotalFund=" + monthBonusTotalFund +
                ", bonusHoldFund=" + bonusHoldFund +
                ", firstBonusTotalFund=" + firstBonusTotalFund +
                ", monthFirstBonusTotalFund=" + monthFirstBonusTotalFund +
                ", firstBonusHoldFund=" + firstBonusHoldFund +
                ", secondBonusTotalFund=" + secondBonusTotalFund +
                ", monthSecondBonusTotalFund=" + monthSecondBonusTotalFund +
                ", secondBonusHoldFund=" + secondBonusHoldFund +
                '}';
    }
}
