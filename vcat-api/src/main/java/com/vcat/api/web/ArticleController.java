package com.vcat.api.web;

import java.util.HashMap;
import java.util.Map;

import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.module.core.entity.MsgEntity;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vcat.api.service.ArticleDataService;
import com.vcat.api.service.ArticleService;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;

/**
 * 消息接口
 * @author zfc
 *2015年5月30日 11:20:54
 */
@RestController
@ApiVersion({1,2})
public class ArticleController extends RestBaseController {
	@Autowired
	private ArticleService articleService;
	@Autowired
	private ArticleDataService articleDataService;

	/**
	 * 获取文章列表
	 * TYPE_NOVICE = "TYPE_NOVICE"							新手上路
	 * TYPE_MARKETING_STRATEGY = "TYPE_MARKETING_STRATEGY"	营销策略
	 * TYPE_POPULAR_STORIES = "TYPE_POPULAR_STORIES"		人气案例
	 * TYPE_NEW_TUTORIAL = "TYPE_NEW_TUTORIAL"				最新教程
	 * @param token
	 * @param type
	 * @param pageNo
	 * @return
	 */
	@ApiVersion(1)
	@RequiresRoles("seller")
	@RequestMapping("/api/getArticleList")
	public Object getArticleList(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "type", defaultValue = "") String type,
			@RequestParam(value = "pageNo", defaultValue = "1") String pageNo) {
		if (StringUtils.isEmpty(type)) {
			logger.debug("request params is illegal ");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,	ApiMsgConstants.FAILED_CODE);
		}

		Pager page = new Pager();
		page.setPageNo(Integer.parseInt(pageNo));
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);

		try {
			page = articleService.findPage(page, type);
		} catch (Exception e) {
			logger.error("getMessageList ERROR:"+e.getMessage(),e);
			return new MsgEntity("获取文章列表失败！", ApiMsgConstants.FAILED_CODE);
		}

		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("page", page);
		map.put("list", page.getList());
		page.setList(null);
		return map;
	}
	/**
	 * 获取文章详情,h5使用的数据
	 * @param token
	 * @param type
	 * @return
	 */
	@RequestMapping("/anon/getArticleDetail")
	public Object getArticleDetail(
			@RequestParam(value = "id", defaultValue = "") String id ){
		if (StringUtils.isEmpty(id)) {
			logger.debug("request params is illegal ");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		//增加点击量
		articleService.addHits(id);
		Map<String, Object> map = null;
		try {
			map = articleDataService.getDetail(id);
		} catch (Exception e) {
			logger.error("getArticleDetail ERROR:" + e.getMessage(), e);
			return new MsgEntity("获取文章详情失败！", ApiMsgConstants.FAILED_CODE);
		}
		if(map!=null&&map.size()!=0){
			map.put("imgUrl",QCloudUtils.createOriginalDownloadUrl((String)map.get("imgUrl")));
		}
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		returnMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		returnMap.put("article", map);
		return returnMap;
	}
}
