package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.GroupBuying;

@MyBatisDao
public interface GroupBuyingDao extends CrudDao<GroupBuying> {

    /**
     * 激活团购
     * @param groupBuying
     * @return
     */
    Integer activate(GroupBuying groupBuying);

    /**
     * 获取
     * @param groupBuying
     * @return
     */
    GroupBuying getAnother(GroupBuying groupBuying);
}
