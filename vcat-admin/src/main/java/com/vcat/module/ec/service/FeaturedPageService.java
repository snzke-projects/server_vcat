package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.FeaturedPageDao;
import com.vcat.module.ec.entity.FeaturedPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 推荐页Service
 */
@Service
@Transactional(readOnly = true)
public class FeaturedPageService extends CrudService<FeaturedPageDao, FeaturedPage> {
}
