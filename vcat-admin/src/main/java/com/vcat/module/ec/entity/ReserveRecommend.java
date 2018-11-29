package com.vcat.module.ec.entity;

/**
 * 限时预购
 */
public class ReserveRecommend extends RecommendEntity {
    private static final long serialVersionUID = 1L;
    public static final String RECOMMEND_RESERVE = "RESERVE";

    @Override
    public String getTypeCode() {
        return RECOMMEND_RESERVE;
    }
}
