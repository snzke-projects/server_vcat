package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.UpgradeConditionDao;
import com.vcat.module.ec.entity.UpgradeCondition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UpgradeConditionService extends CrudService<UpgradeConditionDao, UpgradeCondition> {
    @Override
    public UpgradeCondition get(UpgradeCondition entity) {
        UpgradeCondition upgradeCondition = super.get(entity);
        upgradeCondition.setProductList(dao.getProductByCondition(upgradeCondition.getId()));
        return upgradeCondition;
    }

    @Override
    public UpgradeCondition get(String id) {
        UpgradeCondition upgradeCondition = new UpgradeCondition();
        upgradeCondition.setId(id);
        return get(upgradeCondition);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(UpgradeCondition entity) {
        super.save(entity);
        String productIds = entity.getSqlMap().get("productIds");
        if (!entity.getIsNewRecord()) {
            dao.deleteProduct(entity.getId());
        }
        if(StringUtils.isNotBlank(productIds)){
            dao.insertProduct(productIds.split("\\|"), entity.getId());
        }
    }
}
