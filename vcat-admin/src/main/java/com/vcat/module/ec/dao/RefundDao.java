package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Refund;
import com.vcat.module.ec.entity.RefundInterface;

import java.util.List;

@MyBatisDao
public interface RefundDao extends CrudDao<Refund> {
    /**
     * 审核退货
     * @param refund
     */
    int verifyReturn(Refund refund);

    /**
     * 确认退货
     * @param refund
     */
    int confirmReturn(Refund refund);

    /**
     * 审核退款
     * @param refund
     */
    int verify(Refund refund);

    /**
     * 确认退款完成
     * @param refund
     */
    int confirmRefundCompleted(Refund refund);

    /**
     * 更新不应该包含邮费但是已包含邮费的退款单
     * @param refundId
     * @return
     */
    Integer updateHasFreightPriceRefundAmount(String refundId);

    List<Refund> findHistory(Refund refund);

    /**
     * 取消历史失败接口数据有效性
     * @param refundId
     */
    void cancelHistoryFailInterfaceData(String refundId);

    /**
     * 插入支付宝退款接口数据
     * @param refundInterface
     */
    void insertInterfaceDataByAlipay(RefundInterface refundInterface);

    /**
     * 获取退款接口请求数据
     * @param refundId
     * @return
     */
    RefundInterface getInterfaceData(String refundId);

    /**
     * 根据批次号获取退款单信息
     * @param batchNo
     * @return
     */
    Refund getRefundByBatchNo(String batchNo);

    /**
     * 更新接口退款结果
     * @param refundInterface
     */
    void updateInterfaceResult(RefundInterface refundInterface);

    /**
     * 撤销退款单
     * @param refund
     */
    void revocation(Refund refund);
}
