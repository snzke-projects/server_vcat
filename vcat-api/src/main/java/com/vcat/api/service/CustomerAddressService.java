package com.vcat.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vcat.common.persistence.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CustomerAddressDao;
import com.vcat.module.ec.entity.Address;
import com.vcat.module.ec.entity.CustomerAddress;
@Service
@Transactional(readOnly = true)
public class CustomerAddressService extends CrudService<CustomerAddress> {

	@Autowired
	private CustomerAddressDao customerAddressDao;
	@Autowired 	
	private AddressService addressService;
	@Override
	protected CrudDao<CustomerAddress> getDao() {
		return customerAddressDao;
	}
	@Transactional(readOnly = false)
	public void cancalDefault(String buyerId) {
		customerAddressDao.cancalDefault(buyerId);
	}
	@Transactional(readOnly = false)
	public void updateAddress(CustomerAddress customerAddress) {
		customerAddressDao.updateDefault(customerAddress);
		addressService.update(customerAddress.getAddress());
	}
	@Transactional(readOnly = false)
	public void saveAddress(CustomerAddress customerAddress) {
		addressService.insert(customerAddress.getAddress());
		customerAddressDao.insert(customerAddress);
	}
	
	public Map<String,Object> countAddress(String buyerId) {
		
		return customerAddressDao.countAddress(buyerId);
	}
	//删除收货地址
	@Transactional(readOnly = false)
	public void deleteAddress(String buyerId, String addressId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("buyerId",buyerId );
		map.put("addressId",addressId );
		customerAddressDao.deleteBuyerId(map);
	}
	public List<Address> getAddressList(String buyerId,Pager page,String condition) {
		
		return customerAddressDao.getAddressList(buyerId,page,condition);
	}
	public Address getDefaultAddressList(String buyerId) {
	
		return customerAddressDao.getDefaultAddressList(buyerId);
	}
	public int getAddressCounts(String buyerId,String condition){
		return customerAddressDao.getAddressCounts(buyerId,condition);
	}
	public Address getChooseAddress(String buyerId, String addressId){
		return customerAddressDao.getChooseAddress(buyerId,addressId);
	}

}
