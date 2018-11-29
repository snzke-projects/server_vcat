package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Image;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface ImageDao extends CrudDao<Image> {
    int deleteImage(@Param("idList")List<String> idList);
}

