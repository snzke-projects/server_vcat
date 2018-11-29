package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ShopInfoDao;
import com.vcat.module.ec.entity.ShopInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ShopInfoService extends CrudService<ShopInfoDao, ShopInfo> {
    @Override
    @Transactional(readOnly = false)
    public void save(ShopInfo entity) {
        super.save(entity);
        dao.updateCompanyInventory(entity);
    }

    public ShopInfo getSame(ShopInfo shopInfo) {
        return dao.getSame(shopInfo);
    }

    public String getSameFarmName(ShopInfo shopInfo) {
        return dao.getSameFarmName(shopInfo);
    }

    public void subtractInventory(String id) {
        dao.subtractInventory(id);
    }

    public ShopInfo getShopInfoByShopId(String shopId) {
        return dao.getShopInfoByShopId(shopId);
    }
}
