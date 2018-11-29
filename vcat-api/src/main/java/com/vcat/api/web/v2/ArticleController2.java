package com.vcat.api.web.v2;
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
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;

/**
 * 文章接口 v2
 * @author cw
 *
 */
@RestController
public class ArticleController2 extends RestBaseController {
	@Autowired
	private ArticleService articleService;
	@Autowired
	private ArticleDataService articleDataService;

	/**
	 * 获取文章列表
	 * TYPE_NOVICE = "TYPE_NOVICE"							新手上路(V买卖)
	 * TYPE_MARKETING_STRATEGY = "TYPE_MARKETING_STRATEGY"	营销策略(V猫会)
	 * TYPE_POPULAR_STORIES = "TYPE_POPULAR_STORIES"		人气案例(寻宝)
	 * TYPE_NEW_TUTORIAL = "TYPE_NEW_TUTORIAL"				最新教程(涨知识)
	 * @param token
	 * @param type
	 * @param pageNo
	 * @return
	 */
	@ApiVersion(2)
	@RequiresRoles("seller")
	@RequestMapping("/api/getArticleList")
	public Object getMessageList(
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
			page = articleService.findPage2(page, type);
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
}