package com.vcat.module.ec.dto;

import java.io.Serializable;

public class AreaDto implements Serializable{


	private static final long serialVersionUID = 4448482779663003464L;
	private String id;
	private String name;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
