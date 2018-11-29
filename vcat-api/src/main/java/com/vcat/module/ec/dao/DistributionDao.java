package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Distribution;

import java.util.List;

@MyBatisDao
public interface DistributionDao extends CrudDao<Distribution> {
    List<Distribution> findListByOfficeId(String officeId);

    void deleteOfficeDistribution(String officeId);

    void insertOfficeDistribution(Distribution distribution);

    /**
     * 判断该机构是否包含供应商权限
     * @param officeId 机构ID
     * @return
     */
    Boolean hasDistributionRole(String officeId);
}
