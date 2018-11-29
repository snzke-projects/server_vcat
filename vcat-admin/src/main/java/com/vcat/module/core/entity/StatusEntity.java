package com.vcat.module.core.entity;

/**
 * 状态实体
 */
public abstract class StatusEntity<T> extends DataEntity<T> {
	private static final long serialVersionUID = 8317635434740433562L;
	private String name;
	private String code;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public StatusEntity() {
		super();
	}
	public StatusEntity(String code) {
		super();
		this.code = code;
	}
	public StatusEntity(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}
	
}
