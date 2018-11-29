package com.vcat.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.BrandDao;
import com.vcat.module.ec.entity.Brand;
@Service
@Transactional(readOnly = true)
public class BrandService extends CrudService<Brand> {

	@Autowired
	private BrandDao brandDao;

	@Override
	protected CrudDao<Brand> getDao() {
		return brandDao;
	}
	public List<Map<String, Object>> getBrandList(String shopId,String guruId) {
		
		return brandDao.getBrandList(shopId,guruId);
	}
}
