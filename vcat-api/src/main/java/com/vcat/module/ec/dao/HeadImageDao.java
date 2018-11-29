package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.BgImage;
import com.vcat.module.ec.entity.HeadImage;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface HeadImageDao extends CrudDao<HeadImage> {
    List<Map<String,Object>> getAppShowList(HeadImage headImage);
    void activate(HeadImage headImage);
}
