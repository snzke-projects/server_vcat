package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.SupplierDao;
import com.vcat.module.ec.entity.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class SupplierService extends CrudService<Supplier> {

	@Autowired
	private SupplierDao supplierDao;
	@Override
	protected CrudDao<Supplier> getDao() {
		return supplierDao;
	}
}
