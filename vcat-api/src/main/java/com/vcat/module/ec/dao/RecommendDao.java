package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.RecommendEntity;

@MyBatisDao
public interface RecommendDao extends CrudDao<RecommendEntity> {
    void insertProductRecommend(RecommendEntity recommend);
    void deleteProductRecommend(String productId);
}
