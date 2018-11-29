package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.UpgradeCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface UpgradeConditionDao extends CrudDao<UpgradeCondition> {
    /**
     * 根据升级规则ID获取商品集合
     * @param conditionId
     * @return
     */
    List<Product> getProductByCondition(String conditionId);

    /**
     * 删除规则关联的商品
     * @param id
     */
    void deleteProduct(String id);

    /**
     * 插入规则商品
     * @param productIdArray
     * @param id
     */
    void insertProduct(@Param(value = "productIdArray") String[] productIdArray, @Param(value = "id") String id);
}
