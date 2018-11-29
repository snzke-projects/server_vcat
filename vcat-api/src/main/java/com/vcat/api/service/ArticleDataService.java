package com.vcat.api.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.content.dao.ArticleDataDao;
import com.vcat.module.content.entity.ArticleData;

@Service
@Transactional(readOnly = true)
public class ArticleDataService extends CrudService<ArticleData>{
	@Autowired
	private ArticleDataDao articleDataDao;
	@Override
	protected CrudDao<ArticleData> getDao() {
		return articleDataDao;
	}
	public Map<String, Object> getDetail(String articleId) {
		
		return articleDataDao.getDetail(articleId);
	}
}
