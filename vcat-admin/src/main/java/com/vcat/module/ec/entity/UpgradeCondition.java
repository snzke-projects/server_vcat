package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.common.utils.DateUtils;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.entity.User;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 升级规则
 */
public class UpgradeCondition extends DataEntity<UpgradeCondition> {
    private Integer period;         // 有效周期 单位：分钟
    private BigDecimal amount;      // 发放金额
    private User operBy;            // 执行人
    private Date operDate;          // 执行时间
    private String productShow;     // 商品名称显示

    private List<Product> productList;  // 商品集合

    public String getPeriodLabel(){
        int interval = getPeriod();
        return DateUtils.getIntervalLabel(interval) + "之内";
    }

    public String getProductIdArray(){
        if(null != productList && !productList.isEmpty()){
            StringBuffer idArray = new StringBuffer();
            productList.forEach((Product p) -> {
                idArray.append("|" + p.getId());
            });
            return idArray.toString();
        }
        return "";
    }

    public Integer getPeriod() {
        if(null == period || 0 == period){
            period = 1440;
        }
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public User getOperBy() {
        return operBy;
    }

    public void setOperBy(User operBy) {
        this.operBy = operBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    public String getProductShow() {
        return productShow;
    }

    public void setProductShow(String productShow) {
        this.productShow = productShow;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}
