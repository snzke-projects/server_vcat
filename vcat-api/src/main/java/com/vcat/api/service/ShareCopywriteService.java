package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ShareCopywriteDao;
import com.vcat.module.ec.entity.ShareCopywrite;
@Service
@Transactional(readOnly = true)
public class ShareCopywriteService extends CrudService<ShareCopywrite> {

	@Autowired
	private ShareCopywriteDao shareCopywriteDao;
	@Override
	protected CrudDao<ShareCopywrite> getDao() {
		return shareCopywriteDao;
	}

}
