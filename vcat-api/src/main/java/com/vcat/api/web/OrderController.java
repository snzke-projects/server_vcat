package com.vcat.api.web;

import java.util.*;

import com.vcat.api.service.*;
import com.vcat.common.config.Global;
import com.vcat.config.CacheConfig;
import com.vcat.module.ec.dto.*;
import com.vcat.module.ec.entity.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vcat.api.exception.ApiException;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.EmojiFilter;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.kuaidi100.pojo.NoticeRequest;
import com.vcat.module.core.entity.MsgEntity;

@RestController
@ApiVersion({1,2})
public class OrderController extends RestBaseController{

	@Autowired
	private OrderService orderService;
	@Autowired
	private OaNotifyService oaNotifyService;
	@Autowired
	private OrderItemService orderItemService;
	@Autowired
	private ExpressService expressService;
	@Autowired
	private ExpressApiService expressApiService;
	@Autowired
	private ProductItemService productItemService;
	@Autowired
	private CustomerAddressService customerAddressService;
	@Autowired
	private RefundService          refundService;
	@Autowired
	private RefundLogService       refundLogService;
	@Autowired
	private CustomerService        customerService;
	@Autowired
	private CouponFundService      couponFundService;
	@Autowired
	private RatingSummaryService   ratingSummaryService;
	@Autowired
	private FreightConfigService   freightConfigService;
	@Autowired
	private ReviewDetailService    reviewDetailService;
	@Autowired
	private ProductService         productService;
	@Autowired
	private ShopInfoService shopInfoService;
	@Autowired
	private AddressService addressService;
	/**
	 * 卖家提醒厂商客户发货消息
	 * @param token
	 * @return
	 */
	//@RequiresRoles("seller")
	//@RequestMapping("/api/deliveryRemind")
	//public Object deliveryRemind(@RequestHeader(value = "token", defaultValue = "") String token,
	//							 @RequestParam(value = "orderNum", defaultValue = "") String orderNum){
	//	if(StringUtils.isEmpty(orderNum)){
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	String shopId = StringUtils.getCustomerIdByToken(token);
	//	// 根据订单号判断是否发送过提现发货消息
	//	OaNotify notify = oaNotifyService
	//			.findDeliveryNotifyByOrderNum(orderNum);
	//	//如果存在，返回成功
	//	if(notify!=null){
	//		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
	//				ApiMsgConstants.SUCCESS_CODE);
	//	}
	//	//根据订单号查处对应厂商的所有发货员id
	//	List<String> list = orderItemService.findDeliveryerList(orderNum);
	//	if(list==null){
	//		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
	//				ApiMsgConstants.SUCCESS_CODE);
	//	}
	//	OaNotify oaNotify = new OaNotify();
	//	oaNotify.preInsert();
	//	//5为发货通知
	//	User user = new User();
	//	user.setId(shopId);
	//	oaNotify.setType("5");
	//	oaNotify.setCreateBy(user);
	//	oaNotify.setUpdateBy(user);
	//	oaNotify.setTitle("提醒订单号["+orderNum+"]发货。");
	//	oaNotify.setContent("提醒订单号["+orderNum+"]发货。");
	//	oaNotify.setDelFlag("0");
	//	oaNotify.setStatus("1");
	//	List<OaNotifyRecord> records = new  ArrayList<OaNotifyRecord>();
	//	//组装订单记录表
	//	for(String userId:list){
	//		OaNotifyRecord record = new OaNotifyRecord();
	//		record.preInsert();
	//		User user1 = new User();
	//		user1.setId(userId);
	//		record.setOaNotify(oaNotify);
	//		record.setUser(user1);
	//		record.setReadFlag("0");
	//		records.add(record);
	//	}
	//	oaNotify.setOaNotifyRecordList(records);
	//	oaNotifyService.save(oaNotify);
	//	return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
	//			ApiMsgConstants.SUCCESS_CODE);
	//}
	/**
	 * 买家提醒厂商客户发货消息
	 * @param token
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/buyerDeliveryRemind")
	//public Object buyerDeliveryRemind(@RequestHeader(value = "token", defaultValue = "") String token,
	//								  @RequestParam(value = "params", defaultValue = "") String param){
	//	// 简单验证,保证服务端不会出异常
	//	JSONObject params = null;
	//	try {
	//		params = JSONObject.parseObject(param);
	//	} catch (Exception e) {
	//		logger.error("params 出错"+e.getMessage());
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	if (params == null) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	String  orderNum = params.getString("orderNum");
	//	return deliveryRemind(token,orderNum);
	//}
	/**
	 * 获取店铺的订单信息列表，orderType为订单类型
	 * 未付款,待处理,已完成,已关闭,退款
	 * @param token
	 * @param orderType
	 * @param pageNo
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getOrderList")
	public Object getOrderList(@RequestHeader(value = "token", defaultValue = "") String token,
							   @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
							   @RequestParam(value = "orderType", defaultValue = "1") int orderType){

		String shopId = StringUtils.getCustomerIdByToken(token);
		int count = orderService.countByShopId(shopId,orderType);
		// 组装分页信息
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();
		List<OrderDto> list = orderService.getOrderList(shopId,page,orderType);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", list);
		map.put("page", page);
		map.put("orderType", orderType);
		return map;
	}
	/**
	 * 获取买家的订单信息列表，orderType为订单类型
	 * 1 全部 2待支付 3待发货 4待收货 5已完成 6退款
	 * @param token
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/getBuyerOrderList")
	//public Object getBuyOrderList(@RequestHeader(value = "token", defaultValue = "") String token,
	//							  @RequestParam(value = "params",defaultValue="") String param){
	//	// 简单验证,保证服务端不会出异常
	//	JSONObject params = null;
	//	try {
	//		params = JSONObject.parseObject(param);
	//	} catch (Exception e) {
	//		logger.error("params 出错"+e.getMessage());
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	if (params == null) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	String orderType = params.getString("orderType");
	//	int pageNo  = params.getIntValue("pageNo");
	//	String buyerId = StringUtils.getCustomerIdByToken(token);
	//	int count = orderService.countByBuyerId(buyerId,orderType);
	//	// 组装分页信息
	//	Pager page = new Pager();
	//	page.setPageNo(pageNo);
	//	page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
	//	page.setRowCount(count);
	//	page.doPage();
	//	List<OrderDto> list = orderService.getBuyerOrderList(buyerId,page,orderType);
	//	// 删除待付款,待收货,已完成订单中的已完成退款的订单
	//	if(list != null){
	//		Iterator<OrderDto> orderDtoIterator = list.iterator();
	//		while(orderDtoIterator.hasNext()){
	//			OrderDto orderDto = orderDtoIterator.next();
	//			if(!Objects.equals(orderType, "6")  				// 不是退款
	//					&& orderDto.getProductItems().size() == 1 	// 订单中只有一个订单项
	//					&& orderDto.getProductItems().get(0).getAllreFundStatus().equals("3")	// 订单项的退款状态为已完成
	//					&& orderDto.getProductItems().get(0).getQuantity()  == orderDto.getProductItems().get(0).getReQuantity()
	//					){
	//				orderDtoIterator.remove();
	//				continue;
	//			}
	//			int counts = 0;
	//			for(ProductItemDto productItemDto : orderDto.getProductItems()){
	//				if(productItemDto.getAllreFundStatus().equals("3") &&  // 如果是退款完成
	//						productItemDto.getQuantity() == productItemDto.getReQuantity() // 如果是全部退款
	//						){
	//					counts++;
	//				}
	//				productItemDto.setQuantity(productItemDto.getQuantity()+productItemDto.getPromotionQuantity());
	//			}
	//			if(counts == orderDto.getProductItems().size() && !Objects.equals(orderType, "6")){
	//				orderDtoIterator.remove();
	//			}
	//		}
	//	}
	//	//退款是否包含邮费
	//	if (list != null && list.size() > 0) {
	//		for (OrderDto order : list) {
	//			List<ProductItemDto> productItems = order.getProductItems();
	//			if (productItems != null && productItems.size() > 0) {
	//				for (ProductItemDto item : productItems) {
	//					Refund re = refundService.getRefundAmount(item.getOrderItemId());
	//					if(re != null&&ApiConstants.YES.equals(item.getAllreFundStatus())){
	//						item.setHasFreightPrice(re.getHasFreightPrice());
	//					}
	//				}
	//			}
	//		}
	//	}
    //
	//	Map<String, Object> map = new HashMap<String, Object>();
	//	map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
	//	map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
	//	map.put("orderList", list);
	//	map.put("page", page);
	//	//获取我的猫币金额
	//	Shop shop = new Shop();
	//	BigDecimal couponFundPrice = BigDecimal.ZERO;
	//	shop.setId(buyerId);
	//	CouponFund couponFund = new CouponFund();
	//	couponFund.setShop(shop);
	//	CouponFund conFund = couponFundService.get(couponFund);
	//	if(conFund!=null){
	//		couponFundPrice = conFund.getAvailableFund();
	//	}
	//	map.put("shopCouponFund", couponFundPrice);
	//	return map;
	//}

	/**
	 * 卖家获取快递详情
	 * @param token
	 * @return
	 */
	//@RequiresRoles("seller")
	@RequestMapping("/api/getExpressInfo")
	public Object getExpressInfo(@RequestHeader(value = "token", defaultValue = "") String token,
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
		String deliveryNum = params.getString("deliveryNum");
		String expressCode = params.getString("expressCode");
		if (StringUtils.isEmpty(deliveryNum)
				|| StringUtils.isEmpty(expressCode)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}

		deliveryNum = StringUtils.filterBlank(deliveryNum);
		expressCode = StringUtils.filterBlank(expressCode);

		NoticeRequest requst = null;
		String date = expressApiService.query(expressCode,deliveryNum);
		if(date!=null)
			requst= JSONObject.parseObject(date,NoticeRequest.class);
		//查询物流logo和名字
		Express express = expressService.findByCode(expressCode);
		if(express==null){
			return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
					ApiMsgConstants.SUCCESS_CODE);
		}
		Map<String, Object> res = new HashMap<String, Object>();
		if(requst==null){
			requst = new NoticeRequest();
		}
		res.put("data", requst.getLastResult() == null ? "" : requst.getLastResult().getData());
		res.put("expressName", express.getName());
		res.put("logoUrl", QCloudUtils.createThumbDownloadUrl(express.getLogoUrl(), ApiConstants.DEFAULT_AVA_THUMBSTYLE));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("expressInfo", res);
		return map;
	}

	/**
	 * 买家获取快递详情
	 * @param token
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/getBuyerExpressInfo")
	//public Object getBuyerExpressInfo(@RequestHeader(value = "token", defaultValue = "") String token,
	//								  @RequestParam(value = "params", defaultValue = "") String param){
	//	return getExpressInfo(token,param);
	//}
	/**
	 * 卖家删除订单接口
	 * @param token
	 * @param orderId
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/delOrder")
	public Object delOrder(@RequestHeader(value = "token", defaultValue = "") String token,
						   @RequestParam(value = "orderId", defaultValue = "") String orderId){
		if(StringUtils.isBlank(orderId)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		orderService.sellerDelete(orderId);
		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
				ApiMsgConstants.SUCCESS_CODE);
	}

	/**
	 * 获取订单详情（卖家）
	 * @param token
	 * @param orderId
	 * @param orderNumber
	 * @return
	 */
	//@RequiresRoles("seller")
	//@RequestMapping("/api/getOrderInfo")
	//public Object getOrderInfo(
	//		@RequestHeader(value = "token", defaultValue = "") String token,
	//		@RequestParam(value = "orderId", defaultValue = "", required = false) String orderId,
	//		@RequestParam(value = "orderNumber", defaultValue = "", required = false) String orderNumber) {
	//	if (StringUtils.isBlank(orderId) && StringUtils.isBlank(orderNumber)) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	Map<String, Object> map = new HashMap<>();
	//	OrderDto order = new OrderDto();
	//	order.setId(orderId);
	//	order.setOrderNum(orderNumber);
	//	try {
	//		map.put("order",orderService.getOrder(order,StringUtils.getCustomerIdByToken(token)));
	//	} catch (Exception e) {
	//		logger.error(e.getMessage(),e);
	//		return new MsgEntity("获取订单详情失败：" + e.getMessage(), ApiMsgConstants.FAILED_CODE);
	//	}
	//	map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
	//	map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
	//	return map;
	//}
	/**
	 * 下单接口  如果 productType=5 标识店主拿货  productType=6 v猫庄园
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/checkOrders")
	public Object checkOrders(
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
		// 包含店铺和产品项的list
		JSONArray list = params.getJSONArray("list");
		// "addressId": "9082facc42cc4f45b3cbe6253cef01af",
		String addressId = params.getString("addressId");
		//获取订单发票
		Invoice invoice = null;
		Object invoiceObj = params.get("invoice");
		if(invoiceObj!=null){
			JSONObject gson = (JSONObject) invoiceObj;
			invoice = JSON.toJavaObject(gson,Invoice.class);
		}
		if (StringUtils.isBlank(addressId) || list == null || list.size() == 0) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		// 如果是庄园商品
		//String shopInfoId = params.getString("shopInfoId");
		String groupBuySponsorId = params.getString("groupBuySponsorId");
		String groupBuyId = params.getString("groupBuyId");
		Customer buyer = new Customer();
		buyer.setId(buyerId);
		CustomerAddress cta = new CustomerAddress();
		cta.setCustomer(buyer);
		Address address = new Address();
		address.setId(addressId);
		cta.setAddress(address);
		CustomerAddress customerAddress = customerAddressService.get(cta);
		if (customerAddress == null) {
			return new MsgEntity(ApiMsgConstants.ADDRESS_NOT_FIND,
					ApiMsgConstants.FAILED_CODE);
		}
		Map<String,Object> resultMap  = new HashMap<>();
		String note = params.getString("note");
		try{
			resultMap = orderService.checkOrders(buyer, //买家
					list,   //包含店铺和产品项的list
					customerAddress, //客户地址
					invoice ,//发票
					"",
					groupBuySponsorId,
					groupBuyId,
					note
			);
		}catch(ApiException e){
//			logger.error(e.getMessage(),e);
			resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
			resultMap.put(ApiConstants.MSG,e.getMessage());
		}

		return resultMap;
	}
	/**
	 * 获取默认地址
	 * @param token
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/getDefaultAddress")
	//public Object getDefaultAddress(@RequestHeader(value = "token", defaultValue = "") String token){
	//	String buyerId = StringUtils.getCustomerIdByToken(token);
	//	Customer cus = customerService.get(buyerId);
	//	Address list = customerAddressService.getDefaultAddressList(buyerId);
	//	Map<String, Object> map = new HashMap<String, Object>();
	//	map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
	//	map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
	//	if(list!=null)
	//		map.put("address", list);
	//	if(cus!=null){
	//		map.put("phoneNum", cus.getPhoneNumber());
	//	}
	//	return map;
	//}

	/**
	 * 2016.04.06
	 * 获取买家信息(结算页) 庄园主信息+默认地址信息
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/getBuyerInfo")
	public Object getBuyerInfo(
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
		// 获取默认地址
		String buyerId = StringUtils.getCustomerIdByToken(token);
		Customer cus = customerService.get(buyerId);
		String addressId = params.getString("addressId");
		Address address;
		if(!StringUtils.isNullBlank(addressId)){
			address = customerAddressService.getChooseAddress(buyerId,addressId);
		}else{
			address = customerAddressService.getDefaultAddressList(buyerId);
		}

		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		if(address!=null)
			map.put("address", address);
		if(cus!=null){
			map.put("phoneNum", cus.getPhoneNumber());
		}
		//// 获取庄园主信息
		String type = params.getString("type");
		// 如果有type字段且为6时,获取庄园主信息
		if(!StringUtils.isBlank(type) && type.equals(ApiConstants.LEROY)){
			Product product = productService.getVcatProductDetail(params.getString("productId"),buyerId,type);
			map.put("farmWechatNo",product.getFarmWechatNo() == null ? Global.getConfig("service.wechat") : product.getFarmWechatNo());
		}
		return map;
	}

	/**
	 * 买家取消订单接口
	 * @param token
	 * @param
	 * @return 如果是普通预售订单,则增加小店库存
	 * 如果是其他 则按原来逻辑处理
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/cancelOrder")
	//public Object cancelOrder(@RequestHeader(value = "token", defaultValue = "") String token,
	//						  @RequestParam(value = "params", defaultValue = "") String param){
	//	// 简单验证,保证服务端不会出异常
	//	JSONObject params = null;
	//	try {
	//		params = JSONObject.parseObject(param);
	//	} catch (Exception e) {
	//		logger.error("params 出错"+e.getMessage());
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	if (params == null) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	String buyerId = StringUtils.getCustomerIdByToken(token);
	//	String orderId = params.getString("orderId");
	//	if(StringUtils.isBlank(orderId)){
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	orderService.buyerCloseOrder(orderId,buyerId);
	//	return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
	//			ApiMsgConstants.SUCCESS_CODE);
	//}
	/**
	 * 买家删除订单接口
	 * @param token
	 * @param
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/delBuyerOrder")
	//public Object delBuyerOrder(@RequestHeader(value = "token", defaultValue = "") String token,
	//							@RequestParam(value = "params", defaultValue = "") String param){
	//	// 简单验证,保证服务端不会出异常
	//	JSONObject params = null;
	//	try {
	//		params = JSONObject.parseObject(param);
	//	} catch (Exception e) {
	//		logger.error("params 出错"+e.getMessage());
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	if (params == null) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	String orderId = params.getString("orderId");
	//	if(StringUtils.isBlank(orderId)){
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	orderService.buyerDelete(orderId);
	//	return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
	//			ApiMsgConstants.SUCCESS_CODE);
	//}

	/**
	 * 买家确认收货接口 增加卖家收入(返佣+分红)
	 * @param token
	 * @param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/checkShipping")
	public Object checkShipping(@RequestHeader(value = "token", defaultValue = "") String token,
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
		String orderId = params.getString("orderId");
		String buyerId = StringUtils.getCustomerIdByToken(token);
		if(StringUtils.isBlank(orderId)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		Map<String,Object> resultMap  = new HashMap<>();
		try{
			resultMap =  orderService.sellerCheckShipping(buyerId,orderId);
		}catch(ApiException e){
			logger.error(e.getMessage(),e);
			resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
			resultMap.put(ApiConstants.MSG,e.getMessage());
		}
		return resultMap;
	}
	/**
	 * 退款申请
	 * @param token
	 * @param param
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/reFund")
	//public Object reFund(@RequestHeader(value = "token", defaultValue = "") String token,
	//					 @RequestParam(value = "params", defaultValue = "") String param){
	//	JSONObject params = null;
	//	try {
	//		params = JSONObject.parseObject(param);
	//	} catch (Exception e) {
	//		logger.error("params 出错"+e.getMessage());
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	if (params == null) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	String buyerId = StringUtils.getCustomerIdByToken(token);
	//	String orderItemId = params.getString("orderItemId");
	//	String reason = params.getString("reason");
	//	String phone = params.getString("phone");
	//	int reFundQuantity = params.getIntValue("reFundQuantity");
	//	if (StringUtils.isBlank(orderItemId) || reFundQuantity == 0) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
     //   OrderItem orderItem = new OrderItem();
     //   orderItem.setId(orderItemId);
     //   orderItem = orderItemService.get(orderItem);
	//	if(orderItem != null && !orderItem.getProductItem().getProduct().getCanRefund()){
	//		return new MsgEntity(ApiMsgConstants.FAILED_MSG,ApiMsgConstants.FAILED_CODE);
	//	}
    //
	//	String refundId = params.getString("refundId");
	//	Refund rf = new Refund();
	//	rf.setId(refundId);
	//	//查询退款单是否可以更新
	//	Refund refund = refundService.get(rf);
	//	if(refund!=null){
	//		if(!Refund.REFUND_STATUS_NO_REFUND.equals(refund.getRefundStatus())){
	//			return new MsgEntity(ApiMsgConstants.REFUND_NO_COMFIRE,
	//					ApiMsgConstants.FAILED_CODE);
	//		}
	//		RefundLog log = new RefundLog();
	//		log.preInsert();
	//		log.setRefund(refund);
	//		log.setStatusNote("您的退款原因修改成功，请等待后台人员再次审核");
	//		refundLogService.insert(log);
	//		refund.setReturnReason(reason);
	//		refundService.updateReason(refund);
	//	}else{
	//		//判断此订单项是否可以申请退款
	//		OrderItemDto item  = null;
	//		RLock lock = DistLockHelper.getLock("reFund");
	//		try {
	//			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
	//			item = orderItemService.checkReFund(orderItemId);
	//			if (item == null || StringUtils.isBlank(reason)) {
	//				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//						ApiMsgConstants.FAILED_CODE);
	//			}
	//			if (item.isRefundStatus()) {
	//				return new MsgEntity(ApiMsgConstants.REFUNDED,
	//						ApiMsgConstants.FAILED_CODE);
	//			}
	//			// 删除之前的退款申请
	//			refundService.deleteRefund(item.getId(),"");
	//			// 获取买家信息
	//			Customer cus = customerService.get(buyerId);
	//			if (cus == null) {
	//				return new MsgEntity(ApiMsgConstants.REFUNDED,
	//						ApiMsgConstants.FAILED_CODE);
	//			}
	//			BigDecimal amount = BigDecimal.ZERO;
	//			String hasFreightPrice = "";
    //
     //           // 如果此订单向中包含订单数量 退款时,退款数量必须等于 购买的数量+优惠的数量
     //           if(item.getPromotionQuantity() != 0 && (reFundQuantity < (item.getPromotionQuantity() + item.getQuantity()))){
     //               return new MsgEntity(ApiMsgConstants.PROMOTION_PRODUCT,
     //                       ApiMsgConstants.FAILED_CODE);
     //           }
     //           if (item.getPromotionQuantity() == 0 && item.getQuantity() < reFundQuantity) {
     //               return new MsgEntity(ApiMsgConstants.REFUND_NUM_TOOMUCH,
     //                       ApiMsgConstants.FAILED_CODE);
     //           }
     //           //如果当前订单项数量和退款数量相同，查询退款金额（可能包含邮费）
	//			if((item.getQuantity() + item.getPromotionQuantity())==reFundQuantity){
	//				Refund re = refundService.getRefundAmount(orderItemId);
	//				if(re != null){
	//					amount = re.getAmount();
	//					hasFreightPrice = re.getHasFreightPrice();
	//				}
	//			}else{
	//				amount = item.getItemPrice().multiply(new BigDecimal(reFundQuantity));
	//				hasFreightPrice = ApiConstants.NO;
	//			}
	//			// 组装退款申请
	//			Refund ref = new Refund();
	//			ref.preInsert();
	//			ref.setPhone(phone);
	//			ref.setAmount(amount);
	//			ref.setHasFreightPrice(hasFreightPrice);
	//			if(item.getPromotionQuantity() != 0){ //如果有优惠 则必须退全部
	//				ref.setQuantity(reFundQuantity - item.getPromotionQuantity());
	//			}else {
     //               ref.setQuantity(reFundQuantity);
     //           }
	//			ref.setReturnReason(reason);
	//			ref.setOrderItem(new OrderItem(orderItemId));
	//			ref.setNote(cus.getUserName() + "申请退款成功");
	//			ref.setCustomer(cus);
	//			refundService.save(ref);
	//			refundId = ref.getId();
	//			logger.debug(cus.getUserName()+"申请退款成功，退款单："+refundId);
	//		} finally {
	//			if(lock.isLocked())
	//				lock.unlock();
	//		}
	//	}
	//	Map<String, Object> map = new HashMap<String, Object>();
	//	map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
	//	map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
	//	//发送退款申请成功
	//	map.put("refundStatus", Refund.REFUND_STATUS_NO_REFUND);
	//	map.put("refundId", refundId);
	//	return map;
	//}
	/**
	 * 买家退款日志
	 * @param token
	 * @param param
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/BuyerReFundLog")
	//public Object reFundBuyerLog(@RequestHeader(value = "token", defaultValue = "") String token,
	//							 @RequestParam(value = "params", defaultValue = "") String param){
	//	JSONObject params = null;
	//	try {
	//		params = JSONObject.parseObject(param);
	//	} catch (Exception e) {
	//		logger.error("params 出错"+e.getMessage());
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	if (params == null) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	String orderItemId = params.getString("orderItemId");
	//	if(StringUtils.isBlank(orderItemId)){
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	//获取退款单信息
	//	RefundDto	refundDto = refundService.getRefund(orderItemId);
	//	//获取日志
    //
	//	RefundLog log = new RefundLog();
	//	List<RefundLogDto> list = null;
	//	if(refundDto!=null){
	//		log.setOrderItemId(refundDto.getOrderItemId());
	//		list = refundLogService.findLogList(log);
	//	}
	//	Map<String, Object> map = new HashMap<String, Object>();
	//	map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
	//	map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
	//	map.put("refund", refundDto);
	//	if(list!=null)
	//		map.put("logList", list);
	//	return map;
	//}

	/**
	 * 后台没审核时，买家修改退货原因
	 * @param token
	 * @param param
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/updateRefundReason")
	//public Object updateRefundReason(@RequestHeader(value = "token", defaultValue = "") String token,
	//								 @RequestParam(value = "params", defaultValue = "") String param){
	//	JSONObject params = null;
	//	try {
	//		params = JSONObject.parseObject(param);
	//	} catch (Exception e) {
	//		logger.error("params 出错"+e.getMessage());
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	if (params == null) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
    //
	//	String refundId = params.getString("refundId");
	//	String reason = params.getString("reason");
	//	Refund rf = new Refund();
	//	rf.setId(refundId);
	//	//查询退款单是否可以更新
	//	Refund refund = refundService.get(rf);
	//	if(refund==null||!Refund.REFUND_STATUS_NO_REFUND.equals(refund.getRefundStatus())){
	//		return new MsgEntity(ApiMsgConstants.REFUND_NO_COMFIRE,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	refund.setReturnReason(reason);
	//	refundService.updateReason(refund);
	//	return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
	//			ApiMsgConstants.SUCCESS_CODE);
	//}

	/**
	 * 后台没审核通过并需要退货时，买家增加退款物流号
	 * @param token
	 * @param param
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/addRefundExpress")
	//public Object addRefundExpress(@RequestHeader(value = "token", defaultValue = "") String token,
	//							   @RequestParam(value = "params", defaultValue = "") String param){
	//	JSONObject params = null;
	//	try {
	//		params = JSONObject.parseObject(param);
	//	} catch (Exception e) {
	//		logger.error("params 出错"+e.getMessage());
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	if (params == null) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
    //
	//	String refundId = params.getString("refundId");
	//	String shippingNum = params.getString("shippingNum");
	//	String expressCode = params.getString("expressCode");
    //
	//	if(StringUtils.isBlank(expressCode)||StringUtils.isBlank(shippingNum)){
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	// 订阅快递100 推送物流信息服务
	//	ThreadPoolUtil.execute(() -> {
	//		TaskResponse tr = ExpressUtils.subscribeExpress(expressCode,shippingNum);
	//		logger.info("退款单[" + refundId + "]订阅物流信息完成：" + tr.toString());
	//	});
	//	//查询退款单是否可以更新
	//	Refund rf = new Refund();
	//	rf.setId(refundId);
	//	//只有允许退货，买家才可以添加退货单
	//	Refund refund = refundService.get(rf);
	//	if(refund==null||!Refund.REFUND_STATUS_REFUND.equals(refund.getReturnStatus())){
	//		return new MsgEntity(ApiMsgConstants.REFUND_NOT_COMFIRE,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	Express express = expressService.findByCode(expressCode);
	//	if(express==null){
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	refund.setExpress(express);
	//	refund.setShippingNumber(shippingNum);
	//	//设置退款单退货状态为退货中
	//	refund.setReturnStatus(Refund.RETURN_STATUS_RETURNS_IN);
	//	refund.setNote("买家退货单已添加");
	//	refundService.saveExpress(refund);
	//	return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
	//			ApiMsgConstants.SUCCESS_CODE);
	//}
	/**
	 * 买家取消退货单
	 * @param token
	 * @param
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/cancelReFund")
	//public Object cancelReFund(@RequestHeader(value = "token", defaultValue = "") String token,
	//						   @RequestParam(value = "params", defaultValue = "") String param){
	//	// 简单验证,保证服务端不会出异常
	//	JSONObject params = null;
	//	try {
	//		params = JSONObject.parseObject(param);
	//	} catch (Exception e) {
	//		logger.error("params 出错"+e.getMessage());
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	if (params == null) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	String refundId = params.getString("refundId");
	//	Refund rf = new Refund();
	//	rf.setId(refundId);
	//	//查询退款单是否可以更新
	//	Refund refund = refundService.get(rf);
	//	if(refund==null||(!Refund.RETURN_STATUS_CHECK_SUCC.equals(refund.getReturnStatus())
	//			&&!Refund.RETURN_STATUS_UNTREATED.equals(refund.getReturnStatus()))){
	//		return new MsgEntity(ApiMsgConstants.RETURN_STATUS_APP_FAILED,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	refundService.cancelRefund(refund);
	//	return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
	//			ApiMsgConstants.SUCCESS_CODE);
	//}
	/**
	 * 获取结算金额接口
	 * @param token
	 * @param param
	 */

	@RequiresRoles("buyer")
	@RequestMapping("/api/getTotalPrice")
	public Object getCartTotalPrice(
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
		// 包含店铺和产品项的list
		JSONArray list = params.getJSONArray("list");

		Map<String,Object> resultMap  = new HashMap<>();
		try{
			resultMap = orderService.getCartTotalPrice(buyerId,list);
		}catch(ApiException e){
//			logger.error(e.getMessage(),e);
			resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
			resultMap.put(ApiConstants.MSG,e.getMessage());
		}
		return resultMap;
	}
	/**
	 * 检查商品是否全额购买，且是否已经购买过
	 * 注意getTotalPrice里面有相同逻辑
	 * @param token
	 * @param param
	 * @return
	 */
	@RequestMapping("/anon/checkProduct")
	public Object checkProduct(@RequestHeader(value = "token", defaultValue = "") String token,
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
		String productItemId = params.getString("productItemId");
		String productType = params.getString("productType");
		if(StringUtils.isNullBlank(productItemId) || StringUtils.isNullBlank(productType)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		Map<String,Object> resultMap  = new HashMap<String,Object>();
		resultMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		//应用规则，判断此商品是否已经购买过，是否可以购买
		Map<String, Object> rule = orderService.getProductLimit(productItemId);
		if(rule == null || rule.isEmpty()){
			resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
			return resultMap;
		}
		Date startTime = (Date)rule.get("start_time");
		Date endTime = (Date)rule.get("end_time");
		Date now = new Date();
		if(now.getTime() >= startTime.getTime() && now.getTime() <= endTime.getTime()){
			int interval = (Integer)rule.get("interval");
			int times = (Integer)rule.get("times");
			if(interval == -1){
				resultMap.put(ApiConstants.MSG, "限购"+times+"件");
			}else {
				resultMap.put(ApiConstants.MSG, (interval + 1) + "天内限购" + times + "件");
			}
			resultMap.put("times", times);
		}else {
			resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		}
		return resultMap;
	}
	/**
	 * 获取订单评价列表
	 * @param token
	 * @param orderId
	 * @return
	 */
	//@RequiresRoles("buyer")
	//@RequestMapping("/api/getOrderReview")
	//public Object getOrderReview(@RequestHeader(value = "token", defaultValue = "") String token,
	//							 @RequestParam(value = "orderId", defaultValue = "") String orderId){
	//	if(StringUtils.isBlank(orderId)){
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	List<ReviewDto> list = orderService.getReviewProductItemList(orderId);
	//	Map<String, Object> map = new HashMap<String, Object>();
	//	map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
	//	map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
	//	map.put("list", list);
	//	return map;
	//}
	/**
	 * 评价商品
	 * @param token
	 * @param
	 * @return
	 */
	//@RequiresRoles(value = {"buyer","seller"},logical = Logical.OR)
	//@RequestMapping("/api/reviewProduct")
	//public Object reviewProduct(@RequestHeader(value = "token", defaultValue = "") String token,
	//							@RequestParam(value = "params", defaultValue = "") String param){
	//	// 简单验证,保证服务端不会出异常
	//	JSONObject params = null;
	//	try {
	//		params = JSONObject.parseObject(param);
	//	} catch (Exception e) {
	//		logger.error("params 出错" + e.getMessage());
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	if (params == null) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	String buyerId = StringUtils.getCustomerIdByToken(token);
	//	Integer rating = params.getInteger("rating");
	//	//默认评分为1
	//	if(rating==null){
	//		rating = 1;
	//	}
	//	if(rating>5){
	//		rating = 5;
	//	}
	//	// 评分内容
	//	String reviewText = params.getString("reviewText");
	//	// 订单项id
	//	String orderItemId = params.getString("orderItemId");
	//	//通过订单项查看订单是否已经确认收货
	//	String orderId = orderService.getOrderByItemId(orderItemId);
	//	if(StringUtils.isBlank(orderId)){
	//		return new MsgEntity(ApiMsgConstants.REVIEW_ILLEGAL,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	//商品id
	//	String productId = params.getString("productId");
	//	if (StringUtils.isBlank(productId) || StringUtils.isBlank(reviewText)
	//			|| StringUtils.isBlank(orderItemId)) {
	//		return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	//判断此用户是否同一订单同一商品评价过此商品
	//	ReviewDetail  rd = reviewDetailService.getReviewByProduct(buyerId,productId,orderItemId);
	//	if(rd != null){
	//		return new MsgEntity(ApiMsgConstants.REVIEW_IS_EXIST,
	//				ApiMsgConstants.FAILED_CODE);
	//	}
	//	ratingSummaryService.saveReview(buyerId,productId,orderItemId,rating,reviewText);
	//	Map<String, Object> map = new HashMap<String, Object>();
	//	map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
	//	map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
	//	return map;
	//}
	/**
	 * 获取评价列表 (商品详情页使用)
	 * @param param
	 * @return
	 */
	@RequestMapping("/anon/getReviewList")
	@Cacheable(value = CacheConfig.GET_REVIEW_LIST_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public Object getReviewList(@RequestParam(value = "params", defaultValue = "") String param){
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错" + e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String productId = params.getString("productId");
		Integer pageNo = params.getInteger("pageNo");
		Integer pageSize = params.getInteger("pageSize");
		if(pageSize==null){
			pageSize = ApiConstants.DEFAULT_PAGE_SIZE;
		}
		if(pageNo == null){
			pageNo = 1;
		}
		if (StringUtils.isBlank(productId)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		int count = ratingSummaryService.countReviewList(productId);
		// 组装分页信息
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(pageSize);
		page.setRowCount(count);
		page.doPage();
		List<Map<String,Object>> list = ratingSummaryService.getReviewList(productId,page);
		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {

				String userName = (String) map.get("buyerName");
				if (!StringUtils.isBlank(userName)) {
					logger.debug("用户名的长度" + userName.length());
					if (userName.length() > 2) {
						String emoji = userName.substring(0, 2);
						if (EmojiFilter.isEmojiCharacter(emoji)) {
							logger.debug("用户名包含emoji表情");
							userName = emoji + "***";
						} else {
							userName = userName.charAt(0) + "***";
						}
					} else {
						userName = userName.charAt(0) + "***";
					}
					map.put("buyerName", userName);
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", list);
		map.put("page", page);
		return map;
	}

	/**
	 * 接口测试 手动调用结算
	 */
    @RequestMapping("/anno/checkShippingEarning/jkerbviwubvpwe234rferv")
    public Object checkShippingEarning(@RequestParam(value = "params", defaultValue = "") String param){
        // 简单验证,保证服务端不会出异常
		// 如果是测试环境才执行
		String envirnment = Global.getConfig("vcat.environment");
		if(envirnment.equals("prod")){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String buyerId = params.getString("buyerId");
        String orderId = params.getString("orderId");
        orderService.checkShippingEarning(buyerId,orderId,5);
        return "成功" + buyerId + "  " + orderId;
    }
}
