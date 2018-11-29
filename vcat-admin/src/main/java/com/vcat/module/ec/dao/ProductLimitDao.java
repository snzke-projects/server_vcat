package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ProductLimit;

@MyBatisDao
public interface ProductLimitDao extends CrudDao<ProductLimit> {
    /**
     * 检查是否包含重复限购
     * @param entity
     * @return
     */
    boolean hasSameLimit(ProductLimit entity);

    /**
     * 删除团购活动限购商品规则
     * @param productItemId
     */
    void deleteByGroupBuying(String productItemId);
}

