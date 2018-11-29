package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.ShopInfoDto;
import com.vcat.module.ec.entity.ShopInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@MyBatisDao
public interface ShopInfoDao extends CrudDao<ShopInfo> {
    List<ShopInfo> getAllFarmInfoByShopId(@Param("shopId")String shopId);
    List<ShopInfoDto> getShopInfo(ShopInfoDto shopInfoDto);
    void addLeroyerInfo(ShopInfoDto shopInfoDto);
    void updateShopInfo(ShopInfoDto shopInfoDto);
}
