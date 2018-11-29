package com.vcat.common.persistence;

import java.util.List;

/**
 * DAO支持类实现
 */
public interface CrudDao<T> extends BaseDao {

	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	T get(String id);
	
	/**
	 * 获取单条数据
	 * @param entity
	 * @return
	 */
	T get(T entity);
	
	/**
	 * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
	 * @param entity
	 * @return
	 */
	List<T> findList(T entity);
	
	/**
	 * 查询所有数据列表
	 * @param entity
	 * @return
	 */
	List<T> findAllList(T entity);
	
	/**
	 * 查询所有数据列表
	 * @see List<T> findAllList(T entity)
	 * @return
	 */
	@Deprecated
	List<T> findAllList();
	
	/**
	 * 插入数据
	 * @param entity
	 * @return
	 */
	int insert(T entity);
	
	/**
	 * 更新数据
	 * @param entity
	 * @return
	 */
	int update(T entity);
	
	/**
	 * 删除数据（一般为逻辑删除，更新del_flag字段为1）
	 * @param id
	 * @see int delete(T entity)
	 * @return
	 */
	int delete(String id);
	
	/**
	 * 删除数据（一般为逻辑删除，更新del_flag字段为1）
	 * @param entity
	 * @return
	 */
	int delete(T entity);
	
	T select(String id);
	
}