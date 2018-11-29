package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.BgImageDao;
import com.vcat.module.ec.entity.BgImage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 背景图片Service
 */
@Service
@Transactional(readOnly = true)
public class BgImageService extends CrudService<BgImageDao, BgImage> {

    @Override
    @Transactional(readOnly = false)
    public void delete(BgImage entity) {
        doOnlyOneImageShop(entity);

        super.delete(entity);

        logger.info("删除背景图[" + entity.getId() + "]成功");
    }

    private void doOnlyOneImageShop(BgImage entity){
        Integer i = dao.insertOnlyOneImageShop(entity.getId());

        logger.info("为[" + i + "]个小店增加默认背景图");

        Integer j = dao.deleteShopBG(entity);

        logger.info("删除[" + j + "]个小店背景图[" + entity.getId() + "]");
    }

    @Transactional(readOnly = false)
    public void activate(BgImage entity){
        if (0 == entity.getIsActivate()){
            doOnlyOneImageShop(entity);
        }
        dao.activate(entity);
    }
}
