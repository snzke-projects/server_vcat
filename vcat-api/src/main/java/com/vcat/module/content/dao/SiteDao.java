package com.vcat.module.content.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.content.entity.Site;

/**
 * 站点DAO接口
 */
@MyBatisDao
public interface SiteDao extends CrudDao<Site> {

}
