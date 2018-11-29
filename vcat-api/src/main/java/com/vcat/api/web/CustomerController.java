package com.vcat.api.web;

import com.alibaba.fastjson.JSONObject;
import com.vcat.api.service.*;
import com.vcat.common.cloud.QCloudClient;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.FileUtils;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.IdcardUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.entity.*;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * 关于用户统一请求处理类
 *
 * @author cw 2015年5月6日 09:43:54
 */
@RestController
@ApiVersion({1,2})
public class CustomerController extends RestBaseController {

	private static Logger logger = Logger.getLogger(CustomerController.class);

	@Autowired
	private RedisService redisService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private ThirdPartyLoginService thirdPartyLoginService;
	@Autowired
	private ThirdLoginTypeService thirdLoginTypeService;
	@Autowired 	
	private ServletContext servletContext;
	@Autowired 	
	private CustomerAddressService customerAddressService;
	@Autowired 	
	private AddressService addressService;
	@Autowired 	
	private BrandService brandService;
	@Autowired 
	private ShopBrandService shopBrandService;
	@Autowired
	private InviteEarningService inviteEarningService;
	@Autowired
	private CustomerLogService customerLogService;
	@Autowired
	private CustomerRoleService customerRoleService;
	@Autowired
	private ChatService chatService;

	/**
	 * app注册接口
	 * 默认买家 + 卖家角色 + 新建店铺
	 * is_registered = 1
	 * 如果填写了上家店铺编号,则增加上家店铺收入,添加此用户30猫币
	 * @param param
	 * @return
     */
	@RequestMapping("/anon/register")
	public Object customerRegister(@RequestParam(value = "params",defaultValue="") String param) {
		return this.register2(param,ApiConstants.SELLER_TYPE);
	}

	/**
	 * 邀请好友注册(网页注册,邀请注册后必须在客户端再注册一次,此次注册无角色绑定)
	 * 有店铺 is_registered = 2
	 * @param param 上家店铺编号,新注册电话,验证码
	 * @return
	 */
	//@RequestMapping("/anon/invite")
	//public Object invite(@RequestParam(value = "params",defaultValue="") String param) {
     //   Object obj = validateJson(param);
     //   if(obj instanceof MsgEntity) return obj;
     //   JSONObject params        = (JSONObject) obj;
     //   String phoneNum = params.getString("phoneNum");
     //   if(!IdcardUtils.isPhoneNum(phoneNum)){
     //       logger.error("phoneNum format error");
     //       return new MsgEntity(ApiMsgConstants.PHONENUM_ERROR_MSG,
     //               ApiMsgConstants.FAILED_CODE);
     //   }
     //   String identifyCode = params.getString("identifyCode");
	//	//验证码是否正确,redis验证
	//	if (StringUtils.isBlank(identifyCode)
	//			|| !identifyCode.equals(redisService
	//			.getIdentifyingCode(phoneNum))) {
	//		logger.debug("identifyCode is incurrent ");
	//		return new MsgEntity(ApiMsgConstants.IDENTIFYCODE_INCURRENT_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	String password = params.getString("password");
	//	if(StringUtils.isBlank(password)){
	//		logger.error("phoneNum format error");
	//		return new MsgEntity(ApiMsgConstants.PASSWORD_EMPTY_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
     //   String 	inviteCode = params.getString("inviteCode");
     //   Shop parentShop = shopService.getShopByInviteCode(inviteCode);
     //   if(parentShop == null){
     //       logger.error("inviteCode error");
     //       return new MsgEntity(ApiMsgConstants.PARENT_SHOP_NOT_FIND,ApiMsgConstants.FAILED_CODE);
     //   }
	//	return customerService.inviteRegistration(phoneNum, parentShop);
	//}

	@RequestMapping("/anon/invite")
	public Object invite2(@RequestParam(value = "params",defaultValue="") String param) {
		return this.register2(param,ApiConstants.INVITE_TYPE);
	}

