package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.LoadEarningDao;
import com.vcat.module.ec.entity.LoadEarning;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 上架奖励Service
 */
@Service
@Transactional(readOnly = true)
public class LoadEarningService extends CrudService<LoadEarningDao, LoadEarning> {
	@Transactional(readOnly = false)
	public void save(LoadEarning loadEarning) {
		if(loadEarning.getFund().compareTo(loadEarning.getOldFund()) == 0
                && loadEarning.getConvertFund().compareTo(loadEarning.getOldConvertFund()) == 0){
			return;
		}
		dao.deleteLastEarning(loadEarning);
		loadEarning.preInsert();
		dao.insert(loadEarning);
	}
}
