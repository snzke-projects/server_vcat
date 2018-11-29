package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 客户角色
 */
public class CustomerRole extends DataEntity<CustomerRole> {
	private Customer customer;
	private EcRole role;
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public EcRole getRole() {
		return role;
	}
	public void setRole(EcRole role) {
		this.role = role;
	}
	private static final long serialVersionUID = 1L;
}
