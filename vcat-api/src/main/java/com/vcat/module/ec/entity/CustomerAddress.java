package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 客户角色
 */
public class CustomerAddress extends DataEntity<CustomerAddress> {
	private Customer customer;
	private Address address;
	private String addressName;
	private boolean isDefault;
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getAddressName() {
		return addressName;
	}
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	private static final long serialVersionUID = 1L;
}
