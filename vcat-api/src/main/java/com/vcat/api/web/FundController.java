package com.vcat.api.web;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.lock.DistLockHelper;
import com.vcat.module.core.entity.MsgEntity;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.vcat.api.service.CouponFundService;
import com.vcat.api.service.CouponShopService;
import com.vcat.api.service.CustomerService;
import com.vcat.api.service.RedisService;
import com.vcat.api.service.ShopFundService;
import com.vcat.api.service.ShopService;
import com.vcat.api.service.WithdrawalService;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.IdcardUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.module.ec.dto.FundDetailBillDto;
import com.vcat.module.ec.dto.WithdrawDto;
import com.vcat.module.ec.entity.CouponFund;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.Rank;
import com.vcat.module.ec.entity.Shop;
import com.vcat.module.ec.entity.ShopFund;
import com.vcat.module.ec.entity.Withdrawal;

/**
 * 关于小店相关，请求控制器
 * 
 * @author cw 2015年5月9日 10:33:11
 */
@RestController
@ApiVersion({1,2})
public class FundController extends RestBaseController {

	@Autowired
	private CustomerService customerService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private ShopFundService shopFundService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private CouponFundService couponFundService;
	@Autowired
	private CouponShopService couponShopService;
	@Autowired
	private WithdrawalService withdrawalService;

	private static Logger logger = Logger.getLogger(FundController.class);

