package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

public class EcRole extends DataEntity<EcRole>{
	private static final long serialVersionUID = 1L;
	private String roleName;	// 角色名(卖家|买家)
	public String getRoleName() {
		return roleName;
	}
	public EcRole(){};
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public EcRole(int id,String roleName) {
		this.id = id+"";
		this.roleName = roleName;
	}
}
