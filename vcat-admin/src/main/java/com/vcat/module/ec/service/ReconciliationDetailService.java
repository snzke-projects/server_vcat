package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.service.ServiceException;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.ReconciliationDetailDao;
import com.vcat.module.ec.entity.ReconciliationDetail;
import com.vcat.module.ec.entity.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 对账明细Service
 */
@Service
@Transactional(readOnly = true)
public class ReconciliationDetailService extends CrudService<ReconciliationDetailDao, ReconciliationDetail> {
    /**
     * 根据订单结算
     * @param reconciliationDetail
     */
    @Transactional(readOnly = false)
    public void reconciliationByOrder(ReconciliationDetail reconciliationDetail) {
        if(null == reconciliationDetail.getOrder() || StringUtils.isEmpty(reconciliationDetail.getOrder().getId())){
            throw new ServiceException("结算失败，缺少参数：订单号！");
        }
        dao.insertByOrder(reconciliationDetail);
        dao.updateReconciliationStatus(reconciliationDetail.getOrder().getId());
    }

    /**
     * 根据订单项单独结算
     * @param reconciliationDetail
     */
    @Transactional(readOnly = false)
    public void reconciliationByItem(ReconciliationDetail reconciliationDetail) {
        if(null == reconciliationDetail.getOrderItem() || StringUtils.isEmpty(reconciliationDetail.getOrderItem().getId())){
            throw new ServiceException("结算失败，缺少参数：订单项ID！");
        }
        dao.insertByOrderItem(reconciliationDetail);
        dao.updateReconciliationStatus(reconciliationDetail.getOrder().getId());
    }

    /**
     * 批量结算订单
     * @param orderIdArray
     */
    @Transactional(readOnly = false)
    public int batchReconciliationOrder(String[] orderIdArray) {
        dao.insertByOrderArray(orderIdArray, UserUtils.getUser());
        int i = dao.updateReconciliationStatus(orderIdArray);
        return i;
    }

    /**
     * 根据查询条件结算订单
     * @param supplier
     */
    @Transactional(readOnly = false)
    public int reconciliationByQueryConditions(Supplier supplier) {
        dao.insertByQueryConditions(supplier);
        int i = dao.updateReconciliationStatusByQueryConditions(supplier);
        return i;
    }

}
