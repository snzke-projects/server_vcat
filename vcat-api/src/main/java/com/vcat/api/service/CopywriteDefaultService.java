package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CopywriteDefaultDao;
import com.vcat.module.ec.entity.CopywriteDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Code.Ai on 16/4/11.
 * Description:
 */
@Service
@Transactional(readOnly = true)
public class CopywriteDefaultService extends CrudService<CopywriteDefault> {
    @Autowired
    private CopywriteDefaultDao copywriteDefaultDao;
    @Override
    protected CrudDao<CopywriteDefault> getDao() {
        return copywriteDefaultDao;
    }
}
