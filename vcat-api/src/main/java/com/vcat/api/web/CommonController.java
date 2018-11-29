package com.vcat.api.web;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vcat.api.service.*;
import com.vcat.common.config.Global;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.utils.*;
import com.vcat.config.CacheConfig;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.entity.*;
import org.apache.log4j.Logger;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.sms.SmsClient;
import com.vcat.common.sms.SmsResult;
import com.vcat.common.sms.UcpSmsRestClient;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;

/**
 * 关于通用接口，请求控制器
 * @author cw
 *2015年5月6日 09:43:54
 */
@RestController
@ApiVersion({1,2})
public class CommonController extends RestBaseController {
	private String APP_LOG_PATH = Global.getConfig("vcat.app.log.path");
	@Autowired
	private RedisService redisService;
	@Autowired
	private AreaService areaService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private BankService bankService;
	@Autowired
	private SuggestService suggestService;
	@Autowired
	private ExpressService expressService;
	@Autowired
	private AppConfigService appConfigService;
	@Autowired
	private ShareCopywriteService shareCopywriteService;
	@Autowired
	private ServerConfigService cfgService;
	@Autowired
	private AppDomainService appDomainService;
	@Autowired
	private AppLogService appLogService;
	@Autowired
	private ShopService shopService;

	private static Logger logger = Logger.getLogger(CommonController.class);


//	@RequestMapping("/anon/app/log")
//	public Object processLog(@RequestParam(value = "params", defaultValue = "") String param,
//							 MultipartFile logFile){
//		// 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
//		JsonNode params =null;
//		try {
//			ObjectMapper mapper = new ObjectMapper();
//			params = mapper.readTree(param);
//		} catch (Exception e) {
//			logger.error("params 出错"+e.getMessage());
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		if (params == null) {
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		if(logFile!=null){
//			File dir = new File(APP_LOG_PATH + File.separator);
//			if(!dir.exists()){
//				dir.mkdirs();
//			}
//			String filePath = APP_LOG_PATH + File.separator + logFile.getOriginalFilename();
//			File localFile = new File(filePath);
//			logger.debug("local path = "+localFile.getPath());
//			try {
//				logFile.transferTo(localFile);
//			} catch (Exception e) {
//				logger.error(e.getMessage(),e);
//			}
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("deviceModel", params.get("deviceModel").asText());
//			map.put("osType", params.get("osType").asInt());
//			map.put("dataUrl", localFile.getPath());
//			appLogService.insertLog(map);
//			SendMailUtil.sendCommonMail("195822080@qq.com","["+DateUtils.getDateTime()+"] 有新上传的崩溃日志，请查看！",map.toString());
//			return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
//					ApiMsgConstants.SUCCESS_CODE);
//		}else{
//			logger.debug("file is not exsit");
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//	}

	/**
	 * 获取手机验证码接口，验证码存入redis，默认30分钟失效
	 * @param phoneNum
	 * @return
	 */
	@RequestMapping("/anon/getIdentifyCode")
	public Object getIdentifyCode(@RequestParam(value = "phoneNum",defaultValue="") String phoneNum,
								  @RequestParam(value = "isRegistered") boolean isRegistered){
		if(!IdcardUtils.isPhoneNum(phoneNum)){
			logger.debug("phoneNum is illegal");
			return new MsgEntity(ApiMsgConstants.PHONENUM_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
		}
        Map<String,Object> map = new HashMap<>();
		Customer customer = new Customer();
		customer.setPhoneNumber(phoneNum);
        //customer.setRegistered(ApiConstants.ALL_REGISTER);//1,2,3
		// 判断是否存在
		Customer cus = customerService.get(customer);
        //isRegistered为true 表示找回密码或者其他
        //isRegistered为false 表示注册
		//isRegistered为true，表示用户存在时发送短信，如果用户不存在，返回错误
		if(isRegistered && cus==null){
			logger.debug("customer is not exsit ");
			return new MsgEntity(ApiMsgConstants.CUSTOMER_NOTEXSIT_MSG,
					ApiMsgConstants.SUCCESS_CODE, ApiMsgConstants.SUCCESS_CODE_CUSTOMER_NOT_EXSIT);
		}
        ////isRegistered为false，表示用户不存在时发送短信，如果用户存在，返回错误
		else if(!isRegistered && cus!=null && !cus.getRegistered().equals(ApiConstants.INVITE_REGISTER)){
			logger.debug("customer is exsits ");
			return new MsgEntity(ApiMsgConstants.CUSTOMER_EXSIT_MSG,
					ApiMsgConstants.SUCCESS_CODE, ApiMsgConstants.SUCCESS_CODE_CUSTOMER_EXSIT);
		}
        if(!isRegistered && cus!=null && cus.getRegistered().equals(ApiConstants.INVITE_REGISTER)
				&& !StringUtils.isNullBlank(cus.getShop().getParentId() )
				&& cus.getShop().getUsedInviteCode() != null){
            map.put("shopNum",shopService.get(cus.getShop().getParentId()).getShopNum());
			map.put("inviteCode",shopService.getInviteCode(cus.getShop().getUsedInviteCode().getId()));
        }
		//发送短信
		//获取六位数字验证码
		String code =IdGen.getRandomNumber(6);
		//生成验证码并存入redis，默认30分钟过期
		redisService.putIdentifyingCode(phoneNum, code);

		ThreadPoolUtil.execute(() -> {
            String type= getSmsServer();
            if("2".equalsIgnoreCase(type)){
				StringBuffer sb = new StringBuffer();
				sb.append("#name#=").append("V猫").append("&#code#=").append(code)
						.append("&#hour#=半").append("&#company#=V猫小店");
				SmsResult result = SmsClient.tplSendSms(4, sb.toString(), phoneNum);
                logger.info("手机号码:"+phoneNum+",验证码:"+code+",结果:"+result);
            }else{
                if(UcpSmsRestClient.templateSMS(true, UcpSmsRestClient.VERIFY_TMP_ID, phoneNum, code+",半")){
                    logger.info("手机号码:" + phoneNum + ",验证码:" + code + "发送成功");
                }else{
                    logger.warn("手机号码:"+phoneNum+",验证码:"+code+"发送失败");
                }
            }
        });


		//return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
		//		ApiMsgConstants.SUCCESS_CODE, ApiMsgConstants.SUCCESS_CODE_SUB);
        map.put("msg",ApiMsgConstants.SUCCESS_MSG);
        map.put("code",ApiMsgConstants.SUCCESS_CODE);
        map.put("status",ApiMsgConstants.SUCCESS_CODE_SUB);
        return map;
	}

	/**
	 * 使用哪种验证码平台 1.云之讯 2.云片
	 * @return
	 */
	private String getSmsServer(){
		return cfgService.findCfgValue("sms_verification_server");
	}
	/**
	 * 获取地区信息
	 * @param areaId
	 * @return
	 */
	@RequestMapping("/anon/getAreaList")
	public Object getArea(@RequestParam(value = "areaId",defaultValue="") String areaId){
		if(StringUtils.isEmpty(areaId)){
			areaId =ApiConstants.AREA_CODE;
		}
		
		return areaService.getAreaList(areaId);
	}
	//获取物流公司
	@RequestMapping("/anon/getExpressList")
	public Object getExpressList(){
		List<Express> list = expressService.getExpressList();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list",list);
		return map;
	}
	/**
	 * 获取银行卡信息list
	 * @return
	 */
	@RequestMapping("/api/getBankList")
	public Object getBankList(@RequestHeader(value = "token", defaultValue = "") String token){
		
		List<Bank> list = bankService.findList(null);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", list);
		return map;
	}
	/**
	 * 建议与反馈
	 * @param token
	 * @param param
	 * @return
	 */
	@RequestMapping("/api/suggest")
	public Object addSuggest(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param){
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错"+e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String contact = params.getString("contact");
		String content = params.getString("content");
		if(StringUtils.isBlank(content)||StringUtils.isBlank(contact)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		Suggest sugg = new Suggest();
		sugg.preInsert();
		sugg.setContact(contact);
		sugg.setContent(content);
		suggestService.insert(sugg);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		return map;
	}
	/**
	 * 获取app的配置
	 * @return
	 */
	@RequestMapping("/anon/getAppConfig")
	@Cacheable(value = CacheConfig.GET_APP_CONFING_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public Object getAppConfig() {
		List<AppConfig> list = appConfigService.findList(null);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		if (list != null && list.size() > 0) {
			for (AppConfig config : list) {
				if ("promote_image".equals(config.getCfgName())) {
					map.put("promoteImagePath", QCloudUtils
							.createOriginalDownloadUrl(config.getCfgValue()));
				}else{
					map.put(config.getCfgName(), config.getCfgValue());
				}
			}
		}
		return map;
	}
	/**
	 * 获取app的配置
	 * @return
	 */
	@RequestMapping("/anon/getServiceAddress")
	@Cacheable(value = CacheConfig.GET_SERVICE_ADDRESS_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public Object getServiceAddress(@RequestParam(value = "deviceType",defaultValue="") String deviceType,
			@RequestParam(value = "appVersion",defaultValue="") String appVersion) {
		if(StringUtils.isBlank(appVersion)||StringUtils.isBlank(deviceType)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		Map<String, Object> result = appDomainService.getByDeviceTypeAndAppVersion(deviceType, appVersion);
		if(result == null || result.isEmpty()){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String serviceAddress = String.valueOf(result.get("service_domain"));
		String environment = String.valueOf(result.get("environment"));
		if(StringUtils.isBlank(serviceAddress)){
			serviceAddress = ApiConstants.VCAT_DOMAIN;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("serviceAddress", serviceAddress);
		map.put("environment", environment);
		return map;
	}
	/**
	 * 获取分享文案
	 * @return
	 */
	@RequestMapping("/anon/getCopywriteList")
	@Cacheable(value = CacheConfig.GET_COPYWRITE_LIST_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public Object getCopywriteList(@RequestParam(value = "type", defaultValue = "") String type){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		if (StringUtils.isBlank(type)) {
			List<ShareCopywrite> list = shareCopywriteService.findList(null);
			map.put("list", list);
		}
		else{
			ShareCopywrite cy = new ShareCopywrite();
			cy.setType(type);
			ShareCopywrite Copywrite = shareCopywriteService.get(cy);
			map.put("copywrite", Copywrite);
		}
		return map;
	}
}
