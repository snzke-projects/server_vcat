package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.common.utils.IdGen;
import com.vcat.module.ec.dao.ShopInfoDao;
import com.vcat.module.ec.dto.ShopInfoDto;
import com.vcat.module.ec.entity.ShopInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
@Transactional(readOnly = true)
public class ShopInfoService extends CrudService<ShopInfo> {
    @Autowired
    private ShopInfoDao shopInfoDao;

    @Override
    protected CrudDao<ShopInfo> getDao() {
        return shopInfoDao;
    }


    public List<ShopInfo> getAllFarmInfoByShopId(String shopId){
        return shopInfoDao.getAllFarmInfoByShopId(shopId);
    }
    public List<ShopInfoDto> getShopInfo(ShopInfoDto shopInfoDto){
        return shopInfoDao.getShopInfo(shopInfoDto);
    }
    @Transactional(readOnly = false)
    public void addLeroyerInfo(String wechatName, String imageUrl, String realName, String farmName, String customerId, String productId){
        ShopInfoDto newShopInfo = new ShopInfoDto();
        newShopInfo.setShopId(customerId);
        newShopInfo.setProductId(productId);
        newShopInfo.setRealName(realName);
        newShopInfo.setFarmName(farmName);
        newShopInfo.setId(IdGen.uuid());
        //shopInfo.preInsert();
        newShopInfo.setIsDefault(0);
        // 默认未激活
        newShopInfo.setIsActivate(0);
        newShopInfo.setQRCode(imageUrl);
        newShopInfo.setWechatName(wechatName);
        shopInfoDao.addLeroyerInfo(newShopInfo);
    }
    @Transactional(readOnly = false)
    public void updateShopInfo(ShopInfoDto shopInfoDto){
        shopInfoDao.updateShopInfo(shopInfoDto);
    }
}
