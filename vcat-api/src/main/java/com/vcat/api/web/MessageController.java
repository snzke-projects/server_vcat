package com.vcat.api.web;

import java.util.HashMap;
import java.util.Map;

import com.vcat.api.exception.ApiException;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.module.core.entity.MsgEntity;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vcat.api.service.MessageService;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;

/**
 * 消息接口
 * @author zfc
 *2015年5月28日 11:20:54
 */
@RestController
@ApiVersion({1,2})
public class MessageController extends RestBaseController {
	@Autowired
	private MessageService messageService;

	/**
	 * 获取消息列表
	 * @param token
	 * @param messageType
	 * @param pageNo
	 * @return
	 */
    @ApiVersion(1)
	@RequiresRoles("seller")
	@RequestMapping("/api/getMessageList")
	public Object getMessageList(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "messageType", defaultValue = "") String messageType,
			@RequestParam(value = "pageNo", defaultValue = "1") String pageNo) {
		if (StringUtils.isEmpty(messageType)) {
			logger.debug("request params is illegal ");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,	ApiMsgConstants.FAILED_CODE);
		}
		String shopId = StringUtils.getCustomerIdByToken(token);

		Pager page = new Pager();
		page.setPageNo(Integer.parseInt(pageNo));
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);

		try {
			page = messageService.findPage(page, shopId, messageType, 1);
		} catch (Exception e) {
			logger.error("getMessageList ERROR:"+e.getMessage(),e);
			return new MsgEntity("获取消息列表失败！", ApiMsgConstants.FAILED_CODE);
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
	 * 订阅分享活动
	 * @param token
	 * @param shareId
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/remindShare")
	public Object remindShare(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "shareId", defaultValue = "") String shareId) {
		if (StringUtils.isEmpty(shareId)) {
			logger.debug("request params is illegal ");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,	ApiMsgConstants.FAILED_CODE);
		}
		String shopId = StringUtils.getCustomerIdByToken(token);

		try {
			messageService.remindShare(shopId, shareId);
		} catch (ApiException e) {
            logger.error("remindShare ERROR:"+e.getMessage(),e);
            return new MsgEntity(e.getMessage(), ApiMsgConstants.FAILED_CODE);
        } catch (Exception e) {
			logger.error("remindShare ERROR:"+e.getMessage(),e);
			return new MsgEntity("订阅分享活动失败！", ApiMsgConstants.FAILED_CODE);
		}

		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		return map;
	}
	/**
	 * 获取店铺最新消息
	 * @param token
	 * @return
	 */
	@ApiVersion(1)
	@RequiresRoles("seller")
	@RequestMapping("/api/getNewestMessage")
	public Object getNewestMessage(
			@RequestHeader(value = "token", defaultValue = "") String token) {
		String shopId = StringUtils.getCustomerIdByToken(token);

		try {
			Map<String, Object> map = new HashMap<>();
			map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
			map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);

			Map<String, Object> result = messageService.getNewestMessage(shopId);
			if(null != result && !result.isEmpty()){
				map.putAll(result);
			}
			return map;
		} catch (Exception e) {
			logger.error("getNewestMessage ERROR:"+e.getMessage(),e);
			return new MsgEntity("获取店铺最新消息失败！", ApiMsgConstants.FAILED_CODE);
		}
	}
}
