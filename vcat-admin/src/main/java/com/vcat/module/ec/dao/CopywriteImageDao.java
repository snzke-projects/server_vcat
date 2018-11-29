package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.CopywriteImage;

import java.util.List;

@MyBatisDao
public interface CopywriteImageDao extends CrudDao<CopywriteImage> {
    /**
     * 获取店铺素材图片列表
     * @param copywriteImage
     * @return
     */
    List<CopywriteImage> findShopList(CopywriteImage copywriteImage);

    /**
     * 插入店铺素材图片
     * @param copywriteImage
     */
    void insertShop(CopywriteImage copywriteImage);

    /**
     * 商品商品素材图片
     * @param copywriteId
     */
    int deleteByCopywrite(String copywriteId);

    /**
     * 删除店铺素材图片
     * @param copywriteId
     */
    int deleteShopByCopywrite(String copywriteId);
}
