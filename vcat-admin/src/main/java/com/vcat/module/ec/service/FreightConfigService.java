package com.vcat.module.ec.service;

import com.vcat.module.core.entity.Area;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.FreightConfigDao;
import com.vcat.module.ec.entity.FreightConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 运费模板配置Service
 */
@Service
@Transactional(readOnly = true)
public class FreightConfigService extends CrudService<FreightConfigDao, FreightConfig> {
    @Override
    @Transactional(readOnly = false)
    public void save(FreightConfig entity) {
        super.save(entity);
        List<Area> cityList = entity.getCityList();
        if(null != cityList && !cityList.isEmpty()){
            dao.insertCityList(entity.getId(),cityList);
        }
    }

    @Override
    public List<FreightConfig> findList(FreightConfig entity) {
        List<FreightConfig> list = super.findList(entity);
        if(null != list && !list.isEmpty()){
            for (int i = 0; i < list.size(); i++) {
                FreightConfig f = list.get(i);
                f.setCityList(dao.getCityList(f.getId()));
            }
        }
        return list;
    }

    /**
     * 每次保存邮费配置前先删除一次
     * @param expressTemplateId
     */
    public void deleteBeforeSaveTemplate(String expressTemplateId){
        dao.deleteCityList(expressTemplateId);
        dao.deleteConfigs(expressTemplateId);
    }
}
