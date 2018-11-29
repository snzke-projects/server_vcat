package com.vcat.common.persistence.entity;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.vcat.common.config.Global;
import com.vcat.common.persistence.Page;
import com.vcat.common.supcan.annotation.treelist.SupTreeList;
import com.vcat.common.supcan.annotation.treelist.cols.SupCol;
import com.vcat.common.utils.StringUtils;

/**
 * Entity支持类
 */
@SupTreeList
public abstract class Entity<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 实体编号（唯一标识）
	 */
	protected String id;

	/**
	 * 当前实体分页对象
	 */
	protected Page<T> page;

	/**
	 * 自定义SQL（SQL标识，SQL内容）
	 */
	protected Map<String, String> sqlMap;

	/**
	 * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
	 * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
	 */
	protected boolean isNewRecord = false;

	public Entity() {

	}

	public Entity(String id) {
		this();
		this.id = id;
	}

	@SupCol(isUnique = "true", isHide = "true")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JSONField(serialize=false)
	@XmlTransient
	@JsonIgnore
	public Page<T> getPage() {
		if (page == null) {
			page = new Page<T>();
		}
		return page;
	}

	public Page<T> setPage(Page<T> page) {
		this.page = page;
		return page;
	}

	@JsonIgnore
	@XmlTransient
	@JSONField(serialize=false)
	public Map<String, String> getSqlMap() {
		if (sqlMap == null) {
			sqlMap = Maps.newHashMap();
		}
		return sqlMap;
	}

	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}

	/**
	 * 插入之前执行方法，子类实现
	 */
	public abstract void preInsert();

	/**
	 * 更新之前执行方法，子类实现
	 */
	public abstract void preUpdate();

	/**
	 * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
	 * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
	 * 
	 * @return
	 */
	@JSONField(serialize=false)
	@XmlTransient
	@JsonIgnore
	public boolean getIsNewRecord() {
		return isNewRecord || StringUtils.isBlank(getId());
	}

	/**
	 * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
	 * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
	 */
	public void setIsNewRecord(boolean isNewRecord) {
		this.isNewRecord = isNewRecord;
	}

	/**
	 * 全局变量对象
	 */
	@JsonIgnore
	@JSONField(serialize=false)
	public Global getGlobal() {
		return Global.getInstance();
	}

	/**
	 * 获取数据库名称
	 */
	@JsonIgnore
	@JSONField(serialize=false)
	public String getDbName() {
		return Global.getConfig("jdbc.type");
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		Entity<?> that = (Entity<?>) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	/**
	 * 删除标记（0：正常；1：删除；2：审核；）
	 */
	public static final String DEL_FLAG_NORMAL = "0";
	public static final String DEL_FLAG_DELETE = "1";
	public static final String DEL_FLAG_AUDIT = "2";

	public static final String ACTIVATED = "1";
	public static final String NOT_ACTIVATED = "0";
}
