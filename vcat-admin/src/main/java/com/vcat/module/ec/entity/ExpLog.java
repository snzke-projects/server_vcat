package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 店铺登记操作日志
 */
public class ExpLog extends DataEntity<ExpLog> {
	private static final long serialVersionUID = 1L;
	private Integer value;	// 操作数值(增加为正，减少为负)
	private String note;	// 备注
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}
