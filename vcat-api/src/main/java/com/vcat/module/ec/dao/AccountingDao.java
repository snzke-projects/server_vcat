package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.core.entity.User;
import com.vcat.module.ec.entity.Accounting;
import com.vcat.module.ec.entity.Supplier;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface AccountingDao extends CrudDao<Accounting> {

    /**
     * 根据订单记录结算详情
     * @param accounting
     */
    void insertByOrder(Accounting accounting);

    /**
     * 批量结算订单
     * @param orderId
     */
    int insertByOrderArray(@Param(value = "orderIdArray") String[] orderId, @Param(value = "currentUser") User user);

    /**
     * 根据查询条件批量结算订单
     * @param supplier
     * @return
     */
    int insertByQueryConditions(Supplier supplier);
}
