package com.vcat.api.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.vcat.api.service.ProductTopicService;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.ProductDto;
import com.vcat.module.ec.entity.ProductTopic;

@RestController
@ApiVersion({ 1, 2 })
public class TopicController extends RestBaseController {
	@Autowired
	private ProductTopicService productTopicService;

	/**
	 * 得到专题树
	 * 
	 * @param token
	 * @param param
	 * @return
	 */
	@RequestMapping("/anon/topic/getTopicTree")
	public Object getTopicTree(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错" + e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null || params.get("topicId") == null
				|| params.get("size") == null || params.get("depth") == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String topicId = params.getString("topicId");
		int size = params.getInteger("size");
		int depth = params.getInteger("depth");
		Map<String, Object> map = new HashMap<>();
		ProductTopic productTopic = new ProductTopic();
		productTopic.setId(topicId);
		productTopicService.getTopicByPid(productTopic, size, depth);
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("topic", productTopic);
		return map;
	}

	/**
	 * 得到产品的list
	 * 
	 * @param token
	 * @param param
	 * @return
	 */
	@RequestMapping("/anon/topic/getProductList")
	public Object getProductList(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错" + e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null || params.get("topicId") == null
				|| params.get("pageNo") == null || params.get("flat") == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String topicId = params.getString("topicId");
		int pageNo = params.getInteger("pageNo");
		boolean flat = params.getBoolean("flat");
		List<String> ids;
		if(flat){
			ids = productTopicService.getChildren(topicId);
		} else {
			ids = new ArrayList<String>();
		}
		ids.add(topicId);
		int count = productTopicService.countProductByTopic(ids);
		// 组装分页信息
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();
		List<ProductDto> list = productTopicService.getProductList(page,
				ids);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("topic", productTopicService.findTopicById(topicId));
		map.put("page", page);
		map.put("list", list);
		return map;
	}
}
