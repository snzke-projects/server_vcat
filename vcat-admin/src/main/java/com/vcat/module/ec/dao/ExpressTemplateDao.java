package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ExpressTemplate;
import com.vcat.module.ec.entity.Product;

@MyBatisDao
public interface ExpressTemplateDao extends CrudDao<ExpressTemplate> {
    void setDefault(String id);

    ExpressTemplate getDefault();

    /**
     * 删除商品邮费模板
     * @param product
     */
    void deleteFromProduct(Product product);

    /**
     * 添加商品邮费模板
     * @param product
     */
    void saveWithProduct(Product product);
}
