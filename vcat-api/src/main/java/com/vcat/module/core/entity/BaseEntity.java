package com.vcat.module.core.entity;

import javax.xml.bind.annotation.XmlTransient;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vcat.common.persistence.entity.Entity;
import com.vcat.common.supcan.annotation.treelist.SupTreeList;
import com.vcat.module.core.utils.UserUtils;

/**
 * Entity支持类
 */
@SupTreeList
public abstract class BaseEntity<T> extends Entity<T> {
	private static final long serialVersionUID = 349018139433732308L;

	public BaseEntity() {
		super();
	}

	public BaseEntity(String id) {
		super(id);
	}

	/**
	 * 当前用户
	 */
	protected User currentUser;

	@JsonIgnore
	@XmlTransient
	@JSONField(serialize=false)
	public User getCurrentUser() {
		if (currentUser == null) {
			currentUser = UserUtils.getUser();
		}
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

}
