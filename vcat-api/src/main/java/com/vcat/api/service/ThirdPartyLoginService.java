package com.vcat.api.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.vcat.common.thirdparty.WeixinClient;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.constant.ApiConstants;
import com.vcat.common.lock.DistLockHelper;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.security.EntryptUtils;
import com.vcat.common.service.CrudService;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.dao.ThirdPartyLoginDao;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.ThirdLoginType;
import com.vcat.module.ec.entity.ThirdPartyLogin;

@Service
@Transactional(readOnly = true)
public class ThirdPartyLoginService extends CrudService<ThirdPartyLogin> {
	@Autowired
	private RedisService redisService;
	@Autowired
	private ThirdPartyLoginDao thirdPartyLoginDao;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ThirdLoginTypeService thirdLoginTypeService;
	@Autowired
	private ShopService shopService;
	@Override
	protected CrudDao<ThirdPartyLogin> getDao() {
		return thirdPartyLoginDao;
	}


	public ThirdPartyLogin selectByTypeCodeAndUniqueId(String code, String uniqueId) {
		Map<String, Object> map = new HashMap<>();
		map.put("code", code);
		map.put("uniqueId", uniqueId);
		return thirdPartyLoginDao.selectByTypeCodeAndUniqueId(map);
	}

	/**
	 * 用户没有被邀请过,直接通过第三方登录绑定手机号
	 * @param thirdLogin
	 * @param parentShopId
     */
	@Transactional(readOnly = false)
	public void bind3rdLogin(ThirdPartyLogin thirdLogin, String parentShopId) {
		customerService.saveCustomer(thirdLogin.getCustomer(),shopService.get(parentShopId));
		thirdPartyLoginDao.insert(thirdLogin);
	}
	@Transactional(readOnly = false)
	public void bind3rdLoginBuyer(ThirdPartyLogin thirdLogin) {
		customerService.saveCustomer(thirdLogin.getCustomer());
		thirdPartyLoginDao.insert(thirdLogin);
	}
	
	// 微信用户自动登录自动注册自动登录
	@Transactional(readOnly = false)
	public String auto(String code, String openId, String accessToken) {
		// 注册、登录、生成token
		String token = "";
		if(StringUtils.isBlank(openId)){
			return "";
		}

		String userInfo = WeixinClient.getUserInfo(accessToken, openId);
		logger.info("WeixinClient.getUserInfo=" + userInfo);
		JSONObject params = JSONObject.parseObject(userInfo);
		String userName = null;
		String headImg = null;
		if(params != null ){
			userName = params.getString("nickname");
			headImg = params.getString("headimgurl");
		}

		if(userName ==null){
			userName = "VWX" + IdGen.getRandomNumber(13);
		}

		RLock lock = DistLockHelper.getLock("selectByTypeCodeAndUniqueIdLock");
		try {
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
			ThirdPartyLogin td = selectByTypeCodeAndUniqueId(
					ApiConstants.SHARE_TYPE_WX, openId);
			if (td == null) {
				// 注册用户，并绑定
				// 获取三方登录类型
				ThirdLoginType thirdLoginType = new ThirdLoginType();
				thirdLoginType.setCode(ApiConstants.SHARE_TYPE_WX);
				thirdLoginType = thirdLoginTypeService.get(thirdLoginType);
				// 创建用户
				Customer newCustomer = new Customer();
				newCustomer.preInsert();
				newCustomer.setAvatarUrl(headImg);
				newCustomer.setRegistered(ApiConstants.WX_REGISTER);
				newCustomer.setPhoneNumber(IdGen.getRandomNumber(13));
				newCustomer.setUserName(userName);
				newCustomer.setPassword(EntryptUtils.entryptPassword(IdGen
						.getRandomNumber(20)));
				// 创建用户三方登录关系
				ThirdPartyLogin thirdLogin = new ThirdPartyLogin();
				thirdLogin.preInsert();
				thirdLogin.setCustomer(newCustomer);
				thirdLogin.setUniqueId(openId);
				thirdLogin.setThirdLoginType(thirdLoginType);
				bind3rdLoginBuyer(thirdLogin);
				token = StringUtils.produceBuyerToken(newCustomer.getId());
				token = redisService.putBuyerToken(newCustomer.getId(), token);
			} else {
				Customer customer = td.getCustomer();
				logger.debug("customer.getUserName="+customer.getUserName());
				logger.debug("customer.getAvatarUrl="+customer.getAvatarUrl());
				boolean update = false;
				if(headImg!=null && !headImg.equalsIgnoreCase(customer.getAvatarUrl())) {
					logger.debug("call customer.setAvatarUrl");
					customer.setAvatarUrl(headImg);
					update=true;
				}
				if(!userName.startsWith("VWX") &&
						!userName.equalsIgnoreCase(customer.getUserName())) {
					logger.debug("call customer.setUserName");
					customer.setUserName(userName);
					update =true;
				}
				if(update) {
					logger.debug("update customer");
					customerService.update(customer);
				}
				token = StringUtils.produceBuyerToken(customer.getId());
				token = redisService.putBuyerToken(customer.getId(), token);
			}
		} finally {
			if (lock.isLocked())
				lock.unlock();
		}
		return token;
	}

	public ThirdPartyLogin selectByTypeCodeAndCustomerId(String shareTypeWx,
			String customerId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", shareTypeWx);
		map.put("customerId", customerId);
		return thirdPartyLoginDao.selectByTypeCodeAndCustomerId(map);
	}

    /**
     * 用户曾经被邀请注册过,通过第三方登录时绑定手机号
     * @param cus
     * @param thirdLogin
     * @param parentShopId
     * @return
     */
	@Transactional(readOnly = false)
	public Customer inviteBind3rdLogin(Customer cus, ThirdPartyLogin thirdLogin, String parentShopId) {
		customerService.inviteCustomer(cus,shopService.get(parentShopId));
		thirdPartyLoginDao.insert(thirdLogin);
		return cus;
	}
}
