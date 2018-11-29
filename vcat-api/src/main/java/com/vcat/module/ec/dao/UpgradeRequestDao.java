package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.UpgradeRequest;
import com.vcat.module.ec.entity.UpgradeRequestLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * desc:
 */
@MyBatisDao
public interface UpgradeRequestDao extends CrudDao<UpgradeRequest> {
    /**
     * 获取审核记录
     */
    List<UpgradeRequestLog> findLog(@Param("shopId")String shopId);
}
