package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ProductCategory;
import com.vcat.module.ec.entity.Spec;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface SpecDao extends CrudDao<Spec> {
    Spec getByName(Spec spec);

    /**
     * 根据商品删除规格属性
     * @param productId
     */
    void deleteByProductId(String productId);

    /**
     * 根据商品规格删除规格属性
     * @param productItemId
     */
    void deleteByProductItemId(String productItemId);

    /**
     * 添加规格属性
     * @param spec
     */
    void insertSpecValue(Spec spec);

    /**
     * 根据商品获取规格属性名称数组
     * @param id
     * @return
     */
    String[] getSpecNameArrayByProduct(String id);

    /**
     * 根据商品分类获取规格属性
     * @param category
     * @param editable 规格是否可编辑
     * @return
     */
    List<Spec> findListByCategory(@Param(value = "category")ProductCategory category,@Param(value = "editable")boolean editable);

    /**
     * 在原有规格属性中添加属性值
     * @param spec
     */
    void addSpecValue(Spec spec);
}