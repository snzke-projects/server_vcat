package com.vcat.api.service;

import java.util.*;

import com.vcat.common.constant.ApiConstants;
import com.vcat.config.CacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.HeadImageDao;
import com.vcat.module.ec.dao.ShopBgImageDao;
import com.vcat.module.ec.dto.ShopBgImageDto;
import com.vcat.module.ec.entity.BgImage;
import com.vcat.module.ec.entity.HeadImage;
import com.vcat.module.ec.entity.Shop;
import com.vcat.module.ec.entity.ShopBgImage;
@Service
@Transactional(readOnly = true)
public class ShopBgImageService extends CrudService<ShopBgImage> {

	@Autowired
	private ShopBgImageDao shopBgImageDao;
	@Autowired
	private HeadImageDao headImageDao;

	@Override
	protected CrudDao<ShopBgImage> getDao() {
		return shopBgImageDao;
	}

	//2016.2.25添加,重构获取背景图片代码
	public List<ShopBgImageDto> findBackgroundPicList(String shopId, String type){
        if(type.equals(ApiConstants.BG_TYPE_ALL))
            return shopBgImageDao.findAllBackgroundPicList(shopId);
        else {
            return shopBgImageDao.findBindBackgroundPicList(shopId);
        }
    }

	// 插入背景图片
	@Transactional(readOnly = false)
	public void insertList(String shopId, List<String> list) {
		//删除原来的关联图片关系
		shopBgImageDao.deleteList(shopId);
		Shop shop = new Shop();
		shop.setId(shopId);
		//去除list里面的重复id
		//Set<String> set = new HashSet<>(list);
		//for (String imageId : set) {
		//	ShopBgImage bgImage = new ShopBgImage();
		//	BgImage image = new BgImage();
		//	image.setId(imageId);
		//	bgImage.setShop(shop);
		//	bgImage.setBgImage(image);
		//	bgImage.preInsert();
		//	shopBgImageDao.insert(bgImage);
		//}
        ShopBgImage bgImage = new ShopBgImage();
        BgImage image = new BgImage();
        image.setId(list.get(0));
        bgImage.setShop(shop);
        bgImage.setBgImage(image);
        bgImage.preInsert();
        shopBgImageDao.insert(bgImage);

        ////1.得到具有多个背景图的店铺对象
        //List<String> alllist = shopBgImageDao.getList();
        ////根据 shop_id 删除记录
        //for(String ss : alllist){
        //    shopBgImageDao.deleteList(ss);
        //}
        ////再添加默认背景图
        //for(String ss : alllist){
        //    Shop shop = new Shop();
        //    shop.setId(ss);
        //    ShopBgImage bgImage = new ShopBgImage();
        //    BgImage image = new BgImage();
        //    image.setId("1c52c4fffe2b4e979e16ce289cd1743e");
        //    bgImage.setShop(shop);
        //    bgImage.setBgImage(image);
        //    bgImage.preInsert();
        //    shopBgImageDao.insert(bgImage);
        //}
	}

	/**
	 * 获取首页背景图集合
	 * @param type 
	 * @return
	 */
	@Cacheable(value = CacheConfig.GET_INDEX_IMAGE_LIST_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public List<Map<String,Object>> getIndexImageList(String type){
		HeadImage head = new HeadImage();
		head.setType(type);
		List<Map<String,Object>> list = headImageDao.getAppShowList(head);
		for (int i = 0;i<list.size();i++){
			Object res = list.get(i);
			if(res instanceof Map){
				Map<String,Object> row = (Map)res;
				row.put("url", QCloudUtils.createOriginalDownloadUrl(row.get("url") + ""));
				row.remove("isActivate");
				row.remove("delFlag");
				row.remove("displayOrder");
			}
		}

		return list;
	}
}
