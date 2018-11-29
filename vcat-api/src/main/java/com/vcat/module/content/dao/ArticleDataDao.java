package com.vcat.module.content.dao;

import java.util.Map;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.content.entity.ArticleData;

/**
 * 文章DAO接口
 */
@MyBatisDao
public interface ArticleDataDao extends CrudDao<ArticleData> {

	Map<String, Object> getDetail(String articleId);
	
}
