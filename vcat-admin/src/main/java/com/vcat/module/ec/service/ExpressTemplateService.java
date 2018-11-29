package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ExpressTemplateDao;
import com.vcat.module.ec.entity.ExpressTemplate;
import com.vcat.module.ec.entity.FreightConfig;
import com.vcat.module.ec.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 物流公司Service
 */
@Service
@Transactional(readOnly = true)
public class ExpressTemplateService extends CrudService<ExpressTemplateDao, ExpressTemplate> {
    @Autowired
    private FreightConfigService freightConfigService;
    @Override
    @Transactional(readOnly = false)
    public void save(ExpressTemplate entity) {
        super.save(entity);
        freightConfigService.deleteBeforeSaveTemplate(entity.getId());
        List<FreightConfig> configList = entity.getFreightConfigList();
        for (int i = 0; i < configList.size(); i++) {
            FreightConfig freightConfig = configList.get(i);
            if(StringUtils.isEmpty(freightConfig.getName()) && null == freightConfig.getNationwideFlag()){
                continue;
            }
            freightConfig.setExpressTemplate(entity);
            freightConfigService.save(freightConfig);
        }
    }
    @Transactional(readOnly = false)
    public void setDefault(ExpressTemplate entity){
        dao.setDefault(entity.getId());
    }

    public ExpressTemplate getDefault(){
        return dao.getDefault();
    }

    public void deleteFromProduct(Product product) {
        dao.deleteFromProduct(product);
    }

    public void saveWithProduct(Product product) {
        dao.saveWithProduct(product);
    }
}
