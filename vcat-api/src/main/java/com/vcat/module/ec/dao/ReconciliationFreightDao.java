package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.core.entity.User;
import com.vcat.module.ec.entity.ReconciliationFreight;
import com.vcat.module.ec.entity.Supplier;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface ReconciliationFreightDao extends CrudDao<ReconciliationFreight> {

    /**
     * 根据订单记录邮费结算详情
     * @param reconciliationFreight
     */
    void insertByOrder(ReconciliationFreight reconciliationFreight);

    /**
     * 批量结算订单邮费
     * @param orderId
     */
    int insertByOrderArray(@Param(value = "orderIdArray") String[] orderId, @Param(value = "currentUser") User user);

    /**
     * 根据查询条件批量结算订单邮费
     * @param supplier
     * @return
     */
    int insertByQueryConditions(Supplier supplier);
}
