package com.vcat.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ExpressDao;
import com.vcat.module.ec.entity.Express;
@Service
@Transactional(readOnly = true)
public class ExpressService extends CrudService<Express> {

	@Autowired
	private ExpressDao expressDao;
	@Autowired
	private RedisService redisService;
	@Override
	protected CrudDao<Express> getDao() {
		return expressDao;
	}
	public Express findByCode(String expressCode) {
		return expressDao.findByCode(expressCode);
	}
	
	public List<Express> getExpressList() {
		// 从redis缓存读取数据
		List<Express> expressList = redisService.getList("ExpressList");
		// redis里面没有数据 从数据库读，并写入redis缓存
		if (expressList == null || expressList.isEmpty()) {
			expressList = expressDao.getExpressList();
			redisService.setList("ExpressList",
					expressList);
		}
		return expressList;
	}
	public List<Express> findByName(String name){
		return expressDao.findByName(name);
	}
}
