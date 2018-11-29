package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.ShopBgImageDto;
import com.vcat.module.ec.entity.BgImage;
import com.vcat.module.ec.entity.ShopBgImage;

import java.util.List;

@MyBatisDao
public interface BgImageDao extends CrudDao<BgImage> {

    Integer deleteShopBG(BgImage entity);

    /**
     * 为只选择了一张待删除背景图的小店新增一张默认图片
     * @param id
     * @return
     */
    Integer insertOnlyOneImageShop(String id);

    void activate(BgImage entity);
}
