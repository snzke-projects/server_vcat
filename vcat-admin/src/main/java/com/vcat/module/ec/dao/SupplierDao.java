package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.StatementOfAccount;
import com.vcat.module.ec.entity.StatementOfFinancial;
import com.vcat.module.ec.entity.Supplier;

import java.util.List;

/**
 * 商品DAO接口
 */
@MyBatisDao
public interface SupplierDao extends CrudDao<Supplier> {
    /**
     * 对账单清单
     * @param supplier
     * @return
     */
    List<StatementOfAccount> statementOfAccount(Supplier supplier);

    /**
     * 对账单统计
     * @param supplier
     * @return
     */
    List<StatementOfAccount> statementOfAccountTotal(Supplier supplier);

    /**
     * 对账单清单
     * @param supplier
     * @return
     */
    List<StatementOfFinancial> statementOfFinancial(Supplier supplier);

    /**
     * 对账单统计
     * @param supplier
     * @return
     */
    List<StatementOfFinancial> statementOfFinancialTotal(Supplier supplier);
}
