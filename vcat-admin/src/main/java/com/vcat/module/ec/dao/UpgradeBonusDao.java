package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.UpgradeBonus;

@MyBatisDao
public interface UpgradeBonusDao extends CrudDao<UpgradeBonus> {
    /**
     * 发放伯乐奖励或者不发放
     * @param upgradeBonus
     */
    void issueBonus(UpgradeBonus upgradeBonus);
}
