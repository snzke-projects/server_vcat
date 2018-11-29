package com.vcat.module.ec.service;

import com.alibaba.fastjson.JSON;
import com.vcat.kuaidi100.pojo.NoticeRequest;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.ExpressApiDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ExpressDao;
import com.vcat.module.ec.entity.Express;

/**
 * 物流公司Service
 */
@Service
@Transactional(readOnly = true)
public class ExpressService extends CrudService<ExpressDao, Express> {
	@Autowired
	private ExpressApiDao expressApiDao;
	@Transactional(readOnly = false)
	public void save(Express area) {
        UserUtils.removeCache(UserUtils.CACHE_EXPRESS_LIST);
		super.save(area);
	}

	public NoticeRequest queryExpress(String com,String number){
		return JSON.parseObject(expressApiDao.query(com, number), NoticeRequest.class);
	}
}
