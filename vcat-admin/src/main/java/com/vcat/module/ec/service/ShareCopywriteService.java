package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ShareCopywriteDao;
import com.vcat.module.ec.entity.ShareCopywrite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 分享文案模板Service
 */
@Service
@Transactional(readOnly = true)
public class ShareCopywriteService extends CrudService<ShareCopywriteDao, ShareCopywrite> {
}
