package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.HeadImage;

@MyBatisDao
public interface HeadImageDao extends CrudDao<HeadImage> {
    void activate(HeadImage headImage);
}
