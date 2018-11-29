package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.core.entity.User;
import com.vcat.module.ec.entity.ReconciliationDetail;
import com.vcat.module.ec.entity.Supplier;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface ReconciliationDetailDao extends CrudDao<ReconciliationDetail> {

    /**
     * 根据订单记录结算详情
     * @param reconciliationDetail
     */
    void insertByOrder(ReconciliationDetail reconciliationDetail);

    /**
     * 批量结算订单
     * @param orderId
     */
    void insertByOrderArray(@Param(value = "orderIdArray")String[] orderId, @Param(value = "currentUser")User user);

    /**
     * 根据订单项记录结算详情
     * @param reconciliationDetail
     */
    void insertByOrderItem(ReconciliationDetail reconciliationDetail);

    /**
     * 更新订单结算状态
     * @param orderIdArray
     */
    int updateReconciliationStatus(@Param(value = "orderIdArray")String... orderIdArray);

    /**
     * 根据查询条件批量结算订单
     * @param supplier
     * @return
     */
    void insertByQueryConditions(Supplier supplier);

    /**
     * 根据查询条件更新订单结算状态
     * @param supplier
     */
    int updateReconciliationStatusByQueryConditions(Supplier supplier);

    /**
     * 根据订单项ID自动结算
     * @param orderItemId
     */
    void insertByCheckOrder(String orderItemId);
}
