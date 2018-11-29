package com.vcat.module.content.dao;

import java.util.List;
import java.util.Map;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.content.entity.Article;
import com.vcat.module.content.entity.Category;

/**
 * 文章DAO接口
 */
@MyBatisDao
public interface ArticleDao extends CrudDao<Article> {
	List<Article> findByIdIn(String[] ids);
	
	int updateHitsAddOne(String id);
	
	int updateExpiredWeight(Article article);
	
	List<Category> findStats(Category category);

	/**
	 * 获取V猫教室文章
	 * @param article
	 * @return
	 */
	List<Map<String,Object>> findClassRoomArticle(Article article);
	//增加点击量
	void addHits(String id);

	Map<String,Object> findTopArticle(Article article);
	
}
