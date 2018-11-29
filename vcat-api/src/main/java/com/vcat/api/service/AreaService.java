package com.vcat.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.core.dao.AreaDao;
import com.vcat.module.core.entity.Area;
import com.vcat.module.ec.dto.AreaDto;
@Service
@Transactional(readOnly = true)
public class AreaService extends CrudService<Area> {

	@Autowired
	private AreaDao areaDao;
	@Autowired
	private RedisService redisService;
	@Override
	protected CrudDao<Area> getDao() {
		return areaDao;
	}
	public List<AreaDto> getAreaList(String areaId){
		//从redis缓存读取数据
		List<AreaDto> areaList = redisService.getList(areaId+"_"+ApiConstants.AREA_CODE);
		//redis里面没有数据 从数据库读，并写入redis缓存
		if(areaList==null||areaList.isEmpty()){
			areaList = areaDao.getAreaList(areaId);
			redisService.setList(areaId+"_"+ApiConstants.AREA_CODE, areaList);
		}
		return areaList;
	}
}
