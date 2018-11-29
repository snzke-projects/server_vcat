package com.vcat.api.web.v2;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.dto.ShopFundExpandDto;
import com.vcat.module.ec.entity.CouponFund;
import com.vcat.module.ec.entity.Shop;
import com.vcat.module.ec.entity.ShopFund;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

/**
 * 关于小店相关，请求控制器
 * 
 * @author cw 2015年5月9日 10:33:11
 */
@RestController
public class FundController2 extends RestBaseController {

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
	private static Logger logger = Logger.getLogger(FundController2.class);
	/**
	 * 获取收入账单接口，包括总账单和销售账单、分红账单
	 * @param token
	 * @param month
	 * @param billType
	 * @param pageNo
	 * @return
	 */
	@ApiVersion(2)
	@RequiresRoles("seller")
	@RequestMapping("/api/getDetailBill")
	public Object getDetailBill(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "billType", defaultValue = "") String billType,
			@RequestParam(value = "pageNo", defaultValue = "") int pageNo) {

		if(StringUtils.isNotEmpty(billType)){
			billType=billType.trim();
		}
		if (!IdcardUtils.isMonth(month) || StringUtils.isEmpty(billType)) {
			logger.debug("request params is illegal ");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		//String shopId = "818b1d7a78234a37bd50a74e64c08c80";
		String shopId = StringUtils.getCustomerIdByToken(token);
		// 查询当月详细单总数
		Integer count = 0;
		// 查询总详单数
		if (ApiConstants.RANK_TYPE_ALL.equals(billType))
			count = shopFundService.getShopDetailBillCount(month, shopId);
		// 查询销售详单数
		else if (ApiConstants.BILL_TYPE_SALE.equals(billType))
			count = shopFundService.getShopSaleDetailBillCount(month, shopId);
		//查询销售分红详单数 将原来的分开
		else if(ApiConstants.BILL_TYPE_BONUS.equals(billType)){
			count = shopFundService.getShopBonusDetailBillCount2(month, shopId);
		}
        // 一级团队分红
        else if(ApiConstants.BILL_TYPE_FIRSTBONUS.equals(billType)){
            count = shopFundService.getShopFirstBonusDetailBillCount(month, shopId);
        }
        // 二级团队分红
        else if(ApiConstants.BILL_TYPE_SECONDBONUS.equals(billType)){
            count = shopFundService.getShopSecondBonusDetailBillCount(month,shopId);
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
			bills = shopFundService.getShopDetailBill2(page, shopId, month);
		else
		// 查询销售详单
		if (ApiConstants.BILL_TYPE_SALE.equals(billType)) {
			bills = shopFundService.getShopSaleDetailBill2(page, shopId, month);
		}
		else 
		//查询销售分红详单
		if(ApiConstants.BILL_TYPE_BONUS.equals(billType)){
			bills = shopFundService.getShopBonusDetailBill3(page, shopId, month);
		}
        else
        //查询一级分红详单
        if(ApiConstants.BILL_TYPE_FIRSTBONUS.equals(billType)){
            bills = shopFundService.getShopFirstBonusDetailBill2(page, shopId, month);
        }
        else
        //查询二级分红详单
        if(ApiConstants.BILL_TYPE_SECONDBONUS.equals(billType)){
            bills = shopFundService.getShopSecondBonusDetailBill2(page, shopId, month);
        }
		else 
		//查询猫币
		if(ApiConstants.BILL_TYPE_COUPON.equals(billType)){
			bills = couponShopService.getShopCouponDetailBill2(page, shopId, month);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", bills);
		map.put("page", page);
		return map;
	}
	/**
	 * 获取提现记录接口
	 * 
	 * @param token
	 * @return
	 * cw 2015年5月15日 11:20:34
	 */
	@ApiVersion(2)
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
		List<WithdrawDto> list = withdrawalService.getWithdrawalLogList2(page,
				shopId,month);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("page", page);
		map.put("list", list);
		return map;
	}

    /**
     * 2016年12月03日  下午 3:19:07 dong4j
     * Sprint:9
     * 获取我的收入
     * 根据店铺类型返回不同的数据 特约小店 返回收入
     * @param token the token
     * @return the my fund
     */
    @ApiVersion(2)
    @RequiresRoles("seller")
    @RequestMapping("/api/getMyFund")
    public Object getMyFund(@RequestHeader(value = "token", defaultValue = "") String token) {
        //String shopId = "818b1d7a78234a37bd50a74e64c08c80";
        String shopId = StringUtils.getCustomerIdByToken(token);
        Shop shop = shopService.get(shopId);
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
        map.put("fundInfo", fund);
        map.put("isVIP",true);
        // shopType  0:特约且未达到升级条件 1:白金 2:审核中 3:驳回 4:达到升级要求但是还未升级
        map.put("shopType",shopService.getShopStatus(shopId));
        if(shop.getAdvancedShop() != 1){ // 非白金
            // 获取店铺中销售额
            BigDecimal nowSales = shopService.getTotalSale(shopId);
            map.put("nowSales", nowSales);
            map.put("defaultSalesLimit", DictUtils.getDictValue("ec_upgrade_sales_limit", ""));
            map.put("isVIP", false);
            //

        }
        return map;
    }

    /**
     * 2016年12月03日  下午 4:07:23 dong4j
     * Sprint:9
     * 销售收入
     * 根据不同的店铺类型返回不同的数据
     * @param token the token
     * @return the sell earning
     */
    @ApiVersion(2)
    @RequiresRoles("seller")
    @RequestMapping("/api/getSellEarning")
    public Object getSellEarning(@RequestHeader(value = "token", defaultValue = "") String token) {
        //String shopId = "818b1d7a78234a37bd50a74e64c08c80";
        String shopId = StringUtils.getCustomerIdByToken(token);
        Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        Shop shop = shopService.get(shopId);
        ShopFundExpandDto fund = null;
        if(shop.getAdvancedShop() == 1){
            fund   = shopFundService.getShopFund2(shopId);
            // 构建返回对象
            if (fund == null) {
                map.put("sellEarning", new ShopFund(new BigDecimal(0)));
                return map;
            }
        }
        map.put("sellEarning", fund);
        return map;
    }
}
