package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.service.ServiceException;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.AccountingDao;
import com.vcat.module.ec.entity.Accounting;
import com.vcat.module.ec.entity.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单记账Service
 */
@Service
@Transactional(readOnly = true)
public class AccountingService extends CrudService<AccountingDao, Accounting> {
    /**
     * 根据订单记账
     * @param accounting
     */
    @Transactional(readOnly = false)
    public void accountingByOrder(Accounting accounting) {
        if(null == accounting.getOrder() || StringUtils.isEmpty(accounting.getOrder().getId())){
            throw new ServiceException("记账失败，缺少参数：订单号！");
        }
        dao.insertByOrder(accounting);
    }

    /**
     * 批量记账订单
     * @param orderIdArray
     */
    @Transactional(readOnly = false)
    public int batchAccountingOrder(String[] orderIdArray) {
        return dao.insertByOrderArray(orderIdArray, UserUtils.getUser());
    }

    /**
     * 根据查询条件记账订单
     * @param supplier
     */
    @Transactional(readOnly = false)
    public int accountingByQueryConditions(Supplier supplier) {
        return dao.insertByQueryConditions(supplier);
    }

}
