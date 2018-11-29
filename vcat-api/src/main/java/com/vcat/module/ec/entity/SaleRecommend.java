package com.vcat.module.ec.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 限时打折
 */
public class SaleRecommend extends RecommendEntity {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.JSON_STYLE);
    }

    private static final long serialVersionUID = 1L;
    public static final String RECOMMEND_SALE = "SALE";

    @Override
    public String getTypeCode() {
        return RECOMMEND_SALE;
    }
}
