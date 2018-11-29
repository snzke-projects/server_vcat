package com.vcat.api.web;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.vcat.api.service.*;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.lock.DistLockHelper;
import com.vcat.common.supcan.treelist.cols.Group;
import com.vcat.config.CacheConfig;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.entity.*;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.module.ec.dto.BuyerInfoDto;
import com.vcat.module.ec.dto.ChildCustomerDto;
import com.vcat.module.ec.dto.ProductDto;
import com.vcat.module.ec.dto.ShopBgImageDto;
import com.vcat.module.ec.dto.ShopCollectDto;
import reactor.rx.action.transformation.GroupByAction;

/**
 * 关于小店相关，请求控制器
 * 
 * @author cw 2015年5月9日 10:33:11
 */
@RestController
@ApiVersion({1,2})
public class ShopController extends RestBaseController {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private ShopFundService shopFundService;
	@Autowired
	private ShopProductService shopProductService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ShopBgImageService   shopBgImageService;
	@Autowired
	private ProductService       productService;
	@Autowired
	private FavoritesService     favoritesService;
	@Autowired
	private MessageService       messageService;
	@Autowired 	
	private BrandService         supplierService;
	@Autowired 	
	private BrandService         brandService;
	@Autowired 
	private ShopBrandService     shopBrandService;
	@Autowired
	private InviteEarningService inviteEarningService;
	@Autowired
	private CustomerLogService   customerLogService;

	@Autowired
	private ServerConfigService  cfgService;
	private static Logger logger = Logger.getLogger(ShopController.class);

