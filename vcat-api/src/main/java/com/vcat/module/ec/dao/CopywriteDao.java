package com.vcat.module.ec.dao;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.CopywriteDto;
import com.vcat.module.ec.entity.Copywrite;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Code.Ai on 16/4/11.
 * Description:
 */
@MyBatisDao
public interface CopywriteDao extends CrudDao<Copywrite> {
    // 获取素材列表
    List<CopywriteDto> getCopywrites(@Param("shopId")String shopId,
                                     @Param("productId")String productId,
                                     @Param("page") Pager page,
                                     @Param("sortType") String sortType);
    int countGetCopywriteList(String productId);
    // 获取店铺文案配图
    Map<String,String> getShopCopywriteImages(
            @Param("shopId")String shopId,
            @Param("copywriteId")String copywriteId);
}