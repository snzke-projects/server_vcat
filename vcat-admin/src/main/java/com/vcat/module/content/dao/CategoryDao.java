package com.vcat.module.content.dao;

import java.util.List;
import java.util.Map;

import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.content.entity.Category;
import com.vcat.module.core.dao.TreeDao;

/**
 * 栏目DAO接口
 */
@MyBatisDao
public interface CategoryDao extends TreeDao<Category> {
	
	public List<Category> findModule(Category category);
	
	public List<Category> findByModule(String module);
	
	public List<Category> findByParentId(String parentId, String isMenu);

	public List<Category> findByParentIdAndSiteId(Category entity);
	
	public List<Map<String, Object>> findStats(String sql);
}
