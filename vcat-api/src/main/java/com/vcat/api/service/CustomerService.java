package com.vcat.api.service;

import com.easemob.server.common.Constants;
import com.easemob.server.httpclient.api.EasemobIMUsers;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vcat.api.service.mq.RegisterMessageSendReceive;
import com.vcat.api.web.ChatMessageHelper;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.lock.DistLockHelper;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.dao.CustomerDao;
import com.vcat.module.ec.dao.MessageRegisterDao;
import com.vcat.module.ec.entity.*;

import org.apache.log4j.Logger;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class CustomerService extends CrudService<Customer> {
	@Autowired
	private CustomerDao         customerDao;
	@Autowired
	private MessageRegisterDao  messageRegisterDao;
	@Autowired
	private CustomerRoleService customerRoleService;
	@Autowired
	private LevelService        levelService;
	@Autowired
	private ShopService         shopService;
	@Autowired
	private EcRoleService       roleService;
	@Autowired
	private ShopFundService     shopFundService;
	@Autowired
	private MessageService      messageService;
	@Autowired
	private ChatService chatService;
	@Autowired
	private RegisterMessageSendReceive registerMessageSendReceive;

	@Override
	protected CrudDao<Customer> getDao() {
		return customerDao;
	}

	private static Logger LOG = Logger.getLogger(CustomerService.class);
	/**
	 * 邀请好友注册(页面注册),注册方式2,is_registered=2,
	 * password为6位随机数字,必须在客户端再注册一次
	 *
	 * @param phoneNum   新注册用户电话号码
	 * @param parentShop 上家店铺 不为空
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> inviteRegistration(String phoneNum, Shop parentShop) {
		Map<String, Object> map  = new HashMap<>();
		RLock               lock = DistLockHelper.getLock("inviteRegistration");
		try {
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
			Customer customer = new Customer();
			customer.setPhoneNumber(phoneNum);
			// 根据电话号码查询状态为0,1,2,3的用户信息
			//customer.setRegistered(ApiConstants.ALL_REGISTER);
			//如果不存在此电话
			Customer isExsitCustomer = this.get(customer);
			if(isExsitCustomer == null){
				customer.preInsert();
				customer.setRegistered(ApiConstants.INVITE_REGISTER);
				customer.setPassword(IdGen.getRandomNumber(6));
				// 存入数据库
				this.insertCustomer(customer, parentShop);
			}
			// 1.当此用户已经在app端注册过时
			else if (!isExsitCustomer.getRegistered().equals(ApiConstants.INVITE_REGISTER)) {
				logger.debug("customer is exsits ");
				map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
				map.put(ApiConstants.MSG, ApiMsgConstants.CUSTOMER_EXSIT_MSG);
				map.put(ApiConstants.STATUS, ApiMsgConstants.SUCCESS_CODE_CUSTOMER_EXSIT);
				return map;
			}
			// 2.此用户以前被邀请过
			else if(isExsitCustomer.getRegistered().equals(ApiConstants.INVITE_REGISTER)){
				//得到当前客户的上家店铺,修改上家店铺为最新店铺
				Shop shop = shopService.get(isExsitCustomer.getId());
				shop.setParentId(parentShop.getId());
				shop.setUsedInviteCodeId(parentShop.getMyInviteCodeId());
				shopService.update(shop);
			}
		} finally {
			if (lock.isLocked()) {
				lock.unlock();
			}
		}
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put(ApiConstants.STATUS, ApiMsgConstants.SUCCESS_CODE_SUB);
		return map;
	}

	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 生成卖家账号
	 *
	 * @param customer   卖家对象 保存了ID,电话,密码,注册类型是1
	 * @param parentShop 上家店铺
	 */
	@Transactional(readOnly = false)
	public void saveCustomer(Customer customer, Shop parentShop) {
		//同步获取不重复的店铺号
		int shopCount = this.getShopNum();
		if (StringUtils.isBlank(customer.getUserName())) {
			customer.setUserName("V"+shopCount);
		}
		customerDao.insert(customer);
		// 创建店铺
		this.createShop(customer, parentShop,shopCount);
		// 卖家角色
		this.createRole(customer, ApiConstants.SELLER);
		// 买家角色
		this.createRole(customer, ApiConstants.BUYER);
		//添加环信账号
		this.createNewIMUser(customer.getId());
		// 设置注册消息任务
		this.registerMsgJob(customer);
		// 加好友
		//EasemobIMUsers.addFriendSingle(customer.getId(), parentShop.getId());
		// 透传
		ChatMessageHelper.sendJoinedTeamMessage(parentShop.getId(), chatService.getMemberInfo(customer.getId()));
	}

	/**
	 * 生成买家账号
	 *
	 * @param customer
	 */
	@Transactional(readOnly = false)
	public void saveCustomer(Customer customer) {
		//设置默认名字(电话号码)
		if(StringUtils.isNullBlank(customer.getUserName())) {
			customer.setUserName(customer.getPhoneNumber());
		}
		customerDao.insert(customer);
		//新建角色 ，默认为买家
		this.createRole(customer, ApiConstants.BUYER);
		// 默认没有店铺 当第一次使用app登录的时候创建店铺
	}


	/**
	 * 买家第一次登陆 app
	 * @param cus
	 */
	@Transactional(readOnly = false)
	public Customer saveRoleSeller(Customer cus,Shop parentShop) {
		cus = customerDao.get(cus);
		//创建卖家角色
		this.createRole(cus,ApiConstants.SELLER);
		//修改默认名称
		int shopCount = getShopNum();
		cus.setUserName("V"+shopCount);
		customerDao.update(cus);
		this.registerMsgJob(cus);
		// 创建环信账号
		this.createNewIMUser(cus.getId());
		// 创建店铺
		this.createShop(cus, parentShop ,shopCount);
		// 加好友
		EasemobIMUsers.addFriendSingle(cus.getId(), parentShop.getId());
		// 透传
		ChatMessageHelper.sendJoinedTeamMessage(parentShop.getId(), chatService.getMemberInfo(cus.getId()));
		return cus;
	}

	/**
	 * 向数据库中插入新用户注册信息 邀请注册 默认无角色,有店铺信息
	 *
	 * @param customer   被邀请注册客户对象
	 * @param parentShop 上家店铺
	 */
	@Transactional(readOnly = false)
	private void insertCustomer(Customer customer, Shop parentShop) {
		//设置默认名字
		//同步获取不重复的店铺号
		int shopCount = getShopNum();
		if (StringUtils.isBlank(customer.getUserName())) {
			customer.setUserName("V"+shopCount);
		}
		customerDao.insert(customer);
		//创建店铺
		this.createShop(customer, parentShop, shopCount);
	}


	/**
	 * 卖家如果之前被邀请注册过,添加卖家+买家角色
	 * 更新卖家信息(注册状态2->1,更新密码)
	 * 更新上家店铺信息(如果用户填写了其他邀请码)
	 * @param customer
	 * @param parentShop 上家店铺
	 * @return
	 */
	@Transactional(readOnly = false)
	public Customer inviteCustomer(Customer customer, Shop parentShop) {
		// 更新密码,注册状态
		customer.setUserName(shopService.get(customer.getId()).getName());
		customerDao.update(customer);
		// 卖家角色
		this.createRole(customer, ApiConstants.SELLER);
		// 买家角色
		this.createRole(customer, ApiConstants.BUYER);
		//添加环信账号
		this.createNewIMUser(customer.getId());
		//更新该店铺的上家店铺
		Shop shop = shopService.get(customer.getId());
		shop.setUsedInviteCodeId(parentShop.getMyInviteCode().getId());
		shop.setParentId(parentShop.getId());
		shopService.update(shop);
		//EasemobIMUsers.addFriendSingle(shop.getId(), shop.getParentId());
		this.registerMsgJob(customer);
		return customer;
	}

	/**
	 * 买家如果之前被邀请注册过
	 * 默认买家角色
	 * 默认无店铺
	 * 第一次登录时创建店铺
	 *
	 * @param customer
	 * @return
	 */
	@Transactional(readOnly = false)
	public Customer inviteCustomer(Customer customer) {
		customer.setUserName(shopService.get(customer.getId()).getName());
		customerDao.update(customer);
		// 添加买家角色
		this.createRole(customer, ApiConstants.BUYER);
		return customer;
	}

	/**
	 * 绑定手机号时获取不重复的用户名
	 *
	 * @param userName
	 * @return
	 */
	public String getUserName(String userName) {
		Customer customer = new Customer();
		customer.setUserName(userName);
		Customer cus = customerDao.get(customer);
		if (cus != null) {
			userName = getUserName(userName + IdGen.getRandomNumber(3));
		} else {
			return userName;
		}
		return userName;
	}

	/**
	 * 注册后添加消息任务
	 *
	 * @param customer
	 */
	private void registerMsgJob(Customer customer) {
		// 设置注册消息任务
		try {
			if (customer.getRegistered() == ApiConstants.WX_REGISTER) {
				LOG.warn("用户[" + customer.getUserName() + "]使用微信注册，跳过注册消息任务");
				return;
			}
			List<MessageRegister> list = messageRegisterDao.findMessageList();
			if (null != list && list.size() > 0) {
				registerMessageSendReceive.addRegisterMessageJob(customer.getId(), list);
			}
		} catch (Exception e) {
			LOG.error("设置注册消息任务失败：" + e.getMessage());
		}
	}

	/**
	 * 创建环信账号
	 *
	 * @param cid
	 */
	private void createNewIMUser(String cid) {
		ObjectNode datanode = JsonNodeFactory.instance.objectNode();
		datanode.put("username", cid);
		datanode.put("password", Constants.DEFAULT_PASSWORD);
		ObjectNode createNewIMUserSingleNode = EasemobIMUsers.createNewIMUserSingle(datanode);
		if (null != createNewIMUserSingleNode) {
			logger.debug("注册IM用户[单个]: " + createNewIMUserSingleNode.toString());
		}
	}

	/**
	 * 创建店铺
	 * @param customer
	 * @param parentShop 上家店铺
	 */
	@Transactional(readOnly = false)
	public Shop createShop(Customer customer, Shop parentShop, int shopCount) {
		Level level = new Level();
		level.setName("V1");
		level = levelService.get(level);

		Shop shop = new Shop();
		shop.setId(customer.getId());
		shop.setShopNum(shopCount + "");
		shop.setLevel(level);
		shop.setName(customer.getUserName());
		//设置邀请时的邀请码
		shop.setParentId(parentShop.getId());
		shop.setUsedInviteCodeId(parentShop.getMyInviteCode().getId());
		shop.setAutoOff(Integer.parseInt(DictUtils.getDictValue("ec_auto_close_order_day_limit", "7")));
		shopService.insert(shop);
		//新建店铺账户
		ShopFund fund = new ShopFund(new BigDecimal(0));
		fund.setId(customer.getId());
		shopFundService.insert(fund);
		shopService.insertBgImages(customer.getId());
		// 创建环信关系
		EasemobIMUsers.addFriendSingle(shop.getId(), shop.getParentId());
		// 向上家店铺发送个人消息
		messageService.pushRemindMessage(parentShop.getId(),ApiConstants.NEW_JOIN_MEMBER,"新成员小店名:" + shop.getName() + "\r\n注册手机号：" + customer.getPhoneNumber());
		return shop;
	}

	/**
	 * 创建角色类型
	 * @param customer
	 */
	@Transactional(readOnly = false)
	public void createRole(Customer customer, String roleType) {
		CustomerRole customerRole = new CustomerRole();
		customerRole.preInsert();
		customerRole.setCustomer(customer);
		EcRole ecRole = new EcRole();
		ecRole.setRoleName(roleType);
		ecRole = roleService.get(ecRole);
		customerRole.setRole(ecRole);
		customerRoleService.insert(customerRole);
	}

	public int getShopNum(){
		RLock lock      = DistLockHelper.getLock("getShopCount");
		int   shopCount = 0;
		try {
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
			int count = shopService.count();
			shopCount = shopService.getShopCount(count + 1);
		} finally {
			if (lock.isLocked())
				lock.unlock();
		}
		return shopCount;
	}
}
