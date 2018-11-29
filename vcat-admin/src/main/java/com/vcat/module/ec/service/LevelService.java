package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.LevelDao;
import com.vcat.module.ec.entity.Level;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 买家等级Service
 */
@Service
@Transactional(readOnly = true)
public class LevelService extends CrudService<LevelDao, Level> {
}
