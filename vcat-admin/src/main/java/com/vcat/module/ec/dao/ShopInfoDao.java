package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ShopInfo;

@MyBatisDao
public interface ShopInfoDao extends CrudDao<ShopInfo> {
    ShopInfo getSame(ShopInfo shopInfo);
    String getSameFarmName(ShopInfo shopInfo);
    void subtractInventory(String id);
    void updateCompanyInventory(ShopInfo shopInfo);
    ShopInfo getShopInfoByShopId(String shopId);
}
