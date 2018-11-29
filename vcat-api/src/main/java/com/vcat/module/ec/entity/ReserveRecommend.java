package com.vcat.module.ec.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 限时预购
 */
public class ReserveRecommend extends RecommendEntity {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.JSON_STYLE);
    }

    private static final long serialVersionUID = 1L;
    public static final String RECOMMEND_RESERVE = "RESERVE";

    @Override
    public String getTypeCode() {
        return RECOMMEND_RESERVE;
    }
}
