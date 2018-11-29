package com.vcat.api.web.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcat.api.service.ActivityOfflineService;
import com.vcat.api.service.CustomerOfflineActivityService;
import com.vcat.api.service.CustomerService;
import com.vcat.api.service.DiscoveryService;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.config.CacheConfig;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.entity.*;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 移动客户端 - 发现
 */
@RestController
@ApiVersion(2)
public class DiscoveryController2 extends RestBaseController {
	private static Logger logger = Logger.getLogger(DiscoveryController2.class);
	@Autowired
	private DiscoveryService               discoveryService;
	@Autowired
	private CustomerOfflineActivityService customerOfflineActivityService;
	@Autowired
	private ActivityOfflineService         activityOfflineService;
	@Autowired
	private CustomerService customerService;

	/**
	 * 获得最新三条
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping(value = "/api/discovery/newest")
	@Cacheable(value = CacheConfig.GET_NEWEST_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	@ApiVersion({1,2})
	public Object getNewest() {
		return discoveryService.getNewest();
	}
	/**
	 * 爆品专区
	 * @param token
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping(value = "/api/discovery/hotZoneList")
	@ApiVersion({1,2})
	public Object getHotZoneList(@RequestHeader(value = "token", defaultValue = "") String token,
								 @RequestParam(value = "params",defaultValue="") String param) {
		if (StringUtils.isEmpty(token)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
		}
		JsonNode params = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			params = mapper.readTree(param);
		} catch (Exception e) {
			logger.error("params 出错" + e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null || params.get("pageNo") == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String shopId = StringUtils.getCustomerIdByToken(token);
		String pageNo = params.get("pageNo").asText();
		return discoveryService.getHotZoneList(Integer.parseInt(pageNo), shopId);
	}

	/**
	 * 获取线下活动列表
	 * isSelfJoined 0表示为参加 1表示参加
	 * isInProgress 0表示活动火热报名中 1表示报名已结束
	 * isFull 0表示未满, 1表示已满
	 * @param token
	 * @param pageNo
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping(value = "/api/discovery/offlineActivity/list")
	@ApiVersion({1,2})
	public Object getOfflineActivityList(@RequestHeader(value = "token", defaultValue = "") String token,
										 @RequestParam(value = "pageNo",defaultValue="1") String pageNo) {
		if (StringUtils.isEmpty(token)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
		}
		String cid = StringUtils.getCustomerIdByToken(token);
		int pageNum ;
		try{
			pageNum = Integer.parseInt(pageNo);
		}catch(ClassCastException cc){
			cc.printStackTrace();
			logger.error("参数错误,字符串转换成整型错误");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,ApiMsgConstants.FAILED_CODE);
		}
		return activityOfflineService.getOfflineActivityList(pageNum, cid);
	}

	/**
	 * 获取线下活动详情
	 * @param token
	 * @param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping(value = "/api/discovery/offlineActivity/getOfflineActivityDetail")
	@ApiVersion({1,2})
	public Object getOfflineActivityDetail(@RequestHeader(value = "token", defaultValue = "") String token,
										   @RequestParam(value = "offlineActivityId", defaultValue = "") String offlineActivityId){
		if(StringUtils.isBlank(offlineActivityId)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		return activityOfflineService.getOfflineActivity(offlineActivityId,StringUtils.getCustomerIdByToken(token));
	}

	/**
	 * 卖家参加活动
	 * 只有卖家才能参加活动,但是app端有卖家和卖家角色
	 * H5只认买家角色
	 * @param token
	 * @param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping(value = "/api/discovery/offlineActivity/joinOfflineActivity")
	@ApiVersion({1,2})
	public Object joinOfflineActivity(@RequestHeader(value = "token", defaultValue = "") String token,
									  @RequestParam(value = "offlineActivityId", defaultValue = "") String offlineActivityId) {
		// 简单验证,保证服务端不会出异常
		if (StringUtils.isEmpty(token)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
		}
		//判断名额是否已满,返回为1,则满了
		String isFull = activityOfflineService.getSeatStatus(offlineActivityId);
        String customerId = StringUtils.getCustomerIdByToken(token);
		if(ApiConstants.YES.equals(isFull)){
            //自动修改报名状态
            activityOfflineService.updateOpenStatus(customerId);
			return new MsgEntity(ApiMsgConstants.SEAT_IS_FULL,
					ApiMsgConstants.FAILED_CODE);
		}
		ActivityOffline activityOffline = new ActivityOffline();
		activityOffline.setId(offlineActivityId);
		//根据id查找活动
		activityOffline = activityOfflineService.get(activityOffline);
        //如果关闭了报名(当活动正在进行中时,点击"参加按钮",如果这时关闭了报名通道,则不能报名,所以必须验证报名状态是否为1)
		if(activityOffline.getOpenStatus() == 0){
            return new MsgEntity(ApiMsgConstants.ACTIVITY_TIME_OUT, ApiMsgConstants.FAILED_CODE);
        }
		//根据ID取得客户信息
		Customer customer = new Customer();
		customer.setId(customerId);
		customer = customerService.get(customer);
		CustomerOfflineActivity coa = new CustomerOfflineActivity();
		coa.setCustomer(customer);
		coa.setActivityOffline(activityOffline);
		// 根据customer_id和activity_id查询对象
		CustomerOfflineActivity cusoa = customerOfflineActivityService.get(coa);
		//如果已经参加过活动，不再插入数据 (服务端验证用户是否参加活动,防止用户直接调用接口)
		if(cusoa!=null){
			return new MsgEntity(ApiMsgConstants.ACTIVITY_JOINED,
					ApiMsgConstants.FAILED_CODE);
		}
		coa.preInsert();
		// 参加活动,向ec_activity_offline_customer表插入数据
		Map<String, Object> map = activityOfflineService.joinOfflineActivity(coa);
		logger.info("-------------------------------------"+"参加活动成功:" + map);
		return map;
	}
}