	/**
	 * 网页端注册接口
	 * 默认买家角色 无店铺
	 * @param param
	 * @return
	 */
	@RequestMapping("/anon/buyerRegister")
	public Object buyerRegister(@RequestParam(value = "params",defaultValue="") String param){
		return this.register2(param,ApiConstants.BUYER_TYPE);
	}
	/**
	 * 卖家重设密码接口
	 * @return
	 */
	@RequestMapping("/anon/resetPassWord")
	public Object resetPassWord(@RequestParam(value = "params",defaultValue="") String param) {
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
		String phoneNum = params.get("phoneNum") == null ? null : params
				.getString("phoneNum");
		if (!IdcardUtils.isPhoneNum(phoneNum)) {
			logger.error("phoneNum is illegal");
			return new MsgEntity(ApiMsgConstants.PHONENUM_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String passWord = params.get("passWord") == null ? null : params
				.getString("passWord");
		if (StringUtils.isBlank(passWord)) {
			logger.error("password is empty");
			return new MsgEntity(ApiMsgConstants.PASSWORD_EMPTY_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String identifyCode = params.get("identifyCode") == null ? null
				: params.getString("identifyCode");
		// 验证码是否正确,redis验证
		if (StringUtils.isBlank(identifyCode)
				|| !identifyCode.equals(redisService
						.getIdentifyingCode(phoneNum))) {
			logger.debug("identifyCode is incurrent ");
			return new MsgEntity(ApiMsgConstants.IDENTIFYCODE_INCURRENT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		Customer customer = new Customer();
		customer.setPhoneNumber(phoneNum);
		// 判断是否存在
		Customer cus = customerService.get(customer);
		if (cus == null) {
			logger.debug("customer is not exsit ");
			return new MsgEntity(ApiMsgConstants.CUSTOMER_NOTEXSIT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		cus.setPassword(passWord);
		customerService.update(cus);
		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
				ApiMsgConstants.SUCCESS_CODE);
	}

	/**
	 * app端登录接口
	 * @return token
	 */
	@RequestMapping("/anon/sellerLogin")
	public Object sellerLogin(@RequestParam(value = "params",defaultValue="") String param) {
		return login(param,ApiConstants.SELLER_TYPE);
	}

	/**
	 * 退出登录
	 * @return
	 */
	@RequestMapping("/api/logout")
	public Object logout(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params",defaultValue="") String param){
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
		int deviceType = params.getInteger("deviceType");
		String appVersion = params.getString("appVersion");
		String customerId = StringUtils.getCustomerIdByToken(token);
		Customer newCus = customerService.get(customerId);
		if(newCus!=null){
//			newCus.setDeviceType(null);
			newCus.setPushToken(null);
			customerService.update(newCus);
		}
		CustomerLog log = new CustomerLog();
		log.preInsert();
		log.setCustomerId(customerId);
		log.setDeviceType(deviceType);
		log.setDeviceVersion(appVersion);
		//类型为退出
		log.setType(ApiConstants.LOGOUT);
		customerLogService.insert(log);
		//删除toekn
		redisService.deleteToken(customerId);
		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
				ApiMsgConstants.SUCCESS_CODE);
	}
	/**
	 * 网页方式登录
	 * @return token
	 */
	@RequestMapping("/anon/buyerLogin")
	public Object buyerLogin(@RequestParam(value = "params",defaultValue="") String param) {
		return login(param,ApiConstants.BUYER_TYPE);
	}

	/**
	 * 检查三方登录的卖家是否绑定手机号
	 * @return 2015年5月8日 14:33:31
	 */
	@RequestMapping("/anon/3rdloginBind")
	public Object loginBind(@RequestParam(value = "params",defaultValue="") String param) {
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错"+e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if(params==null){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String code = params.getString("3rdLoginType");
		String uniqueId = params.getString("uniqueId");
		if (StringUtils.isBlank(code)
				|| StringUtils.isBlank(uniqueId)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		// 查询是否绑定customer
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		ThirdPartyLogin td = thirdPartyLoginService
				.selectByTypeCodeAndUniqueId(code, uniqueId);
		if (td==null) {
			map.put("status", false);
		} else {
			String token = StringUtils.produceSellerToken(td.getCustomer().getId());
			redisService.putAccessToken(td.getCustomer().getId(), token);
			//生成买家token
			String buyerToken = StringUtils.produceBuyerToken(td.getCustomer().getId());
			buyerToken = redisService.putBuyerToken(td.getCustomer().getId(), buyerToken);
			map.put("status", true);
			map.put("token", token);
			map.put("huanid",StringUtils.getCustomerIdByToken(token));
			map.put("buyerToken",buyerToken);
            //添加客户级别
            Shop shop = shopService.get(td.getCustomer().getId());
            map.put("isVIP",shop.getAdvancedShop() == 1);
		}
		return map;
	}

	/**
	 * 第三方登录方式
     * 除微信以外,其他方式都需要绑定手机号
     * 以第三方登录时如果没有绑定手机号,调用此方法(绑定手机号)
	 * @return
	 */
	@RequestMapping("/anon/bindPhoneNum")
	public Object bindPhoneNum(@RequestParam(value = "params",defaultValue="") String param) {
        Object obj = paramSimpleValidate(param);
        if(obj instanceof MsgEntity){
            return obj;
        }
        JSONObject params   = (JSONObject) obj;
        String phoneNum = params.getString("phoneNum");
        String passWord = params.getString("passWord");
        String identifyCode = params.get("identifyCode") == null ? ""
                : params.getString("identifyCode");
		if (StringUtils.isBlank(identifyCode)
				|| !identifyCode.equals(redisService
						.getIdentifyingCode(phoneNum))) {
			logger.debug("identifyCode is incurrent ");
			return new MsgEntity(ApiMsgConstants.IDENTIFYCODE_INCURRENT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
        String code = params.get("3rdLoginType") == null ? ""
                : params.getString("3rdLoginType");
        String uniqueId = params.get("uniqueId") == null ? ""
                : params.getString("uniqueId");
        String userName = params.get("userName") == null ? ""
                : params.getString("userName");
        //获取不重复的用户名
		userName = customerService.getUserName(userName);
		String avatarUrl = params.getString("avatarUrl") == null ? ""
				: params.getString("avatarUrl");
		if(!StringUtils.isBlank(avatarUrl) && avatarUrl.contains("wx.qlogo.cn")){
			logger.debug("处理微信头像:"+avatarUrl);
			File file = null;
			try {
				file = new File(FileUtils.download(avatarUrl, "wx_avatar_img_" + new Date().getTime(), "/home2/temp"));
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			avatarUrl = QCloudClient.uploadImage(file);
			logger.debug("处理后头像:"+avatarUrl);
		}
		//查询登录方式
		ThirdLoginType thirdLoginType = new ThirdLoginType();
		thirdLoginType.setCode(code);
		thirdLoginType = thirdLoginTypeService.get(thirdLoginType);
		if(thirdLoginType==null){
			logger.debug("loginType is incurrent ");
			return new MsgEntity(ApiMsgConstants.IDENTIFYCODE_INCURRENT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		ThirdPartyLogin td = thirdPartyLoginService.selectByTypeCodeAndUniqueId(code, uniqueId);
		if (td!=null) {
			logger.debug("phoneNum is binded");
			return new MsgEntity(ApiMsgConstants.PHONENUM_BINDED_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String parentShopId = params.getString("parentShopId");
		Customer customer = new Customer();
		customer.setPhoneNumber(phoneNum);
		// 判断是否存在,普通注册
        //customer.setRegistered(ApiConstants.ALL_REGISTER); //-1
		Customer cus = customerService.get(customer);
        // 当绑定的用户电话在数据库中已有相同号码且以前没有被邀请注册时,退出
		if (cus != null && !cus.getRegistered().equals(ApiConstants.INVITE_REGISTER)) {
			logger.debug("customer is exsits ");
			return new MsgEntity(ApiMsgConstants.CUSTOMER_EXSIT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}else if(cus != null && cus.getRegistered().equals(ApiConstants.INVITE_REGISTER)){ //如果以前被邀请过
			cus.setRegistered(ApiConstants.SELLER_REGISTER);
			cus.setPassword(passWord);
            cus.setAvatarUrl(avatarUrl);
            cus.setUserName(userName);
            ThirdPartyLogin thirdLogin = new ThirdPartyLogin();
            thirdLogin.preInsert();
            thirdLogin.setCustomer(cus);
            thirdLogin.setUniqueId(uniqueId);
            thirdLogin.setThirdLoginType(thirdLoginType);
            customer = thirdPartyLoginService.inviteBind3rdLogin(cus, thirdLogin,parentShopId);
        }else if(cus == null){//用户第一次绑定账号,且没有被邀请过
            // 直接调用
            customer.preInsert();
			customer.setRegistered(ApiConstants.SELLER_REGISTER);
			customer.setPassword(passWord);
            customer.setAvatarUrl(avatarUrl);
            customer.setUserName(userName);
            ThirdPartyLogin thirdLogin = new ThirdPartyLogin();
            thirdLogin.preInsert();
            thirdLogin.setCustomer(customer);
            thirdLogin.setUniqueId(uniqueId);
            thirdLogin.setThirdLoginType(thirdLoginType);
            thirdPartyLoginService.bind3rdLogin(thirdLogin,parentShopId);
        }
		//返回信息
		String token = StringUtils.produceSellerToken(customer.getId());
		redisService.putAccessToken(customer.getId(),token);
		
		//生成买家token
		String buyerToken = StringUtils.produceBuyerToken(customer.getId());
		buyerToken = redisService.putBuyerToken(customer.getId(), buyerToken);
		
		Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("token", token);
		map.put("huanId", StringUtils.getCustomerIdByToken(token));
        map.put("huanid",StringUtils.getCustomerIdByToken(token));
		map.put("buyerToken", buyerToken);
		//添加客户级别
		Shop shop = shopService.get(customer.getId());
		map.put("isVIP",shop.getAdvancedShop() == 1);
		return map;
	}

	/**
	 * 用户上传头像接口
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/uploadAvatar")
	public Object uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request,
							   @RequestHeader(value = "token", defaultValue = "") String token) {
		//查询用户
		String customerId = StringUtils.getCustomerIdByToken(token);
		Customer customer = customerService.get(customerId);
		if (customer == null) {
			logger.debug("customer is not exsit ");
			return new MsgEntity(ApiMsgConstants.CUSTOMER_NOTEXSIT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		//上传图片到第三方服务器
		if(file == null){
			logger.debug("file is not exsit ");
			return new MsgEntity(ApiMsgConstants.IMAGE_NOT_EXSIT,
					ApiMsgConstants.FAILED_CODE);
		}
		String path = servletContext.getRealPath("/");
		logger.debug("service path = " + path);
		String imagePath = path + File.separator + file.getName() + IdGen.getRandomNumber(10);
		File imageFile = new File(imagePath);
		logger.debug("imageFile path = "+imageFile.getPath());
		try {
			file.transferTo(imageFile);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		//上传文件
		String imageId = QCloudClient.uploadImage(imageFile);
		if(imageId==null){
			return new MsgEntity(ApiMsgConstants.UPLOAD_IMAGE_FAILED,
					ApiMsgConstants.FAILED_CODE);
		}
		//获取文件路径
		String imageUrl = QCloudUtils.createThumbDownloadUrl(imageId,ApiConstants.DEFAULT_AVA_THUMBSTYLE);
		logger.debug("imageUrl = " + imageUrl);
		customer.setAvatarUrl(imageId);
		customerService.update(customer);
		//删除临时文件
		imageFile.delete();
		ChatMessageHelper.sendChangedAvatarMessage(shopService.getAllFriends(customerId), customerId,
				chatService.getAvatarUrl(imageId));
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("avatarUrl", imageUrl);
		return map;
	}
	/**
	 * 用户更新用户信息接口
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/updateCustomer")
	public Object updateCustomer(@RequestHeader(value = "token", defaultValue = "") String token,
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
		//获取更改的用户名或者身份证
		String userName = params.getString("userName");
		String idCard = params.getString("idCard");
		if (StringUtils.isBlank(userName) && StringUtils.isBlank(idCard)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String customerId = StringUtils.getCustomerIdByToken(token);
		Customer customer = customerService.get(customerId);
		if (customer == null) {
			logger.debug("customer is not exsit ");
			return new MsgEntity(ApiMsgConstants.CUSTOMER_NOTEXSIT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		//判读用户名是否重复
		Customer cu = new Customer();
		cu.setUserName(userName);
		Customer cu1 = customerService.get(cu);
		if(cu1 != null){
			logger.debug("customer name exsit ");
			return new MsgEntity(ApiMsgConstants.USERNAME_EXSIT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if(StringUtils.isBlank(userName)){
			userName = customer.getUserName();
		}
		// 单独设置用户名
		if (!StringUtils.isBlank(userName) && idCard == null) {
			customer.setUserName(userName);
		} else
		// 单独设置身份证号
		if (IdcardUtils.validateCard(idCard) && userName == null) {
			customer.setIdCard(idCard);
		} else 
		if (!StringUtils.isBlank(userName) && IdcardUtils.validateCard(idCard)) {
			customer.setUserName(userName);
			customer.setIdCard(idCard);
		}else
		{
			return new MsgEntity(ApiMsgConstants.IDCARD_ILLEGAL_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		// 更新店铺名字
		if (!StringUtils.isBlank(userName)) {
			Shop shop = new Shop();
			shop.setId(customerId);
			Shop s = shopService.get(shop);
			if (s != null) {
				s.setName(userName);
				shopService.update(s);
			}
		}
		customerService.update(customer);
		//得到他的上家和下家,并通知他们昵称改变了
		ChatMessageHelper.sendChangedUsernameMessage(shopService.getAllFriends(customerId),customerId, userName);
		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
				ApiMsgConstants.SUCCESS_CODE);
	}
	/**
	 * 买家添加或更新收货地址
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/savaOrUpdateAddress")
	public Object saveOrUpdateBuyerAddress(@RequestHeader(value = "token", defaultValue = "") String token,
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
		String buyerId = StringUtils.getCustomerIdByToken(token);
		String addressId = params.getString("addressId");
		String province = params.getString("provinceId");
		String city = params.getString("cityId");
		String district = params.getString("districtId");
		String detailAddress = params.getString("detailAddress");
		String deliveryName = params.getString("deliveryName");
		String deliveryPhone = params.getString("deliveryPhone");
		String addressName = params.getString("addressName");
		boolean isDefault = params.getBoolean("isDefault");
		//判断是否设置为默认地址,如果是，取消之前的默认地址
		if(isDefault){
			customerAddressService.cancalDefault(buyerId);
		}else{
			//如果只有一个地址，那么不允许用户设置地址为不默认
			Map<String, Object> map = customerAddressService
					.countAddress(buyerId);
			if (map != null && map.size() != 0) {
				long count = (long) map.get("count");
				String DefaultaddressId = (String) map.get("addressId");
				if (count == 0) {
					return new MsgEntity(ApiMsgConstants.ADDRESS_FAILED,
							ApiMsgConstants.FAILED_CODE);
				} else if (!StringUtils.isBlank(addressId)
						&& addressId.equals(DefaultaddressId) && count == 1) {
					return new MsgEntity(ApiMsgConstants.ADDRESS_FAILED,
							ApiMsgConstants.FAILED_CODE);
				}
			}
		}
		CustomerAddress customerAddress = null;
		//通过买家id和地址id查询地址是否存在，如果存在，就使用更新
		if(!StringUtils.isBlank(addressId)){
			CustomerAddress cuAdd = new CustomerAddress();
			Customer cus = new Customer();
			cus.setId(buyerId);
			Address adds = new Address();
			adds.setId(addressId);
			cuAdd.setCustomer(cus);
			cuAdd.setAddress(adds);
			customerAddress =  customerAddressService.get(cuAdd);
			if(customerAddress==null){
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
			customerAddress.setAddressName(addressName);
			customerAddress.setDefault(isDefault);
			customerAddress.getAddress().setProvinceId(province);
			customerAddress.getAddress().setCityId(city);
			customerAddress.getAddress().setDistrictId(district);
			customerAddress.getAddress().setDetailAddress(detailAddress);
			customerAddress.getAddress().setDeliveryName(deliveryName);
			customerAddress.getAddress().setDeliveryPhone(deliveryPhone);
			customerAddressService.updateAddress(customerAddress);
		}else{
			//插入新数据
			customerAddress = new CustomerAddress();
			customerAddress.preInsert();
			Customer cus = new Customer();
			cus.setId(buyerId);
			Address adds = new Address();
			adds.preInsert();
			addressId = adds.getId();
			customerAddress.setCustomer(cus);
			customerAddress.setAddress(adds);
			customerAddress.setAddressName(addressName);
			customerAddress.setDefault(isDefault);
			customerAddress.getAddress().setProvinceId(province);
			customerAddress.getAddress().setCityId(city);
			customerAddress.getAddress().setDistrictId(district);
			customerAddress.getAddress().setDetailAddress(detailAddress);
			customerAddress.getAddress().setDeliveryName(deliveryName);
			customerAddress.getAddress().setDeliveryPhone(deliveryPhone);
			customerAddressService.saveAddress(customerAddress);
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("addressId", addressId);
		return map;
	}
	/**
	 * 买家获取收货地址列表
	 * @param token
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/getBuyerAddressList")
	public Object getBuyerAddressList(@RequestHeader(value = "token", defaultValue = "") String token,
									  @RequestParam(value = "params", defaultValue = "") String param){
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
		String buyerId = StringUtils.getCustomerIdByToken(token);
		String condition = params.getString("condition");
		int pageNo = params.getIntValue("pageNo");
		int count = customerAddressService.getAddressCounts(buyerId,condition);
		Pager page    = new Pager();
		page.setPageNo(pageNo);
		page.setRowCount(count);
		page.doPage();
		List<Address> list = customerAddressService.getAddressList(buyerId,page,condition);
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("addressList", list);
		map.put("page",page);
		return map;
	}
	/**
	 * 删除地址
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/deleteBuyerAddress")
	public Object deleteBuyerAddress(@RequestHeader(value = "token", defaultValue = "") String token,
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
		String buyerId = StringUtils.getCustomerIdByToken(token);
		String addressId = params.getString("addressId");
		if(StringUtils.isBlank(addressId)||StringUtils.isBlank(buyerId)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		customerAddressService.deleteAddress(buyerId,addressId);
		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
				ApiMsgConstants.SUCCESS_CODE);
	}

	/**
	 * 卖家/买家注册统一调用接口
	 * @param param 参数
	 * @param type 类型
	 * @return
	 * 2016.3月.2.dsj 注册改版
	 * 现在必须要邀请码才能注册
     */
    private Object register(String param,Integer type){
        Object obj = paramSimpleValidate(param);
        if(obj instanceof MsgEntity) return obj;
        JSONObject params        = (JSONObject) obj;
        String     phoneNum      = params.getString("phoneNum");
        String     passWord      = params.getString("passWord");
        String     identifyCode  = params.getString("identifyCode");
//		String     parentShopNum = params.getString("shopNum");
        String     parentShopId = params.getString("parentShopId");
        parentShopId = !StringUtils.isBlank(parentShopId) ? parentShopId : "-1";
        if (StringUtils.isBlank(identifyCode) || !identifyCode.equals(redisService.getIdentifyingCode(phoneNum))) {
            logger.debug("identifyCode is incurrent ");
            return new MsgEntity(ApiMsgConstants.IDENTIFYCODE_INCURRENT_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        //1.不为null 肯定正确
        //2.为null
        Shop parentShop = shopService.get(parentShopId);
        // 1. 如果填写了但是没有对应的店铺
        if(!parentShopId.equals("-1") && parentShop == null){
            return new MsgEntity(ApiMsgConstants.PARENT_SHOP_NOT_FIND,ApiMsgConstants.FAILED_CODE);
        }
        // 2. 如果没有传 parentShopId参数,则默认 parentShop 为 null
        Customer customer = new Customer();
        customer.setPhoneNumber(phoneNum);
        customer.setPassword(passWord); // is_registered = null 查0 1 2 3
        //customer.setRegistered(ApiConstants.ALL_REGISTER);
        Customer isExsitCustomer = customerService.get(customer);
        if(isExsitCustomer == null){ // 如果数据库中没有此号码,则直接注册
            customer.preInsert();
            customer.setRegistered(ApiConstants.SELLER_REGISTER);
            if(Objects.equals(type, ApiConstants.SELLER_TYPE)){ //app端
                customerService.saveCustomer(customer,parentShop);
            }else {// h5注册
                customerService.saveCustomer(customer);
            }
        }
        // 如果存在相同的电话且注册状态不为2时
        else if (!isExsitCustomer.getRegistered().equals(ApiConstants.INVITE_REGISTER)) {
            logger.debug("customer is exsits ");
            return new MsgEntity(ApiMsgConstants.CUSTOMER_EXSIT_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        // 如果数据库中存在相同号码且注册状态为2时(以前被邀请注册过)
        else if(isExsitCustomer.getRegistered().equals(ApiConstants.INVITE_REGISTER)){
            //修改注册状态
            isExsitCustomer.setRegistered(ApiConstants.SELLER_REGISTER);
            isExsitCustomer.setPassword(passWord);
            if(Objects.equals(type, ApiConstants.SELLER_TYPE)){// app
                customer = customerService.inviteCustomer(isExsitCustomer,parentShop);
            }else{//h5注册
                customer = customerService.inviteCustomer(isExsitCustomer);
            }
        }
        return resultMap(customer,type);
    }

    //注册改版
    private Object register2(String param,Integer type){
        Object obj = paramSimpleValidate(param);
        if(obj instanceof MsgEntity) return obj;
        JSONObject params        = (JSONObject) obj;
        String     phoneNum      = params.getString("phoneNum");
        String     passWord      = params.getString("passWord");
        String     identifyCode  = params.getString("identifyCode");
		String     parentShopId = "";
		if(Objects.equals(type, ApiConstants.INVITE_TYPE)){
			String inviteCode = params.getString("inviteCode");
			Shop parentShop = shopService.getShopByInviteCode(inviteCode);
		   	if(parentShop == null){
			   logger.error("inviteCode error");
			   return new MsgEntity(ApiMsgConstants.PARENT_SHOP_NOT_FIND,ApiMsgConstants.FAILED_CODE);
		   	}
			parentShopId = parentShop.getId();
		}else{
			parentShopId  = params.getString("parentShopId");
		}
        if (StringUtils.isBlank(identifyCode) || !identifyCode.equals(redisService.getIdentifyingCode(phoneNum))) {
            logger.debug("identifyCode is incurrent ");
            return new MsgEntity(ApiMsgConstants.IDENTIFYCODE_INCURRENT_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        Shop parentShop = shopService.get(parentShopId);
        if((Objects.equals(type, ApiConstants.SELLER_TYPE) || Objects.equals(type, ApiConstants.INVITE_TYPE))&& (StringUtils.isBlank(parentShopId) || parentShop == null)){
            return new MsgEntity(ApiMsgConstants.PARENT_SHOP_NOT_FIND,ApiMsgConstants.FAILED_CODE);
        }
        Customer customer = new Customer();
        customer.setPhoneNumber(phoneNum);
        customer = customerService.get(customer);
        if(customer == null){ // 如果数据库中没有此号码,则直接注册
            Customer cus = new Customer();
            cus.setPhoneNumber(phoneNum);
            cus.setPassword(passWord);
            cus.preInsert();
            cus.setRegistered(ApiConstants.SELLER_REGISTER);
			customer = cus;
            if(Objects.equals(type, ApiConstants.SELLER_TYPE) || Objects.equals(type, ApiConstants.INVITE_TYPE)){ //app端
                customerService.saveCustomer(cus,parentShop);
            }else {// h5注册(不需要邀请码)在 app端第一次登录时需要邀请码
                customerService.saveCustomer(cus);
            }
        }
        // 如果存在相同的电话且注册状态不为2时
        else if (!customer.getRegistered().equals(ApiConstants.INVITE_REGISTER)) {
            logger.debug("customer is exsits ");
            return new MsgEntity(ApiMsgConstants.CUSTOMER_EXSIT_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        // 如果数据库中存在相同号码且注册状态为2时(以前被邀请注册过)
        else if(customer.getRegistered().equals(ApiConstants.INVITE_REGISTER)){
            //修改注册状态
            customer.setRegistered(ApiConstants.SELLER_REGISTER);
            customer.setPassword(passWord);
            if(Objects.equals(type, ApiConstants.SELLER_TYPE)){// app端注册
                customer = customerService.inviteCustomer(customer,parentShop);
            }else if (Objects.equals(type, ApiConstants.INVITE_TYPE )){//invit need update parent shop info
				customer = customerService.inviteCustomer(customer,parentShop);
			}
			else{//h5注册注册
                customer = customerService.inviteCustomer(customer);
            }
        }
        return resultMap(customer,type);
    }

	/**
	 * app端/网页端登录统一调用接口
	 * @param param
	 * @param type
     * @return
     */
	private Object login(String param,Integer type){
		Object obj = paramSimpleValidate(param);
		if(obj instanceof MsgEntity){
			return obj;
		}
		JSONObject params   = (JSONObject) obj;
		String     phoneNum = params.getString("phoneNum");
		String     passWord = params.getString("passWord");

		// 注册状态为0(微信注册)时,电话号码为8位随机数,不用绑定手机号
		// 注册状态为1(app端注册(is_registered=1)或者网页端被邀请后在网页端注册激活(注册状态由2-->1))
		// 注册状态为2(邀请注册)时,账号还未激活,密码为6为随机数,必须在客户端或者网页端再注册一次才能使用(注册状态由2-->1)
		// 注册状态为3(除微信注册外的其他第三方注册方式)

        Customer customer = new Customer();
        customer.setPhoneNumber(phoneNum);
        //customer.setRegistered(ApiConstants.ALL_REGISTER);
		customer = customerService.get(customer);
		if(customer == null){
			logger.debug("customer is not exsit ");
			return new MsgEntity(ApiMsgConstants.CUSTOMER_NOTEXSIT_MSG,
					ApiMsgConstants.SUCCESS_CODE, ApiMsgConstants.SUCCESS_CODE_CUSTOMER_NOT_EXSIT);
		}
		// 如果是邀请注册过但是还未激活的用户,弹出提示,让他重新注册
		else if(customer.getRegistered().equals(ApiConstants.INVITE_REGISTER)){
			logger.debug("customer is not active ");
			return new MsgEntity(ApiMsgConstants.ALREADY_INVITED, //用户已被邀请注册过,请再次注册绑定账号(跳到注册页面)
					ApiMsgConstants.SUCCESS_CODE, ApiMsgConstants.SUCCESS_CODE_CUSTOMER_NOT_ACTIVE);
		}
		//如果有用户信息且已经激活但是密码不正确
		else if(!customer.getPassword().equals(passWord)){
			logger.debug("password incurrents ");
			return new MsgEntity(ApiMsgConstants.PASSWORD_INCURRENT,
					ApiMsgConstants.FAILED_CODE);
		}
		// 1.(买家第一次从 app 登陆)
		//if(Objects.equals(type, ApiConstants.SELLER_TYPE) && customerRoleService.findRole(customer.getId(),ApiConstants.SELLER) == null){
		if(Objects.equals(type, ApiConstants.SELLER_TYPE) &&  // app登录
                shopService.get(customer.getId()) == null     // 买家在网页注册成功后第一次在app端登录(无店铺信息)
				){
            //跳转到邀请页面
            return new MsgEntity(ApiMsgConstants.NO_SHOP_MSG,
                    ApiMsgConstants.SUCCESS_CODE,ApiMsgConstants.SUCCESS_CODE_CUSTOMER_FIRST_LOGIN);
		}else if(Objects.equals(type, ApiConstants.SELLER_TYPE)
				&& shopService.get(customer.getId()) != null
				&& customerRoleService.findRole(customer.getId(),ApiConstants.SELLER) == null){ // 买家被邀请注册过 只是没有卖家角色
            // 卖家角色
            customerService.createRole(customer, ApiConstants.SELLER);
        }
		return this.resultMap(customer,type);
	}
	/**
	 * 登陆和注册的参数验证方法
	 * @param param
	 * @return
     */
	private Object paramSimpleValidate(String param){
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
			if (params == null) {
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
		} catch (Exception e) {
			logger.error("params 出错"+e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String phoneNum = params.getString("phoneNum");
		if (!IdcardUtils.isPhoneNum(phoneNum)) {
			logger.error("phoneNum is illegal");
			return new MsgEntity(ApiMsgConstants.PHONENUM_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String passWord = params.getString("passWord");
		if (StringUtils.isBlank(passWord)) {
			logger.error("password is empty");
			return new MsgEntity(ApiMsgConstants.PASSWORD_EMPTY_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		return params;
	}

	/**
	 * 注册/登录成功后返回的结果
	 * @param customer
	 * @param type
     * @return
     */
	private Map<String,Object> resultMap(Customer customer, Integer type){
		Map<String, Object> map = new HashMap<>();
		String token = "";
		String buyerToken = "";
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("status", ApiMsgConstants.SUCCESS_CODE_SUB);
		// app端 接口
		if(Objects.equals(type, ApiConstants.SELLER_TYPE) ){
            token = StringUtils.produceSellerToken(customer.getId());
            token = redisService.putAccessToken(customer.getId(), token);
            buyerToken = StringUtils.produceBuyerToken(customer.getId());
            buyerToken = redisService.putBuyerToken(customer.getId(), buyerToken);
			map.put("token", token);
			map.put("huanId",StringUtils.getCustomerIdByToken(token));
			map.put("huanid",StringUtils.getCustomerIdByToken(token));
			map.put("buyerToken",buyerToken);
			//添加客户级别
            Shop shop = shopService.get(customer.getId());
            map.put("isVIP",shop.getAdvancedShop() == 1);
		}else { // 网页端接口
            buyerToken = StringUtils.produceBuyerToken(customer.getId());
            buyerToken = redisService.putBuyerToken(customer.getId(), buyerToken);
			map.put("token",buyerToken);
		}
		return map;
	}

    // 以第三方登录时判断此账号是否绑定过手机号
    private Object isLoginBind(String param){
        // 简单验证,保证服务端不会出异常
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(param);
        } catch (Exception e) {
            logger.error("params 出错"+e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if(params==null){
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String code = params.getString("3rdLoginType");
        String uniqueId = params.getString("uniqueId");
        if (StringUtils.isBlank(code)
                || StringUtils.isBlank(uniqueId)) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        // 查询是否绑定customer
        Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        ThirdPartyLogin td = thirdPartyLoginService
                .selectByTypeCodeAndUniqueId(code, uniqueId);
        if (td==null) {
            map.put("status", false);
        } else {
            String token = StringUtils.produceSellerToken(td.getCustomer().getId());
            redisService.putAccessToken(td.getCustomer().getId(), token);
            //生成买家token
            String buyerToken = StringUtils.produceBuyerToken(td.getCustomer().getId());
            buyerToken = redisService.putBuyerToken(td.getCustomer().getId(), buyerToken);
            map.put("status", true);
            map.put("token", token);
            map.put("huanid",StringUtils.getCustomerIdByToken(token));
            map.put("huanId", StringUtils.getCustomerIdByToken(token));
            map.put("buyerToken",buyerToken);
        }
        return map;
    }

    public Object validateJson(String param){
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(param);
            if (params == null) {
                return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                        ApiMsgConstants.FAILED_CODE);
            }
        } catch (Exception e) {
            logger.error("params 出错"+e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        return params;
    }
}
