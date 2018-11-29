package com.vcat.api.service;

import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CopywriteDao;
import com.vcat.module.ec.dto.CopywriteDto;
import com.vcat.module.ec.entity.Copywrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Code.Ai on 16/4/11.
 * Description:
 */
@Service
@Transactional(readOnly = true)
public class CopywriteService extends CrudService<Copywrite> {
    @Autowired
    private CopywriteDao copywriteDao;
    @Override
    protected CrudDao<Copywrite> getDao() {
        return copywriteDao;
    }
    /**
     * 获取素材列表
     * @param productId
     * @return
     */
    @Transactional(readOnly = false)
    public List<CopywriteDto> getCopywrites(String shopId, String productId, Pager page, String sortType){
        List<CopywriteDto> list = copywriteDao.getCopywrites(shopId,productId,page,sortType);
         //将店铺特定图片添加到默认素材图片中
        if(list != null && list.size() > 0){
            for(CopywriteDto copywriteDto : list){
                String copywriteId = copywriteDto.getId();
                // 获取店铺特定图片
                Map<String,String> shopImages = copywriteDao.getShopCopywriteImages(shopId,copywriteId);
                if(shopImages != null && shopImages.size() > 0 && shopImages.get("imageList") != null){
                    // 将店铺图片添加到公共图片中
                    String images = joinImages(copywriteDto.getImageListOfString(),shopImages.get("imageList"));
                    copywriteDto.setImageList(images);
                }
            }
        }
        return list;
    }

    // 获取团购商品素材
    @Transactional(readOnly = false)
    public List<CopywriteDto> getCopywrites(String productId, Pager page) {
        return copywriteDao.getCopywrites("",productId,page,"");
    }

    public int countGetCopywriteList(String productId){
        return copywriteDao.countGetCopywriteList(productId);
    }

    // 合并店主特有素材和默认素材
    private  String joinImages(String defaultImages , String shopImageList){
        String images = "";
        String[] default_images = defaultImages.split(",");
        String[] shop_images = shopImageList.split(",");
        String[] allImages = null;
        int len = default_images.length + shop_images.length;
        if(len > ApiConstants.DEFAULT_LEN){
            allImages = new String[ApiConstants.DEFAULT_LEN];
            System.arraycopy(default_images, 0, allImages, 0, ApiConstants.DEFAULT_LEN - (len - ApiConstants.DEFAULT_LEN));
            for(int i = ApiConstants.DEFAULT_LEN - shop_images.length , j = 0; i < ApiConstants.DEFAULT_LEN && j < shop_images.length  ;i++,j++){
                allImages[i] = shop_images[j];
            }
        }else {
            allImages = new String[len];
            System.arraycopy(default_images, 0, allImages, 0, default_images.length);
            System.arraycopy(shop_images, 0, allImages, default_images.length, len -
                    default_images.length);
            //for(int i = len - 1 ; i >= default_images.length ; i--){
            //    allImages[i] = shop_images[i - default_images.length];
            //}
            //System.arraycopy(shop_images, default_images.length - default_images.length, allImages, default_images.length, len -
            //        default_images.length);
        }
        for(int i = 0 ; i < allImages.length ; i++){
            images += allImages[i];
            if(i + 1 != allImages.length){
                images += ",";
            }
        }
        return images;
    }
}