	/**
	 * 通过token获取小店基本信息
 	 * @param token
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getShopInfo")
	public Object getShopInfo(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
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
		String pushToken = params.getString("pushToken");
		int deviceType = params.getInteger("deviceType");
		String appVersion = params.getString("appVersion");
		if (StringUtils.isEmpty(pushToken)
				|| (deviceType != ApiConstants.DEVICE_TYPE_ANDROID && deviceType != ApiConstants.DEVICE_TYPE_IOS)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String customerId = StringUtils.getCustomerIdByToken(token);
		// 查询用户信息包括小店信息
		Customer cus = customerService.get(customerId);
		if (cus == null) {
			logger.debug("customer is not exsit ");
			return new MsgEntity(ApiMsgConstants.CUSTOMER_NOTEXSIT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		//查询之前的推送id是否被其他账号占用
		Customer oldCus = new Customer();
		oldCus.setPushToken(pushToken);
		Customer newCus = customerService.get(oldCus);
		if(newCus!=null){
//			newCus.setDeviceType(null);
			newCus.setPushToken(null);
			customerService.update(newCus);
		}
		// 修改customer，把pushToken,deviceType更新到数据库
		cus.setDeviceType(deviceType);
		cus.setPushToken(pushToken);
		customerService.update(cus);
		// 构建返回对象
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("shopId", cus.getId());
		res.put("shopNum", cus.getShop().getShopNum());
		res.put("phoneNum", cus.getPhoneNumber());
		res.put("huanId",StringUtils.getCustomerIdByToken(token));
		res.put("levelName", cus.getShop().getLevel().getName());
		// 下面三个如果为空，传空字符串
		res.put("avatarUrl", cus.getAvatarUrl());
		res.put("shopName", cus.getUserName());
		res.put("idCard", cus.getIdCard());
		res.put("isSafePasswd", cus.getSafePass()==null?"0":"1");
		// 下面计算用户的总收入
		cus.getShop().getFund().countTotalFund();
		res.put("totalFund", cus.getShop().getFund().getTotalFund());
		res.put("parentShopName", cus.getShop().getParentName());
		//添加客户级别
		Shop shop = shopService.get(customerId);
		res.put("isVIP", shop.getAdvancedShop() == 1);
        //获取店主的邀请码
        if(shop.getAdvancedShop() == 1){
			res.put("inviteCode",shop.getMyInviteCode().getCode());
        }else res.put("inviteCode","");
		// 返回账户信息
		res.put("bankList", cus.getShop().getAccounts());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("shopInfo", res);
		// 未读消息数量
		map.put("NOT_READ", messageService.getNotReadMsgCount(cus.getId()));
		// 首页背景图片
		map.put("imageList", shopBgImageService.getIndexImageList(ApiConstants.NORMAL));
//		// 查询店铺对应的达人
//		GuruShop guruShop = new GuruShop();
//		Shop shop = new Shop();
//		shop.setId(cus.getId());
//		guruShop.setShop(shop);
//		GuruShop gs = guruShopService.get(guruShop);
//		if (gs != null)
//			map.put("guruType", gs.getGuru().getGuruType() + "");
//		//获取店铺选择品牌的情况
//		String supplierBind = ApiConstants.YES;
//		ShopBrand shopBrand = new ShopBrand();
//		Brand supplier = new Brand();
//		shopBrand.setShop(shop);
//		shopBrand.setBrand(supplier);
//		ShopBrand shopS = shopBrandService.get(shopBrand);
//		if (shopS == null && gs != null && gs.getGuru() != null
//				&& ApiConstants.SUPER.equals(gs.getGuru().getGuruType()+"")) {
//			supplierBind = ApiConstants.NO;
//		}
//		map.put("supplierBind", supplierBind);
		
		//记录日志
		CustomerLog log = new CustomerLog();
		log.preInsert();
		log.setCustomerId(cus.getId());
		log.setDeviceType(deviceType);
		log.setDeviceVersion(appVersion);
		//类型为登录
		log.setType(ApiConstants.LOGIN);
		customerLogService.insert(log);
		return map;
	}

	/**
	 * 买家接口,此接口为买家点击分享页面进入 不需要登录
	 * 
	 * @return
	 */
	@RequestMapping("/anon/getSellerShopInfo")
	public Object getSellerShopInfo(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
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
		String shopId = params.getString("shopId");
		if(StringUtils.isBlank(shopId)){
			shopId = cfgService.findCfgValue("default_shop_id");
		}
		if (StringUtils.isBlank(shopId)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		// 判断用户是否收藏,默认没收藏
		String isCollect = "0";
		// 如果是微信用户，判断用户是否收藏
		String buyerId = StringUtils.getCustomerIdByToken(token);
		if (!StringUtils.isBlank(buyerId)) {
			Favorites fav = new Favorites();
			Customer buyer = new Customer();
			buyer.setId(buyerId);
			Shop shop = new Shop();
			Product product = new Product();
			GroupBuy groupBuy = new GroupBuy();
			fav.setGroupBuy(groupBuy);
			shop.setId(shopId);
			fav.setShop(shop);
			fav.setCustomer(buyer);
			fav.setProduct(product);
			if (favoritesService.get(fav) != null) {
				isCollect = "1";
			}
		}

		// 查询用户信息包括小店信息
		Customer cus = customerService.get(shopId);
		if (cus == null) {
			logger.debug("customer is not exsit ");
			return new MsgEntity(ApiMsgConstants.CUSTOMER_NOTEXSIT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}

		// 构建返回对象
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("shopId", cus.getId());
		res.put("shopNum", cus.getShop().getShopNum());
		res.put("levelName", cus.getShop().getLevel().getName());
		res.put("levelLogo", cus.getShop().getLevel().getUrlPath());
		res.put("avatarUrl", cus.getAvatarUrl());
		res.put("shopName", cus.getUserName());
		res.put("isCollect", isCollect);
		res.put("favoriteCount", cus.getFavoritesCount());
        res.put("isVIP",shopService.get(cus.getId()).getAdvancedShop());
        BigDecimal defaultSalesLimit = new BigDecimal(DictUtils.getDictValue("ec_upgrade_sales_limit",""));
        ShopFund   fund              = shopFundService.getShopFund(shopId);
		BigDecimal nowSales = new BigDecimal(0);
		if(fund != null){
			nowSales = fund.getTotalSale();
		}
        res.put("defaultSalesLimit",defaultSalesLimit);
        res.put("nowSales",nowSales);
		// 获取店铺背景图片连接
		List<ShopBgImageDto> list = shopBgImageService.findBackgroundPicList(cus
				.getId(),ApiConstants.BG_TYPE_BIND);
		res.put("bgPicList", list);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("shopInfo", res);
		return map;
	}

	/**
	 * 绑定银行卡
	 * 
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/bindBank")
	public Object bindBank(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
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

		String customerId = StringUtils.getCustomerIdByToken(token);
		// 查询用户信息包括小店信息
		Customer cus = customerService.get(customerId);
		if (cus == null) {
			logger.debug("customer is not exsit ");
			return new MsgEntity(ApiMsgConstants.CUSTOMER_NOTEXSIT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String identifyCode = params.getString("identifyCode");
		// 验证码是否正确,redis验证
		if (StringUtils.isEmpty(identifyCode)
				|| !identifyCode.equals(redisService.getIdentifyingCode(cus
						.getPhoneNumber()))) {
			logger.debug("identifyCode is incurrent ");
			return new MsgEntity(ApiMsgConstants.IDENTIFYCODE_INCURRENT_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String accountName = params.getString("name");
		String accountNum = params.getString("accountNum");
		String branchName = params.getString("branchName");
		String city = params.getString("city");
		String province = params.getString("province");
		String bankName = params.getString("bankName");
		if(StringUtils.isBlank(bankName)){
			bankName = "支付宝";
		}
		String bankType = params.getString("accountType");
		if (StringUtils.isEmpty(bankName)||StringUtils.isEmpty(bankType) || StringUtils.isEmpty(accountName)
				|| StringUtils.isEmpty(accountNum)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		
		// 如果是绑定银行卡
		if (ApiConstants.PAY_TYPE_BANK.equals(bankType)
				&& (StringUtils.isEmpty(branchName)
						|| StringUtils.isEmpty(city) || StringUtils
							.isEmpty(province))) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		//设置之前的默认账户为不默认
		accountService.updateDefault(customerId);
		// 设置账户类型
		Gateway gateway = new Gateway();
		gateway.setCode(bankType);
		// 判断用户是否已绑定此账户类型
		Account acc = new Account();
		acc.setShopId(customerId);
		acc.setGateWay(gateway);
		acc.setActive(true);
		Account accout = accountService.get(acc);
		// 设置银行卡名字
		Bank bank = new Bank();
		bank.setName(bankName);
		// 如果账户存在 就修改
		if (accout != null) {
			accout.setAccountName(accountName);
			accout.setBranchName(branchName);
			accout.setCity(city);
			accout.setProvince(province);
			accout.setAccountNumber(accountNum);
			accout.setBank(bank);
			accout.setIsDefault(ApiConstants.YES);
			accountService.update(accout);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
			map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
			// 查询用户信息包括小店信息
			Customer newCus = customerService.get(customerId);
			if(newCus!=null){
				map.put("bankList", newCus.getShop().getAccounts());
			}
			return map;
		}
		// 创建银行卡对象
		Account newAccount = new Account();
		// 设置绑定状态为true
		newAccount.preInsert();
		newAccount.setShopId(customerId);
		newAccount.setActive(true);
		newAccount.setAccountName(accountName);
		newAccount.setAccountNumber(accountNum);
		newAccount.setBranchName(branchName);
		newAccount.setCity(city);
		newAccount.setProvince(province);
		newAccount.setBank(bank);
		newAccount.setIsDefault(ApiConstants.YES);
		newAccount.setGateWay(gateway);
		accountService.insert(newAccount);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		// 查询用户信息包括小店信息
		Customer newCus = customerService.get(customerId);
		if(newCus!=null){
			map.put("bankList", newCus.getShop().getAccounts());
		}
		return map;
	}

	/**
	 * 获取商铺滚动图片list
	 * 
	 * @param token
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getBgPicList")
	public Object getShopBgPicList(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "type", defaultValue = "") String type) {
		String shopId = StringUtils.getCustomerIdByToken(token);
		if (StringUtils.isEmpty(type) || (!ApiConstants.BG_TYPE_ALL.equals(type) && !ApiConstants.BG_TYPE_BIND.equals(type))) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		List<ShopBgImageDto> list = shopBgImageService.findBackgroundPicList(shopId,type);
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", list);
		return map;
	}

	/**
	 * 绑定商铺的背景图片
	 * 
	 * @param token
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/bindBgPic")
	public Object bindBgPic(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
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
		String picList = params.getString("picList");
		if (StringUtils.isEmpty(picList)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		List<String> list = Arrays.asList(picList.split("\\,"));
		String shopId = StringUtils.getCustomerIdByToken(token);
		
		if(list.size()>ApiConstants.BG_IMAGE_SIZE){
			return new MsgEntity(ApiMsgConstants.BG_IMAGE_ERROR,
					ApiMsgConstants.FAILED_CODE);
		}
		shopBgImageService.insertList(shopId, list);
		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
				ApiMsgConstants.SUCCESS_CODE);
	}

	/**
	 * 获取店铺的买家信息
	 * rankType为买家排序的类型  - orderCount下单数 / saleFund交易额/ lastBuyDate时间
	 * @param token
	 * @param rankType
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getBuyerList")
	public Object getBuyerList(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "rankType", defaultValue = "") String rankType,
			@RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {
		String shopId = StringUtils.getCustomerIdByToken(token);
		int count = shopService.countBuyerList(shopId);
		// 组装分页信息
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();
		List<BuyerInfoDto> list = shopService.getBuyerList(shopId, rankType,
				page);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", list);
		map.put("page", page);
		return map;
	}

	/**
	 * 收藏商品或者店铺接口
	 * 
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/collect")
	@CacheEvict(value = CacheConfig.GET_VCAT_PRODUCT_DETAIL_CACHE,
			key = "T(com.vcat.common.utils.StringUtils).getCustomerIdByToken(#token) + T(com.vcat.common.utils.StringUtils).getValueFromJson(#param,'productId')", cacheManager = "apiCM")
	public Object collect(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
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
		String shopId = params.getString("shopId");
		String productId = params.getString("productId");
		String groupBuyId = params.getString("groupBuyId");
		// 收藏商品类型，1为普通 2为拿样，目前不传也表示普通,
		String favType = params.getString("productType");
		if (StringUtils.isBlank(favType)) {
			favType = ApiConstants.NORMAL;
		}
		if (StringUtils.isBlank(shopId) && StringUtils.isBlank(productId) && StringUtils.isBlank(groupBuyId)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		// 查询是否绑定customer
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		if (StringUtils.isBlank(productId) && !StringUtils.isBlank(shopId)) {
			// 收藏店铺
			Favorites favorite = new Favorites();
			Customer customer = new Customer();
			customer.setId(StringUtils.getCustomerIdByToken(token));
			favorite.setCustomer(customer);
			Shop shop = new Shop();
			shop.setId(shopId);
			Product product = new Product();
			GroupBuy groupBuy = new GroupBuy();
			favorite.setGroupBuy(groupBuy);
			favorite.setProduct(product);
			favorite.setShop(shop);
			favorite.setFavType(null);
			RLock lock = DistLockHelper.getLock("shopFavoritesServiceLock");
			try {
				lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
				// 如果存在 取消收藏
				if (favoritesService.get(favorite) != null) {
					favoritesService.delete(favorite);
					map.put("isCollect", false);
				} else {
					favorite.preInsert();
					favoritesService.insert(favorite);
					map.put("isCollect", true);
				}
			} finally {
				if (lock.isLocked())
					lock.unlock();
			}
		} else if (!StringUtils.isBlank(productId)) {
			// 收藏商品
			Favorites favorite = new Favorites();
			Customer customer = new Customer();
			customer.setId(StringUtils.getCustomerIdByToken(token));
			favorite.setCustomer(customer);
			Product product = new Product();
			product.setId(productId);
			GroupBuy groupBuy = new GroupBuy();
			if(favType.equals(ApiConstants.GROUPBUY_SELLER) || ApiConstants.GROUPBUY_BUYER.equals(favType)){
				groupBuy.setId(groupBuyId);
			}
			Shop shop = new Shop();
			if(ApiConstants.NORMAL.equals(favType) || ApiConstants.GROUPBUY_BUYER.equals(favType)){
				shop.setId(shopId);
			}
			favorite.setShop(shop);
			favorite.setProduct(product);
			favorite.setFavType(favType);
			favorite.setGroupBuy(groupBuy);
			RLock lock = DistLockHelper.getLock("productFavoritesServiceLock");
			try {
				lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
				if (favoritesService.get(favorite) != null) {
					favoritesService.deleteProduct(favorite);
					map.put("isCollect", false);
				} else {
					favorite.preInsert();
					favorite.setFavType(favType);
					favoritesService.saveProduct(favorite);
					map.put("isCollect", true);
				}
			} finally {
				if (lock.isLocked())
					lock.unlock();
			}
		}
		return map;
	}

	/**
	 * 获取收藏的店铺或者商品id
	 * 
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/getCollectList")
	public Object getCollectList(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
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
		String collectType = params.getString("collectType");
		int pageNo = params.getInteger("pageNo") == null ? 1 : params
				.getIntValue("pageNo");
		if (StringUtils.isBlank(buyerId) || StringUtils.isBlank(collectType)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		// 获取我收藏的店铺
		if (ApiConstants.COLLECT_TYPE_SHOP.equals(collectType)) {
			int count = favoritesService.countShopCollect(buyerId);
			// 组装分页信息
			Pager page = new Pager();
			page.setPageNo(pageNo);
			page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
			page.setRowCount(count);
			page.doPage();
			List<ShopCollectDto> list = favoritesService.getShopCollect(
					buyerId, page);
			map.put("shopList", list);
			map.put("page", page);
		}
		// 获取我收藏的商品
		if (ApiConstants.COLLECT_TYPE_PRODUCT.equals(collectType)) {
			int count = favoritesService.countProductCollect(buyerId);
			// 组装分页信息
			Pager page = new Pager();
			page.setPageNo(pageNo);
			page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
			page.setRowCount(count);
			page.doPage();
			List<ProductDto> list = favoritesService.getProductCollect(buyerId,
					page);
			map.put("productList", list);
			map.put("page", page);
		}
		return map;
	}

//	/**
//	 * 获取达人类型
//	 * 
//	 * @param token
//	 * @param shopId
//	 * @return
//	 */
//	@RequiresRoles("buyer")
//	@RequestMapping("/api/getGuruType")
//	public Object getGuruType(
//			@RequestHeader(value = "token", defaultValue = "") String token,
//			@RequestParam(value = "shopId", defaultValue = "") String shopId) {
//		// 查询店铺对应的达人
//		GuruShop guruShop = new GuruShop();
//		Shop shop = new Shop();
//		shop.setId(shopId);
//		guruShop.setShop(shop);
//		GuruShop gs = guruShopService.get(guruShop);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		if (gs != null)
//			map.put("guru", gs.getGuru());
//		return map;
//	}
//	/**
//	 * 获取达人列表
//	 * @param token
//	 * @return
//	 */
//	@RequiresRoles("seller")
//	@RequestMapping("/api/getGuruList")
//	public Object getGuruList(@RequestHeader(value = "token", defaultValue = "") String token){
//		String shopId = StringUtils.getCustomerIdByToken(token);
//		List<Guru> ruru = guruService.findGuruList(shopId);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		map.put("list", ruru);
//		return map;
//	}
//	
//	/**
//	 * 获取店铺对应的品牌列表
//	 * @param token
//	 * @return
//	 */
//	@RequiresRoles("seller")
//	@RequestMapping("/api/getSupplierList")
//	public Object getBrandList(@RequestHeader(value = "token", defaultValue = "") String token,
//			@RequestParam(value = "guruId", defaultValue = "") String guruId){
//	
//		List<Map<String,Object>> sup = new ArrayList<Map<String,Object>>();
//		String shopId = StringUtils.getCustomerIdByToken(token);
//		//如果不传guruId,使用数据库guruId
//		if(StringUtils.isBlank(guruId)){
//			// 获取店铺绑定的达人
//			GuruShop guruShop = new GuruShop();
//			Shop shop = new Shop();
//			shop.setId(shopId);
//			guruShop.setShop(shop);
//			GuruShop gs = guruShopService.get(guruShop);
//			if(gs!=null){
//				sup = brandService.getBrandList(shopId,gs.getGuru().getId());
//			}
//		}
//		else{
//			sup = brandService.getBrandList(shopId,guruId);
//		}
//		if(sup!=null&&sup.size()>0)
//		for(Map<String,Object> supplier:sup){
//			supplier.put("logoImgPath", QCloudUtils.createOriginalDownloadUrl((String)supplier.get("logoImg")));
//		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		map.put("list", sup);
//		return map;
//	}
//	@RequiresRoles("seller")
//	@RequestMapping("/api/bindGuru")
//	public Object bindGuru(@RequestHeader(value = "token", defaultValue = "") String token,
//			@RequestParam(value = "guruId", defaultValue = "") String guruId){
//		if(StringUtils.isBlank(guruId)){
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		String shopId = StringUtils.getCustomerIdByToken(token);
//		GuruShop guruShop = new GuruShop();
//		Shop shop = new Shop();
//		shop.setId(shopId);
//		guruShop.setShop(shop);
//		GuruShop gs = guruShopService.get(guruShop);
//		if(gs!=null){
//			return new MsgEntity(ApiMsgConstants.BIND_GURU,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		guruShop.preInsert();
//		Guru guru = new Guru();
//		guru.setId(guruId);
//		guruShop.setGuru(guru);
//		guruShopService.insert(guruShop);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		return map;
//	}
//	/**
//	 * 修改达人
//	 * @param token
//	 * @param guruId
//	 * @return
//	 */
//	@RequiresRoles("seller")
//	@RequestMapping("/api/modifyGuru")
//	public Object modifyGuru(@RequestHeader(value = "token", defaultValue = "") String token,
//			@RequestParam(value = "guruId", defaultValue = "") String guruId){
//		if(StringUtils.isBlank(guruId)){
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		String shopId = StringUtils.getCustomerIdByToken(token);
//		GuruShop guruShop = new GuruShop();
//		Shop shop = new Shop();
//		shop.setId(shopId);
//		guruShop.setShop(shop);
//		GuruShop gs = guruShopService.get(guruShop);
//		//如果之前没有绑定达人，绑定达人
//		if(gs==null){
//			guruShop.preInsert();
//			Guru guru = new Guru();
//			guru.setId(guruId);
//			guruShop.setGuru(guru);
//			guruShopService.insert(guruShop);
//		}
//		//更改的达人和之前的不一致，修改达人
//		else if(gs!=null&&!guruId.equals(gs.getGuru().getId())){
//			//更新达人id
//			Guru guru = new Guru();
//			guru.setId(guruId);
//			guruShop.setGuru(guru);
//			guruShopService.batchUpdate(guruShop,null,gs.getGuru().getGuruType(),null);
//		}
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		return map;
//	}
//	/**
//	 * 修改为服饰达人和品牌
//	 * @param token
//	 * @param guruId
//	 * @return
//	 */
//	@RequiresRoles("seller")
//	@RequestMapping("/api/modifyGuruAndBrand")
//	public Object modifyGuruAndBrand(@RequestHeader(value = "token", defaultValue = "") String token,
//			@RequestParam(value = "params", defaultValue = "") String param){
//		// 简单验证,保证服务端不会出异常
//		JSONObject params = null;
//		try {
//			params = JSONObject.parseObject(param);
//		} catch (Exception e) {
//			logger.error("params 出错"+e.getMessage());
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		if (params == null) {
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		String shopId = StringUtils.getCustomerIdByToken(token);
//		String guruId = params.getString("guruId");
//		String supplierIds = params.getString("supplierIds");
//		if (StringUtils.isEmpty(supplierIds)||StringUtils.isEmpty(supplierIds)) {
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		List<String> list = new ArrayList<String>(Arrays.asList(supplierIds
//				.split("\\,")));
//		if (list == null || list.size() > 5) {
//			return new MsgEntity("您最多能选择5个品牌", ApiMsgConstants.FAILED_CODE);
//		}
//		//获取新达人
//		Guru newGuru = guruService.get(guruId);
//		if(newGuru==null){
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		GuruShop guruShop = new GuruShop();
//		Shop shop = new Shop();
//		shop.setId(shopId);
//		guruShop.setShop(shop);
//		GuruShop gs = guruShopService.get(guruShop);
//		//如果之前没有绑定达人，绑定达人
//		if(gs==null){
//			guruShop.preInsert();
//			Guru guru = new Guru();
//			guru.setId(guruId);
//			guruShop.setGuru(guru);
//			guruShopService.insert(guruShop);
//		}
//		//更改的达人和之前的不一致，修改达人
//		else if(gs!=null&&!guruId.equals(gs.getGuru().getId())){
//			//更新达人id
//			Guru guru = new Guru();
//			guru.setId(guruId);
//			guruShop.setGuru(guru);
//			guruShopService.batchUpdate(guruShop,newGuru.getGuruType(),gs.getGuru().getGuruType(),list);
//		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		return map;
//	}
//	/**
//	 * 绑定品牌
//	 * @param token
//	 * @param param
//	 * @return
//	 */
//	@RequiresRoles("seller")
//	@RequestMapping("/api/bindSupplier")
//	public Object bindBrand(@RequestHeader(value = "token", defaultValue = "") String token,
//			@RequestParam(value = "params", defaultValue = "") String param){
//		// 简单验证,保证服务端不会出异常
//		JSONObject params = null;
//		try {
//			params = JSONObject.parseObject(param);
//		} catch (Exception e) {
//			logger.error("params 出错"+e.getMessage());
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		if (params == null) {
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		String shopId = StringUtils.getCustomerIdByToken(token);
//		String supplierIds = params.getString("supplierIds");
//		if(StringUtils.isEmpty(supplierIds)){
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		List<String> list = new ArrayList<String>(Arrays.asList(supplierIds.split("\\,")));
//		if(list==null||list.size()>5){
//			return new MsgEntity("您最多能选择5个品牌",
//					ApiMsgConstants.FAILED_CODE);
//		}
//		// 获取店铺绑定的达人
//		GuruShop guruShop = new GuruShop();
//		Shop shop = new Shop();
//		shop.setId(shopId);
//		guruShop.setShop(shop);
//		GuruShop gs = guruShopService.get(guruShop);
//		if(gs==null){
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		String guruId = gs.getGuru().getId();
//		shopBrandService.bathSave(shopId,guruId,list);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		return map;
//	}
	/**
	 * 根据店铺号获取上级店铺信息
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping("/anon/getParentShop")
	public Object getParentShop(@RequestHeader(value = "token", defaultValue = "") String token,
								@RequestParam(value = "shopNum", defaultValue = "") String shopNum,
								@RequestParam(value = "shopId", defaultValue = "") String shopId){

		if(StringUtils.isBlank(shopNum)){
			return new MsgEntity(ApiMsgConstants.PARENT_SHOP_NOT_FIND,
					ApiMsgConstants.FAILED_CODE);
		}
		shopNum = shopNum.replaceAll("V", "");
		Shop parentShop = new Shop();
		Level level = new Level();
		parentShop.setLevel(level);
		parentShop.setShopNum(shopNum);
		//判断shopNum是否有对应的店铺
		Shop pShop = shopService.get(parentShop);
		if(pShop==null||shopId.equals(pShop.getId())||shopId.equals(pShop.getParentId())){
			return new MsgEntity(ApiMsgConstants.PARENT_SHOP_NOT_FIND,
					ApiMsgConstants.FAILED_CODE);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("parentShopId", pShop.getId());
		map.put("parentShopName", pShop.getName());
		return map;
	}
	/**
	 * 添加上家邀请码
	 * @param token
	 * @param
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/addParentShop")
	public Object addParentShop(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "parentShopId", defaultValue = "") String parentShopId){
	
		String shopId = StringUtils.getCustomerIdByToken(token);
		Shop shop = shopService.get(shopId);
		if(shop.getParentId()!=null){
			return new MsgEntity(ApiMsgConstants.PARENT_SHOP_BIND,
					ApiMsgConstants.FAILED_CODE);
		}
		shop.setParentId(parentShopId);
		shopService.updateParant(shop);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		return map;
	}
//	/**
//	 * 卖家获取商品类别和品牌
//	 * @param token
//	 * @param guruId
//	 * @return
//	 */
//	@RequiresRoles("seller")
//	@RequestMapping("/api/getSupplierListAndCategoryList")
//	public Object getBrandListAndCategoryList(@RequestHeader(value = "token", defaultValue = "") String token,
//			@RequestParam(value = "guruId", defaultValue = "") String guruId){
//		if(StringUtils.isBlank(guruId)){
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		GuruCategory gc = new GuruCategory();
//		Guru guru = new Guru();
//		guru.setId(guruId);
//		gc.setGuru(guru);
//		GuruCategory guruCategory = guruCategoryService.get(gc);
//		if(guruCategory!=null){
//			guruCategory.getCategory().setChilds(guruCategory.getChilds());
//			map.put("category", guruCategory.getCategory());
//		}
//		List<Map<String,Object>> sup = null;
//		String shopId = StringUtils.getCustomerIdByToken(token);
//		// 获取店铺绑定的达人
//		GuruShop guruShop = new GuruShop();
//		Shop shop = new Shop();
//		shop.setId(shopId);
//		guruShop.setShop(shop);
//		GuruShop gs = guruShopService.get(guruShop);
//		if(gs!=null){
//			sup = brandService.getBrandList(shopId,gs.getGuru().getId());
//		}
//		if(sup!=null&&sup.size()>0)
//		for(Map<String,Object> supplier:sup){
//			supplier.put("logoImgPath", QCloudUtils.createOriginalDownloadUrl((String)supplier.get("logoImg")));
//		}
//		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		map.put("list", sup);
//		return map;
//	}
	/**
	 * 获取我的战队成员
	 * @param token
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getChilds")
	public Object getChilds(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "pageNo", defaultValue = "1") int pageNo){
		
		String shopId = StringUtils.getCustomerIdByToken(token);
		int count = shopService.countChildsList(shopId);
		// 组装分页信息
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();
		List<ChildCustomerDto> list = shopService.getChildList(shopId,page);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", list);
		map.put("page", page);
		return map;
	}
}
