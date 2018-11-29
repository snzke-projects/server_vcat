package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.SuggestDao;
import com.vcat.module.ec.entity.Suggest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 活动Service
 */
@Service
@Transactional(readOnly = true)
public class SuggestService extends CrudService<SuggestDao, Suggest> {
    @Transactional(readOnly = false)
	public void process(Suggest suggest){
        suggest.preUpdate();
		dao.process(suggest);
	}
}