	/**
	 * 获取店铺资金
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getShopFund")
	public Object getShopFund(
			@RequestHeader(value = "token", defaultValue = "") String token) {
		String shopId = StringUtils.getCustomerIdByToken(token);
		ShopFund fund = shopFundService.getShopFund(shopId);
		// 构建返回对象
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		if (fund == null) {
			map.put("fundInfo", new ShopFund(new BigDecimal(0)));
			return map;
		}
		// 计算总收入、本月总收入
		if (fund.getTotalSale() == null) {
			fund.setTotalSale(new BigDecimal(0));
		}
		if (fund.getTotalLoadAward() == null) {
			fund.setTotalLoadAward(new BigDecimal(0));
		}
		//计算总收入
		fund.countTotalFund();
		//月总收入
		fund.countMonthTotalFund();
		//计算总可用收入
		fund.countTotalAvailableFund();
		//计算总已提现收入
		fund.countTotalUseDFund();
		//计算总提现中收入
		fund.countTotalProcessingFund();
		//获取购物卷
		Shop shop = new Shop();
		shop.setId(shopId);
		CouponFund cf = new CouponFund();
		cf.setShop(shop);
		CouponFund couponFund  = couponFundService.get(cf);
		if (couponFund != null) {
			fund.setCouponUsedFund(couponFund.getUsedFund());
			fund.setCouponAvailableFund(couponFund.getAvailableFund());
			fund.setCouponTotalFund(couponFund.getUsedFund().add(
					couponFund.getAvailableFund()));
		}
		map.put("fundInfo", fund);
		return map;
	}

	/**
	 * 获取卖家收入排行列表
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getFundRank")
	public Object getFundTopList(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "rankType", defaultValue = "") String rankType,
			@RequestParam(value = "pageNo", defaultValue = "") int pageNo) {
		if (rankType == null) {
			logger.debug("rankType is null or empty ");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String shopId = StringUtils.getCustomerIdByToken(token);
		// 组装分页信息
		Integer count = shopFundService.count(rankType);
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(15);
		page.setRowCount(count==null?0:count);
		page.doPage();
		// 获取店铺收入排行
		List<Rank> shopList = shopService.getFundRankList(page, rankType);
		
		//获取我的名次
		String myMonthRank = shopFundService.getMyMonthRank(shopId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", shopList);
		map.put("page", page);
		map.put("myMonthRank", myMonthRank);
		return map;
	}

	/**
	 * 获取收入账单接口，包括总账单和销售账单、分红账单
	 * @param token
	 * @param month
	 * @param billType
	 * @param pageNo
	 * @return
	 */
	@ApiVersion(1)
	@RequiresRoles("seller")
	@RequestMapping("/api/getDetailBill")
	public Object getDetailBill(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "billType", defaultValue = "") String billType,
			@RequestParam(value = "pageNo", defaultValue = "") int pageNo) {
		if (!IdcardUtils.isMonth(month) || StringUtils.isEmpty(billType)) {
			logger.debug("request params is illegal ");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String shopId = StringUtils.getCustomerIdByToken(token);
		// 查询当月详细单总数
		Integer count = 0;
		// 查询总详单数
		if (ApiConstants.RANK_TYPE_ALL.equals(billType))
			count = shopFundService.getShopDetailBillCount(month, shopId);
		// 查询销售详单数
		else if (ApiConstants.BILL_TYPE_SALE.equals(billType))
			count = shopFundService.getShopSaleDetailBillCount(month, shopId);
		//查询分红祥单数
		else if(ApiConstants.BILL_TYPE_BONUS.equals(billType)){
			count = shopFundService.getShopBonusDetailBillCount(month, shopId);
		}
		else if(ApiConstants.BILL_TYPE_COUPON.equals(billType)){
			count = couponShopService.getShopCouponDetailBillCount(month,shopId);
		}
		// 组装分页信息
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();

		// 查询list
		List<FundDetailBillDto> bills = null;
		// 查询总详单list
		if (ApiConstants.RANK_TYPE_ALL.equals(billType))
			bills = shopFundService.getShopDetailBill(page, shopId, month);
		else
		// 查询销售详单
		if (ApiConstants.BILL_TYPE_SALE.equals(billType)) {
			bills = shopFundService.getShopSaleDetailBill(page, shopId, month);
		}
		else 
		//查询分红详单
		if(ApiConstants.BILL_TYPE_BONUS.equals(billType)){
			bills = shopFundService.getShopBonusDetailBill(page, shopId, month);
		}
		else 
		//查询猫币
		if(ApiConstants.BILL_TYPE_COUPON.equals(billType)){
			bills = couponShopService.getShopCouponDetailBill(page, shopId, month);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", bills);
		map.put("page", page);
		return map;
	}

	/**
	 * 提现接口
	 * 
	 * @param token
	 * @return
	 * cw 2015年5月15日 11:20:44
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/withdrawCash")
	public Object withdrawCash(
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
		if (params == null || params.get("fund") == null || params.get("bankId") == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String fundString = params.getString("fund");
		fundString = StringUtils.filterBlank(fundString);
		BigDecimal fund = new BigDecimal(fundString);
		//获取绑定的银行卡id
		String bankId = params.getString("bankId");
		if (StringUtils.isBlank(bankId)||fund == null || fund.compareTo(BigDecimal.ZERO)<=0
				|| fund.intValue() > ApiConstants.PRO_MAX_WITHDRAW_FUND) {
			return new MsgEntity(ApiMsgConstants.WITHDRAW_ERROR_MSG,
					ApiMsgConstants.SUCCESS_CODE, ApiMsgConstants.SUCCESS_CODE_WITHDRAW_ERROR);
		}
		//如果有未处理的提现申请，提现失败
		String shopId = StringUtils.getCustomerIdByToken(token);
		// 查询用户信息包括小店信息
		Customer cus = customerService.get(shopId);
		if (cus == null || cus.getShop() == null
				|| cus.getShop().getAccounts() == null
				|| cus.getShop().getAccounts().isEmpty()) {
			Map<String, Object> map = new HashMap<>();
			map.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
			map.put(ApiConstants.MSG, ApiMsgConstants.ACCOUNT_NOT_BIND);
			map.put("status", ApiConstants.NO);
			return map;
		}
		Withdrawal withdrawal = new Withdrawal();
		withdrawal.setShopId(shopId);
		withdrawal.setWithdrawalStatus(ApiConstants.WITHDRAW_STATUS_NOT_CONFIRMED);
		Withdrawal www = null;
		Object result = null;
		RLock lock = DistLockHelper.getLock("withdrawCash");
		try {
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
			www = withdrawalService.get(withdrawal);
			if (www != null) {
				Map<String, Object> map = new HashMap<>();
				map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
				map.put(ApiConstants.MSG, ApiMsgConstants.WITHDRAW_CASH_FAILED);
				map.put("status", ApiConstants.NO);
				return map;
			}
			result =  shopFundService.withdrawCash(shopId,bankId,fund);
		} finally {
			if(lock.isLocked())
				lock.unlock();
		}
		return result;
	}

	/**
	 * 获取提现记录接口
	 * 
	 * @param token
	 * @return
	 * cw 2015年5月15日 11:20:34
	 */
	@ApiVersion(1)
	@RequiresRoles("seller")
	@RequestMapping("/api/getWithdrawLog")
	public Object getWithdrawCashLog(
			@RequestParam(value = "pageNo", defaultValue = "") int pageNo,
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "month", defaultValue = "") String month) {
		if (!IdcardUtils.isMonth(month)) {
			logger.debug("request params is illegal ");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String shopId = StringUtils.getCustomerIdByToken(token);
		//组装分页信息
		int count = withdrawalService.count(shopId,month);
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();
		List<WithdrawDto> list = withdrawalService.getWithdrawalLogList(page,
				shopId,month);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("page", page);
		map.put("list", list);
		return map;
	}
}
