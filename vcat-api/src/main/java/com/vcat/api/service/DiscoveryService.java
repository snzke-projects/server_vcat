package com.vcat.api.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.module.ec.dao.ActivityDao;
import com.vcat.module.ec.dao.ActivityOfflineDao;
import com.vcat.module.ec.dao.DiscoveryDao;
import com.vcat.module.ec.dao.ProductDao;
import com.vcat.module.ec.dto.CopywriteDto;
import com.vcat.module.ec.dto.OfflineActivityDto;
import com.vcat.module.ec.dto.ProductDto;
import com.vcat.module.ec.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiscoveryService {
	private static final String TYPE_NOVICE = "TYPE_NOVICE";						// 新手上路
	@Autowired
	private ArticleService articleService;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ActivityOfflineDao activityOfflineDao;
	@Autowired
	private CopywriteService copywriteService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private ActivityService activityService;
	public Map<String, Object> getNewest(){
		//V猫体验馆
		Map<String,Object> vActivity = activityService.findTopActivity();
		//V买卖(新手上路)
		Map<String, Object> novice = articleService.findTopArticle("TYPE_NOVICE", "ec_class_room_type");
		//v猫会活动
		Map<String, Object> activity = activityOfflineDao.findTopActivity();
		//v猫达人
		//Map<String, Object> guru = articleService.findTopArticle("TYPE_GURU_INTRO","ec_guru_intro_type");
		Map<String, Object> status = new HashMap<>();
		status.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		status.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		Map<String, Object> map = new HashMap<>();
		if(vActivity != null){
			map.put("vActivity",String.valueOf(vActivity.get("title")));
		}else{
			map.put("vActivity", "暂时没有V猫体验馆活动");
		}
		if(activity != null){
            map.put("activity", String.valueOf(activity.get("title")));
        }else {
            map.put("activity", "暂时没有V猫会活动");
        }
		if(novice != null){
            map.put("deal", String.valueOf(novice.get("title")));
        }else{
            map.put("deal","暂时没有V买卖活动");
        }
		status.put("data",map);
		return status;
	}

	public Map<String, Object> getHotZoneList(int pageNo, String shopId) {
	    int count = productDao.countHotZoneProductList();
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();
		List<ProductDto> list = productDao.getHotZoneProductList(page, shopId);

		// 根据productId获取文案列表,只取第一条数据
		Pager pager = new Pager();
		pager.setPageOffset(0);
		pager.setPageSize(1);
		// 爆品默认以最新素材排序
		Shop             shop = shopService.get(shopId);
		for(ProductDto productDto : list){
			List<CopywriteDto> copywriteList = copywriteService.getCopywrites(shopId,productDto.getId(),pager,ApiConstants.SORT_DISCOVERY);
			if(copywriteList != null && copywriteList.size() > 0){
				productDto.getCopywriteList().add(copywriteList.get(0));
			}
			if(shop.getAdvancedShop() == 1){
				productDto.setSaleEarningFund(productDto.getSaleEarningFund().add(productDto.getBonusEaringFund()));
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("page", page);
		map.put("list", list);
		return map;
	}
}
