package com.vcat.module.ec.entity;

/**
 * 限时打折
 */
public class SaleRecommend extends RecommendEntity {
    private static final long serialVersionUID = 1L;
    public static final String RECOMMEND_SALE = "SALE";

    @Override
    public String getTypeCode() {
        return RECOMMEND_SALE;
    }
}
