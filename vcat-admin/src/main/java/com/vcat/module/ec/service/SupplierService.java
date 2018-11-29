package com.vcat.module.ec.service;

import com.google.common.collect.Lists;
import com.vcat.common.persistence.Page;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.SupplierDao;
import com.vcat.module.ec.entity.StatementOfAccount;
import com.vcat.module.ec.entity.StatementOfFinancial;
import com.vcat.module.ec.entity.Supplier;
import org.apache.shiro.ShiroException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 品牌Service
 */
@Service
@Transactional(readOnly = true)
public class SupplierService extends CrudService<SupplierDao, Supplier> {
    @Override
    public List<Supplier> findList(Supplier entity) {
        return super.findList(entity);
    }

    @Transactional(readOnly = false)
	public void save(Supplier supplier) {
		// 将转义后的HTML字符回转
//		supplier.setDescription(StringEscapeUtils.unescapeHtml4(supplier.getDescription()));
        // 如果该供应商为新数据，且当前用户没有添加供应商权限，则抛出权限异常
        if(supplier.getIsNewRecord() && !UserUtils.hasPermission("ec:supplier:add")){
            throw new ShiroException("您没有添加供应商的权限！");
        }

		super.save(supplier);
	}

    public Page<StatementOfAccount> statementOfAccount(Page<StatementOfAccount> page,Supplier supplier){
        Page<Supplier> sPage = new Page<>(page.getPageNo(),page.getPageSize());// 根据页码和每页数据模拟分页对象
        supplier.setPage(sPage);    // 设置分页对象，便于拦截器能够正确拦截并执行count查询
        page.setList(dao.statementOfAccount(supplier));	// 查询数据
        page.setCount(supplier.getPage().getCount());	// 更新页面page中的总数
        page.setPageNo(supplier.getPage().getPageNo());	// 更新页面page的页码
        page.initialize();
        return page;
    }

    public List<StatementOfAccount> statementOfAccountTotal(Supplier supplier){
        List<StatementOfAccount> list = Lists.newArrayList();
        list.add(StatementOfAccount.getTitle());
        list.addAll(dao.statementOfAccountTotal(supplier));
        return list;
    }

    public Page<StatementOfFinancial> statementOfFinancial(Page<StatementOfFinancial> page, Supplier supplier) {
        Page<Supplier> sPage = new Page<>(page.getPageNo(),page.getPageSize());// 根据页码和每页数据模拟分页对象
        supplier.setPage(sPage);    // 设置分页对象，便于拦截器能够正确拦截并执行count查询
        page.setList(dao.statementOfFinancial(supplier));	// 查询数据
        page.setCount(supplier.getPage().getCount());	// 更新页面page中的总数
        page.setPageNo(supplier.getPage().getPageNo());	// 更新页面page的页码
        page.initialize();
        return page;
    }

    public List<StatementOfFinancial> statementOfFinancialTotal(Supplier supplier) {
        List<StatementOfFinancial> list = Lists.newArrayList();
        list.addAll(dao.statementOfFinancialTotal(supplier));
        return list;
    }
}
