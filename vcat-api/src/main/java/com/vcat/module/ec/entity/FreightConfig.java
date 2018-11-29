package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.Area;
import com.vcat.module.core.entity.DataEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.List;

/**
 * 运费设置
 */
public class FreightConfig extends DataEntity<FreightConfig> {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.JSON_STYLE);
    }

    private ExpressTemplate expressTemplate;    // 邮费模板
    private String name;                        // 邮费设置名称
    private Boolean nationwideFlag;             // 全国通用标识
    private BigDecimal first;                   // 首件单位
    private BigDecimal firstPrice;              // 首件价格
    private BigDecimal increment;               // 续件单位
    private BigDecimal incrementPrice;          // 续件价格
    private List<Area> cityList;              // 使用该运费设置的城市

    public Object getFirstShow(){
        if(null != first && null != expressTemplate && expressTemplate.getValuationMethod() == 1){
            return first.intValue();
        }
        return first;
    }

    public Object getIncrementShow(){
        if(null != increment && null != expressTemplate && expressTemplate.getValuationMethod() == 1){
            return increment.intValue();
        }
        return increment;
    }

    public ExpressTemplate getExpressTemplate() {
        return expressTemplate;
    }

    public void setExpressTemplate(ExpressTemplate expressTemplate) {
        this.expressTemplate = expressTemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNationwideFlag() {
        return nationwideFlag;
    }

    public void setNationwideFlag(Boolean nationwideFlag) {
        this.nationwideFlag = nationwideFlag;
    }

    public BigDecimal getFirst() {
        return first;
    }

    public void setFirst(BigDecimal first) {
        this.first = first;
    }

    public BigDecimal getFirstPrice() {
        return firstPrice;
    }

    public void setFirstPrice(BigDecimal firstPrice) {
        this.firstPrice = firstPrice;
    }

    public BigDecimal getIncrement() {
        return increment;
    }

    public void setIncrement(BigDecimal increment) {
        this.increment = increment;
    }

    public BigDecimal getIncrementPrice() {
        return incrementPrice;
    }

    public void setIncrementPrice(BigDecimal incrementPrice) {
        this.incrementPrice = incrementPrice;
    }

    public List<Area> getCityList() {
        return cityList;
    }

    public void setCityList(List<Area> cityList) {
        this.cityList = cityList;
    }
}
