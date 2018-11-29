package com.vcat.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.AddressDao;
import com.vcat.module.ec.entity.Address;

import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AddressService extends CrudService<Address> {

	@Autowired
	private AddressDao addressDao;
	@Override
	protected CrudDao<Address> getDao() {
		return addressDao;
	}
	@Transactional(readOnly = false)
	public void deleteById(String addressId) {
		addressDao.deleteById(addressId);
	}
	public Address getOrderAddress(String orderId,String buyerId){
		return addressDao.getOrderAddress(orderId,buyerId);
	}
}
