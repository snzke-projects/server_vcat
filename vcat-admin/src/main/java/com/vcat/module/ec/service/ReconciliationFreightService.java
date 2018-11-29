package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.service.ServiceException;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.ReconciliationFreightDao;
import com.vcat.module.ec.entity.ReconciliationFreight;
import com.vcat.module.ec.entity.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单记账Service
 */
@Service
@Transactional(readOnly = true)
public class ReconciliationFreightService extends CrudService<ReconciliationFreightDao, ReconciliationFreight> {
    /**
     * 根据订单记账
     * @param reconciliationFreight
     */
    @Transactional(readOnly = false)
    public void reconciliationFreightByOrder(ReconciliationFreight reconciliationFreight) {
        if(null == reconciliationFreight.getOrder() || StringUtils.isEmpty(reconciliationFreight.getOrder().getId())){
            throw new ServiceException("记账失败，缺少参数：订单号！");
        }
        dao.insertByOrder(reconciliationFreight);
    }

    /**
     * 批量记账订单
     * @param orderIdArray
     */
    @Transactional(readOnly = false)
    public int batchReconciliationFreight(String[] orderIdArray) {
        return dao.insertByOrderArray(orderIdArray, UserUtils.getUser());
    }

    /**
     * 根据查询条件记账订单
     * @param supplier
     */
    @Transactional(readOnly = false)
    public int reconciliationFreightByQueryConditions(Supplier supplier) {
        return dao.insertByQueryConditions(supplier);
    }

}
