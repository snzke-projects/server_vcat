package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Copywrite;

@MyBatisDao
public interface CopywriteDao extends CrudDao<Copywrite> {
    /**
     * 获取商品已激活文案的数量
     * @param id
     * @return
     */
    Integer getCopywriteCount(String id);

    /**
     * (取消)激活文案
     * @param copywrite
     * @return
     */
    int activate(Copywrite copywrite);
}
