package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.CopywriteDao;
import com.vcat.module.ec.dao.CopywriteImageDao;
import com.vcat.module.ec.entity.Copywrite;
import com.vcat.module.ec.entity.CopywriteImage;
import com.vcat.module.ec.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 商品素材Service
 */
@Service
@Transactional(readOnly = true)
public class CopywriteService extends CrudService<CopywriteDao, Copywrite> {
    @Autowired
    private CopywriteImageDao imageDao;

    @Override
    public Copywrite get(Copywrite entity) {
        Copywrite copywrite = super.get(entity);
        CopywriteImage image = new CopywriteImage();
        image.setCopywrite(copywrite);
        copywrite.setImageList(imageDao.findList(image));
        copywrite.setShopImageList(imageDao.findShopList(image));
        return copywrite;
    }

    @Override
    public Copywrite get(String id) {
        return get(new Copywrite(id));
    }

    @Transactional(readOnly = false)
    public void save(Copywrite entity) {
        super.save(entity);

        imageDao.deleteByCopywrite(entity.getId());
        imageDao.deleteShopByCopywrite(entity.getId());

        if(StringUtils.isNotBlank(entity.getSqlMap().get("productImageUrls"))){
            String[] productImageUrlArray = entity.getSqlMap().get("productImageUrls").split("\\|");
            int listSize = productImageUrlArray.length;
            for (int i = 0; i < listSize; i++) {
                CopywriteImage image = new CopywriteImage();
                image.preInsert();
                image.setCopywrite(entity);
                image.setDisplayOrder((listSize - i) * 10);
                image.setImageUrl(productImageUrlArray[i]);
                imageDao.insert(image);
            }
        }
        if(StringUtils.isNotBlank(entity.getSqlMap().get("shopImageUrls"))){
            String[] shopImageUrlArray = entity.getSqlMap().get("shopImageUrls").split("\\|");
            String[] shopIdArray = entity.getSqlMap().get("shopIds").split("\\|");
            int listSize = shopImageUrlArray.length;
            for (int i = 0; i < listSize; i++) {
                CopywriteImage image = new CopywriteImage();
                Shop shop = new Shop();
                shop.setId(shopIdArray[i]);
                image.preInsert();
                image.setCopywrite(entity);
                image.setDisplayOrder(i * 10);
                image.setImageUrl(shopImageUrlArray[i]);
                image.setShop(shop);
                imageDao.insertShop(image);
            }
        }
    }

    /**
     * (取消)激活文案
     * @param entity
     * @return
     */
    @Transactional(readOnly = false)
    public int activate(Copywrite entity){
        if(!entity.getIsActivate()){
            Integer copywriteCount = dao.getCopywriteCount(entity.getId());
            Assert.isTrue(null != copywriteCount && copywriteCount > 1,"该商品必须包含一个激活文案！");
        }
        return dao.activate(entity);
    }
}
