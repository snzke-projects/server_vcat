package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.InvoiceDao;
import com.vcat.module.ec.entity.Invoice;
@Service
@Transactional(readOnly = true)
public class InvoiceService extends CrudService<Invoice> {

	@Autowired
	private InvoiceDao invoiceDao;
	@Override
	protected CrudDao<Invoice> getDao() {
		return invoiceDao;
	}

}
