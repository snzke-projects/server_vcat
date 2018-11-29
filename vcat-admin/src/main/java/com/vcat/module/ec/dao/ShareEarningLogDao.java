package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ShareEarning;
import com.vcat.module.ec.entity.ShareEarningLog;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface ShareEarningLogDao extends CrudDao<ShareEarningLog> {
    /**
     * 获取分享历史
     * @param shareEarning
     * @return
     */
    List<Map<String,Object>> findLog(ShareEarning shareEarning);
}
