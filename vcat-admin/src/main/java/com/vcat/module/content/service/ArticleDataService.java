package com.vcat.module.content.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.module.content.dao.ArticleDataDao;
import com.vcat.module.content.entity.ArticleData;
import com.vcat.module.core.service.CrudService;

/**
 * 站点Service
 */
@Service
@Transactional(readOnly = false)
public class ArticleDataService extends
		CrudService<ArticleDataDao, ArticleData> {

}
