package com.vcat.common.service;

import java.util.List;

import com.vcat.common.persistence.CrudDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CrudService<T> {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected abstract CrudDao<T> getDao();

	/**
	 * 获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	public T get(String id) {
		return getDao().select(id);
	}

	/**
	 * 获取单条数据
	 * 
	 * @param entity
	 * @return
	 */
	public T get(T entity) {
		return getDao().get(entity);
	}

	/**
	 * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
	 * 
	 * @param entity
	 * @return
	 */
	public List<T> findList(T entity) {
		return getDao().findList(entity);
	}

	/**
	 * 查询所有数据列表
	 * 
	 * @param entity
	 * @return
	 */
	public List<T> findAllList(T entity) {
		return getDao().findAllList(entity);
	}

	/**
	 * 查询所有数据列表
	 * 
	 * @see public List<T> findAllList(T entity)
	 * @return
	 */
	@Deprecated
	public List<T> findAllList() {
		return getDao().findAllList();
	}

	/**
	 * 插入数据
	 * 
	 * @param entity
	 * @return
	 */
	public int insert(T record) {
		return getDao().insert(record);
	}

	/**
	 * 更新数据
	 * 
	 * @param entity
	 * @return
	 */
	public int update(T entity) {
		return getDao().update(entity);
	}

	/**
	 * 删除数据（一般为逻辑删除，更新del_flag字段为1）
	 * 
	 * @param id
	 * @see public int delete(T entity)
	 * @return
	 */
	@Deprecated
	public int delete(String id) {
		return getDao().delete(id);
	}

	/**
	 * 删除数据（一般为逻辑删除，更新del_flag字段为1）
	 * 
	 * @param entity
	 * @return
	 */
	public int delete(T entity) {
		return getDao().delete(entity);
	}
}
