package com.vcat.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vcat.api.exception.ApiException;
import com.vcat.api.service.mq.CancelOrderReceive;
import com.vcat.api.service.mq.ReviewReceive;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.lock.DistLockHelper;
import com.vcat.common.payment.AlipayUtils;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.common.sms.SmsClient;
import com.vcat.common.thirdparty.WeixinClient;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.dao.OrderDao;
import com.vcat.module.ec.dao.ProductTopicDao;
import com.vcat.module.ec.dto.*;
import com.vcat.module.ec.entity.*;
import com.vcat.module.json.order.ChangeOrderAddressRequest;
import com.vcat.module.json.order.MergePayRequest;
import com.vcat.module.json.order.SellerCancelOrderRequest;
import org.apache.log4j.Logger;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class OrderService extends CrudService<Order> {
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private ProductTopicDao productTopicDao;
	@Autowired
	private ShopProductService shopProductService;
	@Override
	protected CrudDao<Order> getDao() {
		return orderDao;
	}

	@Autowired
	private OrderItemService orderItemService;
	@Autowired
	private OrderReserveService orderReserveService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ProductItemService productItemService;
	@Autowired
	private ProductService productService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private PaymentLogService paymentLogService;
	@Autowired
	private ShopFundService shopFundService;
	@Autowired
	private OrderLogService orderLogService;
	@Autowired
	private RefundService refundService;
	@Autowired
	private CartService cartService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private CouponFundService couponFundService;
	@Autowired
	private CouponOperLogService couponOperLogService;
	@Autowired
	private FreightConfigService         freightConfigService;
	@Autowired
	private ProductReconciliationService reconciliationService;
	@Autowired
	private ShopInfoService              shopInfoService;
	@Autowired
	private ActivityService              activityService;
	@Autowired
	private GroupBuySponsorService       groupBuySponsorService;
	@Autowired
	private GroupBuyCustomerService      groupBuyCustomerService;
	@Autowired
	private FundOperLogService           fundOperLogService;
    @Autowired
    private ReviewReceive                reviewReceive;
	@Autowired
	private CancelOrderReceive           cancelOrderReceive;
	@Autowired
	private GroupBuyService groupBuyService;
	@Autowired
	private CustomerAddressService customerAddressService;

	private static Logger log = Logger.getLogger(OrderService.class);
	// 根据店铺id和订单类型查询订单数量
	public int countByShopId(String shopId, int orderType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("shopId", shopId);
		map.put("orderType", orderType);
		return orderDao.countByShopId(map);
	}

	// 根据店铺id和订单类型查询订单
	public List<OrderDto> getOrderList(String shopId, Pager page, int orderType) {
		Map<String, Object> map = new HashMap<>();
		map.put("shopId", shopId);
		map.put("orderType", orderType);
		map.put("page", page);
		return orderDao.getOrderList(map);
	}

	@Transactional(readOnly = false)
	public void sellerDelete(String orderId) {
		orderDao.sellerDelete(orderId);
	}

	// 获取订单详情 (原来的)
	public OrderDto getOrder(OrderDto entity, String shopId) {
		Map<String, Object> map = new HashMap<>();
		map.put("shopId", shopId);
		map.put("orderId", entity.getId());
		map.put("orderNumber", entity.getOrderNum());
		Pager page = new Pager();
		page.setPageNo(1);
		page.setPageSize(1);
		page.doPage();
		map.put("page", page);
		List<OrderDto> list = orderDao.getOrderList(map);
		if (null != list && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 生成订单号,订单号的组成 两位年+两位月+两位日+两位小时
	 *
	 * @return
	 */
	private static String buildOrderid() {
		Date createDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuilder out = new StringBuilder(IdGen.getRandomNumber(3));
		out.append(dateFormat.format(createDate)).append(
				IdGen.getRandomNumber(2));
		return out.toString();
	}

	// 从新生成支付单号
	@Transactional(readOnly = false)
	public Payment autoPayment(String orderId) {
		Payment payment = new Payment();
		payment.preInsert();
		payment.setPaymentNo(buildOrderid());
		paymentService.insert(payment);
		orderDao.paymentedByOrderId(orderId, payment.getId());
		return payment;
	}

	// 确认订单
	@Transactional(readOnly = false)
	public Map<String, Object> checkOrders(Customer buyer,
										   CustomerAddress customerAddress, Map<String, List<OrderItemDto>> map,
										   Payment payment, Invoice invoice,
										   String useCoupon,
										   String shopInfoId,
										   String groupBuySponsorId,
										   String groupBuyId,
										   String note
										   ) {
		Map<String, Object> resultMap = new HashMap<>();
		// 计算总未付款金额
		BigDecimal orderPrice = BigDecimal.ZERO;
		// 计算总快递费付款金额
		BigDecimal totalFreightPrice = BigDecimal.ZERO;
		// 计算总猫币金额
		BigDecimal totalcouponPrice = BigDecimal.ZERO;
		// 买家猫币金额
		BigDecimal couponFundPrice = BigDecimal.ZERO;
		// 获取我的猫币金额
		Shop bshop = new Shop();
		bshop.setId(buyer.getId());
		CouponFund couponFund = new CouponFund();
		couponFund.setShop(bshop);
		CouponFund conFund = couponFundService.get(couponFund);
		if (conFund != null) {
			couponFundPrice = conFund.getAvailableFund();
		}
		Map<String, Integer> allCartCount = new HashMap<>();
		for (String distributionId : map.keySet()) {
			// 计算快递费
			BigDecimal freightPrice = BigDecimal.ZERO;
			// 所有商品购卷金额
			BigDecimal totalProductcouponPrice = BigDecimal.ZERO;
			List<OrderItemDto> list = map.get(distributionId);
			// 计算快递费
			BigDecimal freight = getFreight(buyer.getId(), list);
			if (freight != null) {
				freightPrice = freightPrice.add(freight);
			}
			// 商品项数量和优惠规则信息
			List<PromotionDto> promotionList = new ArrayList<>();
			// 计算商品的总价格
			BigDecimal totalPrice = BigDecimal.ZERO;
			for (OrderItemDto item : list) {
				String checkOrderType = item.getOrderItemType();
				Map<String, Object> productMap = getProductItem(item);
				ProductItem productItem = (ProductItem) productMap.get("productItem");
				if (productItem == null) {
					logger.debug("下单失败，商品项不存在或库存不足："+item.getProductItemId());
					return productMap;
				}
				// Code.Ai(下单成功后,如果是拿样商品,默认代理此商品) 代理商品
				if (ApiConstants.SUPER.equals(checkOrderType) 			// 拿样
						|| ApiConstants.RESERVE.equals(checkOrderType) 	// 预售
						|| ApiConstants.GROUPBUY_SELLER.equals(checkOrderType)	// 卖家开团
						) {
					// 上架商品,如果是预售商品,库存默认为0
					productService.operateProduct(buyer.getId(), productItem
							.getProduct().getId(), "up");
				}
				String subString = productItem.getProduct().getName();

				//虚拟物品只能店主购买
				Boolean virtualProduct = productItem.getProduct().getVirtualProduct();
				boolean isSeller = false;
				List<CustomerRole> customerRoles = customerService.get(buyer.getId()).getRoleList();
				for(CustomerRole cr : customerRoles){
					if(cr.getRole().getRoleName() == "seller"){
						isSeller = true;
					}
				}
				if(virtualProduct !=null && virtualProduct && isSeller){//卖家购买
					resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
					resultMap.put(ApiConstants.MSG, subString+" 该商品你不能购买，请下载V猫小店");
					return resultMap;
				}
				// TODO 删除猫币抵扣商品
				// 如果已经参加过同一个团购,则不能再次参见
				//if(ApiConstants.GROUPBUY.equals(checkOrderType)){
				//	int count = orderDao.countGroupBuyProduct(productItem.getId(),buyer.getId(),checkOrderType);
				//	if(count>0||item.getQuantity()>1){
				//		resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				//		resultMap.put(ApiConstants.MSG,  (subString+"限购1件！"));
				//		return resultMap;
				//	}
				//}

				//应用规则，判断此商品是否已经购买过，是否可以购买
				String ruleType = checkOrderType;
				boolean isLimit = orderDao.isProductInLimit(item.getProductItemId());
				if(isLimit){
					ruleType = ApiConstants.TOPIC;//专题
					if(allCartCount.get(productItem.getId()) == null){
						allCartCount.put(productItem.getId(), item.getQuantity());
					}else{
						Integer preCount = allCartCount.get(productItem.getId());
						allCartCount.put(productItem.getId(), item.getQuantity()+preCount);
					}
				}
				Map<String, Object> rule = getProductLimit(productItem.getId());
				if(rule != null){
					int result = applyLimit(rule, productItem.getId(), checkOrderType, ruleType, buyer.getId(),
							ruleType.equalsIgnoreCase(ApiConstants.TOPIC)?allCartCount.get(productItem.getId()):item.getQuantity());
					if(result == 1){//卖家购买
						resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
						resultMap.put(ApiConstants.MSG, subString+" 该商品你不能购买，请下载V猫小店");
						return resultMap;
					}else if(result == 2){//买家购买
						resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
						resultMap.put(ApiConstants.MSG, subString+" 该商品你不能购买");
						return resultMap;
					}else if(result == 3){//超过限制
						int interval = (Integer)rule.get("interval");
						int times = (Integer)rule.get("times");
						resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
						resultMap.put(ApiConstants.MSG, subString+(interval == -1 ? "":+(interval+1)+"天内")+"限购"+times+"件！");
						return resultMap;
					}
				}
				// 设置订单项价格 (已经根据买家身份修改过价格)
				item.setItemPrice(productItem.getRetailPrice());
				// 计算总价
				totalPrice = totalPrice.add(item.getItemPrice().multiply(
						new BigDecimal(item.getQuantity())));
				//Code.Ai ( 如果此商品有优惠活动,则根据购买的商品数增加相应的数量 )
                // 2016.04.01
                if(ApiConstants.SUPER.equals(checkOrderType) || ApiConstants.RESERVE.equals(checkOrderType) ||
                        ApiConstants.VCATLEROY.equals(checkOrderType)){ // 普通订单不参加优惠
					Map<String,Object> ruleMap = productItemService.getRuleByProductItemId(item.getProductItemId());
					if(ruleMap != null && ruleMap.size() != 0 && (item.getQuantity() >= (Integer)ruleMap.get("buyCount"))) {
						//增加客户的商品量
						//item.setQuantity(item.getQuantity() + (item.getQuantity() / (Integer) ruleMap.get("buyCount")) * (Integer) ruleMap.get("freeCount"));
                        // 根据优惠算出赠送的个数
                        item.setPromotionQuantity((item.getQuantity() / (Integer) ruleMap.get("buyCount")) * (Integer) ruleMap.get("freeCount"));
						PromotionDto promotionDto = new PromotionDto();
						promotionDto.setBuyCount((Integer) ruleMap.get("buyCount"));
						promotionDto.setFreeCount((Integer) ruleMap.get("freeCount"));
						promotionDto.setQuantity(item.getQuantity());
						promotionDto.setProductItemId(item.getProductItemId());
						promotionList.add(promotionDto);
					}
				}
				// 更新每个产品项的库存
				logger.debug("更新每个产品项的库存:"+productItem.getId()+"全部数量:"+(item.getQuantity()+item.getPromotionQuantity()));
				productItemService.updateQuantiy(productItem.getId(),
						item.getQuantity() + item.getPromotionQuantity(), checkOrderType);
				resultMap.put("checkOrderType",checkOrderType);
				resultMap.remove("productItem");
			}
			//插入订单
			Order order = insertOrder(buyer, customerAddress, payment,
					invoice, distributionId, freightPrice,
					totalProductcouponPrice, totalPrice,note);
			// 计算总金额
			totalFreightPrice = totalFreightPrice.add(freightPrice);
			orderPrice = orderPrice.add(totalPrice);
			totalcouponPrice = totalcouponPrice.add(totalProductcouponPrice);

			for (OrderItemDto item : list) {
				String checkOrderType = item.getOrderItemType();
				item.setId(IdGen.uuid());
				item.setOrderId(order.getId());
				if(ApiConstants.RESERVE.equals(checkOrderType) && productService.isReserveProduct(item.getProductItemId())) {
					item.setOrderItemType(ApiConstants.SUPER_RESERVE); //6
					item.setProductRecommendType("RESERVE");// 设置预售进货的订单项为 "6"
				}
				else if(ApiConstants.VCATLEROY.equals(checkOrderType)){
					item.setOrderItemType(ApiConstants.VCATLEROY); //设置V猫庄园的订单项类型为 "7"
				}
				orderItemService.insertItem(item);
				insertOrderTopic(item);

				// 插入团购信息
				orderItemService.insertGroupBuyInfo(groupBuySponsorId, groupBuyId, item,checkOrderType,buyer);

                // 修改返回前端的商品数量
                item.setQuantity(item.getQuantity()+item.getPromotionQuantity());
				resultMap.put("item",item);
			}

			// 添加取消未支付订单的消息
			cancelOrderReceive.addCancelGroupBuyJob(order.getId());
		}
		resultMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		resultMap.put("itemPrice", orderPrice);
		resultMap.put("itemfreightPrice", totalFreightPrice);
		resultMap.put("itemcouponPrice", totalcouponPrice);
		resultMap.put("shopCouponFund", couponFundPrice);

		// 如果是庄园商品,买家以前购买过庄园商品,当再次购买其他庄园商品时直接下单,则添加庄园主信息
		//Map<String, Object> isVirtualProduct = productService.getProductsByPaymentId(payment.getId());
		//if(isVirtualProduct != null && !StringUtils.isBlank(shopInfoId)){
		//	ShopInfoDto shopInfoDto = new ShopInfoDto();
		//	// 根据shopId productId查找具体的庄园主信息
		//	shopInfoDto.setShopId(buyer.getId());
		//	shopInfoDto.setProductId((String)isVirtualProduct.get("productId"));
		//	List<ShopInfoDto> shopInfoDtoList = shopInfoService.getShopInfo(shopInfoDto);
		//	if(shopInfoDtoList == null || shopInfoDtoList.size() == 0){
		//		// 增加新庄园主信息
		//		ShopInfoDto shopInfo = new ShopInfoDto();
		//		shopInfo.setId(shopInfoId);
		//		List<ShopInfoDto> oldShopInfoList = shopInfoService.getShopInfo(shopInfo);
		//		if(oldShopInfoList != null && oldShopInfoList.size() == 1){
		//			shopInfoService.addLeroyerInfo(oldShopInfoList.get(0).getWechatName(),
		//					oldShopInfoList.get(0).getQRCode(),
		//					oldShopInfoList.get(0).getRealName(),
		//					oldShopInfoList.get(0).getFarmName(),
		//					oldShopInfoList.get(0).getShopId(),
		//					(String) isVirtualProduct.get("productId"));
		//		}
		//	}
		//}
		return resultMap;
	}

	/**
	 * 插入活动关联表，用于查询订单是否是活动购买
	 */
	@Transactional(readOnly = false)
	private void insertOrderTopic(OrderItemDto item){
		if(!StringUtils.isNullBlank(item.getTopicId())) {
			productTopicDao.insertOrderTopic(IdGen.uuid(), item.getTopicId(), item.getId());
		}
	}

	@Transactional(readOnly = false)
	private Order insertOrder(Customer buyer,
							  CustomerAddress customerAddress, Payment payment, Invoice invoice,
							  String distributionId, BigDecimal freightPrice,
							  BigDecimal totalProductcouponPrice, BigDecimal totalPrice,String note) {
		// 插入订单表
		Order order = new Order();
		order.preInsert();
		order.setBuyer(buyer);
		order.setCustomerAddress(customerAddress);
		order.setOrderNumber(buildOrderid());
		order.setTotalPrice(totalPrice);
		// 如果使用优惠劵，订单添加优惠劵金额
		order.setFreightPrice(freightPrice);
		order.setTotalCoupon(totalProductcouponPrice);
		order.setPayment(payment);
		// 添加发票信息
		if (invoice != null) {
			invoice.preInsert();
			invoiceService.insert(invoice);
		}
		order.setInvoice(invoice);
		order.setNote(note);
		orderDao.insert(order);
		logger.debug("订单生成成功:" + order.getOrderNumber() + "总金额:" + totalPrice);
		//插入订单 地址表
		orderDao.insertOrderAddress(order.getId());
		return order;
	}

	private Map<String, Object> getProductItem (OrderItemDto item) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String shopId = item.getShopId();
		String checkOrderType = item.getOrderItemType();
		// 如果小于库存，下单失败
		ProductItem productItem = null;
		// 普通订单 判断商品是否下架
		boolean isTopic = false;
		if(productTopicDao.isProductInTopic(item.getProductItemId())){
			isTopic = true;
		}

		if (ApiConstants.NORMAL.equals(checkOrderType) ||
				ApiConstants.GROUPBUY_BUYER.equals(checkOrderType)) {
			if(isTopic){
				productItem = productItemService.get(item.getProductItemId());
			}else{
				productItem = productItemService.getItem(item.getProductItemId(),
						shopId);
			}

			if (productItem == null) {
				resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				resultMap.put(ApiConstants.MSG, "商品已下架！");
				return resultMap;
			}
			if (!isTopic && productItem.getProduct().getRetailUsable() != 1) {
				resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				resultMap.put(ApiConstants.MSG, (productItem.getProduct()
						.getName()) + " 商品已下架！");
				return resultMap;
			}
		}
		if (ApiConstants.POSTAGE.equals(checkOrderType)) {
			productItem = productItemService.get(item.getProductItemId());
			if (productItem == null) {
				resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				resultMap.put(ApiConstants.MSG, "请选择商品规格！");
				return resultMap;
			}
		}

		if (ApiConstants.SUPER.equals(checkOrderType) || ApiConstants.GROUPBUY_SELLER.equals(checkOrderType)) {
			String productItemId = item.getProductItemId();
			productItem = productItemService.get(productItemId);
			if (productItem == null) {
				resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				resultMap.put(ApiConstants.MSG, "请选择商品规格！");
				return resultMap;
			}
			if (!isTopic && productItem.getProduct().getRetailUsable() != 1) {
				resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				resultMap.put(ApiConstants.MSG, (productItem.getProduct().getName()) + " 商品已下架！");
				return resultMap;
			}
			//如果是预售商品
			if(productService.isReserveProduct(item.getProductItemId()) && ApiConstants.NORMAL.equals(checkOrderType)){
				//修改库存为小店中的库存
				//获取小店中的库存
				int inventory = shopProductService.getInventory(shopId,productItemService.get(item.getProductItemId()).getProduct().getId());
				productItem.setInventory(inventory);
			}
			/**
			 * 修改商品项价格
			 */
			Shop shop = shopService.get(shopId);
			if(shop.getAdvancedShop() == 1){ //VIP
				productItem.setRetailPrice(productItem.getRetailPrice().subtract(productItem.getSaleEarning()).subtract(productItem.getBonusEarning()));
			}else{
				productItem.setRetailPrice(productItem.getRetailPrice().subtract(productItem.getSaleEarning()));
			}
		}
		if(((Integer.parseInt(ApiConstants.LEROY) + 1)+"").equals(checkOrderType)){
			productItem = productItemService.get(item.getProductItemId());
			if (productItem == null) {
				resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				resultMap.put(ApiConstants.MSG, "请选择商品规格！");
				return resultMap;
			}
			if (!isTopic && productItem.getProduct().getRetailUsable() != 1) {
				resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				resultMap.put(ApiConstants.MSG, (productItem.getProduct()
						.getName()) + " 商品已下架！");
				return resultMap;
			}
		}

		if (productItem == null || productItem.getInventory() < item.getQuantity()) {
			resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
			resultMap.put(ApiConstants.MSG, ApiMsgConstants.QUANTIYT_NOT_ENOUGH
					+ ":"
					+ (productItem == null ? "" : productItem.getInventory()));
			return resultMap;
		}
		resultMap.put("productItem", productItem);
		return resultMap;
	}

	// 分单
	@Transactional(readOnly = false)
	public Map<String, Object> checkOrders(Customer buyer, JSONArray list,
										   CustomerAddress customerAddress, Invoice invoice,
										   String shopInfoId,
										   String groupBuySponsorId,
										   String groupBuyId,
										   String note
	) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 生成唯一的支付号
		Payment payment = new Payment();
		payment.preInsert();
		payment.setPaymentNo(buildOrderid());
		paymentService.insert(payment);
		// 计算总金额
		BigDecimal paymentTotalPrice = BigDecimal.ZERO;
		BigDecimal paymentFreightPrice = BigDecimal.ZERO;
		BigDecimal paymentCouponPrice = BigDecimal.ZERO;
		String useCoupon = ApiConstants.NO;
		JSONArray cartList  = new JSONArray();
		// 所有购物车商品合起来
		for (Object object : list) {
			JSONObject json = (JSONObject) object;
			String shopId = json.getString("shopId");
			String productType = json.get("productType").toString();
			JSONArray elist = json.getJSONArray("cartList");
			for(Object obj :elist){
				JSONObject gson = (JSONObject) obj;
				//String productItemId = gson.getString("productItemId");
				//if(productService.isVirtualProduct(productItemId) && StringUtils.isBlank(shopInfoId)){
				//	throw new ApiException(ApiMsgConstants.NOFARM_ERROR_MSG);
				//}
				//else if(productService.isVirtualProduct(productItemId) && !StringUtils.isBlank(shopInfoId)){
				//	ShopInfoDto shopInfoDto = new ShopInfoDto();
				//	shopInfoDto.setId(shopInfoId);
				//	shopInfoDto = shopInfoService.getShopInfo(shopInfoDto).get(0);
				//	if(shopInfoDto == null){
				//		throw new ApiException(ApiMsgConstants.NOFARM_ERROR_MSG);
				//	}
				//}
				GroupBuySponsorDto groupBuySponsorDto = new GroupBuySponsorDto();
				groupBuySponsorDto.setId(groupBuySponsorId);
				groupBuySponsorDto = groupBuySponsorService.getGroupBuySponsor(groupBuySponsorDto);
				// 如果此拼团已经锁定 则不能再下单
				if(groupBuySponsorDto != null && groupBuySponsorDto.getLocked()){
					throw new ApiException(ApiMsgConstants.GROUPBUY_LOCKED);
				}
				if((ApiConstants.NORMAL.equals(productType)) || (ApiConstants.GROUPBUY_BUYER.equals(productType))){
					gson.put("shopId", shopId);
				}else {
					gson.put("shopId","");
				}
			}
			cartList.addAll(elist);
		}
		Map<String, List<OrderItemDto>> map = new HashMap<>();
		// 获取每个商品的品牌,对商品按照品牌分类
		map = separateOrder(cartList, map,buyer);
		//if(true){
		//	resultMap.put("map",map);
		//	return resultMap;
		//}
		// 如果有一个子订单失败，停止下单并返回错误
		resultMap = checkOrders(buyer,              // 买家
				customerAddress,    // 买家地址
				map,                // 根据供货商分单
				payment,            // 支付单
				invoice,            // 发票
				useCoupon,           // 优惠券
				"",
				groupBuySponsorId,
				groupBuyId,
				note
		);
		//if(true){
		//	return resultMap;
		//}

		if (ApiMsgConstants.FAILED_CODE == (int) resultMap
				.get(ApiConstants.CODE)) {
			throw new ApiException(resultMap.get(ApiConstants.MSG)
					.toString());
		} else
			// 算出总价格
			if (ApiMsgConstants.SUCCESS_CODE == (int) resultMap
					.get(ApiConstants.CODE)) {
				paymentTotalPrice = paymentTotalPrice
						.add((BigDecimal) resultMap.get("itemPrice"));
				paymentFreightPrice = paymentFreightPrice
						.add((BigDecimal) resultMap.get("itemfreightPrice"));
				paymentCouponPrice = paymentCouponPrice
						.add((BigDecimal) resultMap.get("itemcouponPrice"));
				// 删除购物车里面的商品
				List<String> cartIdlist = new ArrayList<>();
				for (Object obj : cartList) {
					JSONObject gson = (JSONObject) obj;
					OrderItemDto item = JSON.toJavaObject(gson,
							OrderItemDto.class);
					if (!StringUtils.isBlank(item.getCartId())) {
						cartIdlist.add(item.getCartId());
					}
				}
				if (cartIdlist.size() != 0) {
					cartService.deleteCarts(cartIdlist);
				}
			}

		resultMap.put("totalPrice", paymentTotalPrice.add(paymentFreightPrice)
				.subtract(paymentCouponPrice));
		resultMap.put("totalFreightPrice", paymentFreightPrice);
		resultMap.put("totalCouponPrice", paymentCouponPrice);
		resultMap.remove("itemPrice");
		resultMap.remove("itemcouponPrice");
		resultMap.remove("itemfreightPrice");
		resultMap.put("paymentId", payment.getId());
		return resultMap;
	}

	private Map<String, List<OrderItemDto>> separateOrder(JSONArray cartList,
														  Map<String, List<OrderItemDto>> map, Customer buyer) {
		for (Object obj : cartList) {
			JSONObject gson = (JSONObject) obj;
			OrderItemDto item = JSON.toJavaObject(gson, OrderItemDto.class);
			if(item.getProductType().equals(ApiConstants.LEROY)){ //传进来的庄园商品type为6 庄园商品订单类型为7
				item.setOrderItemType(Integer.parseInt(item.getProductType()) + 1 + "");
			}else item.setOrderItemType(item.getProductType());
			//设置商品类型
			List<Map<String,String>> typeList = productItemService.getProductRecommendType(item.getProductItemId());
			for(Map<String,String> typeMap : typeList){
				if(typeMap.get("type").equals("RESERVE")){
					item.setProductRecommendType(typeMap.get("type"));
					item.setRecommendId(typeMap.get("recommendId"));
					break;
				}else {
					item.setProductRecommendType("");
					item.setRecommendId("");
				}
			}
			//如果是拿样订单，设置拿样的店铺号为买家号
			if(ApiConstants.SUPER.equals(item.getProductType()) || ApiConstants.RESERVE.equals(item.getProductType()) ||
					ApiConstants.LEROY.equals(item.getProductType()) || ApiConstants.GROUPBUY_SELLER.equals(item.getProductType())){
				item.setShopId(buyer.getId());
			}

			String productItemId = item.getProductItemId();
			String distributionId = productItemService.getDistributionId(productItemId);

			if (map.containsKey(distributionId)) {
				List<OrderItemDto> orderItems = map.get(distributionId);
				orderItems.add(item);
			} else {
				List<OrderItemDto> orderItems = new ArrayList<>();
				orderItems.add(item);
				map.put(distributionId, orderItems);
			}
		}
		return map;
	}

	// 批量支付更新支付状态，及插入支付记录表
	@Transactional(readOnly = false)
	public void paymented(Payment payment, String openId) {
		paymentService.update(payment);
		// 更新支付状态
		orderDao.paymented(payment.getId());
		// 记录支付日志
		PaymentLog log = new PaymentLog();
		log.preInsert();
		log.setPayment(payment);
		log.setNote("支付单[" + payment.getPaymentNo() + "]支付成功！");
		// 第三方支付类型
		log.setGatewayCode(payment.getGateway().getCode());
		log.setTransactionSuccess(ApiConstants.YES);
		paymentLogService.insertBypayment(log);
		logger.debug("支付单[" + payment + "]支付成功！");
		// 如果是微信支付,向微信客户端发送消息
		if ("Wechat".equalsIgnoreCase(payment.getGateway().getCode())) {
			List<Map<String, Object>> results = this.getWeixinInfoByPayment(payment);
			for(Map<String,Object> result : results) {
				if (StringUtils.isNullBlank(openId)) {
					openId = (String) result.get("openId");
				}
				logger.debug("查询getWeixinInfoByPayment:" + result);
				if (result != null && result.size() != 0 &&
						!StringUtils.isBlank(openId)) {
					WeixinClient.sendOrderSuccessMsg(openId,
							(String) result.get("productName"),
							(String) result.get("id"),
							String.valueOf(result.get("price")));
				}
			}
		}
		// 获取总折扣卷金额 减去总折扣劵金额，并记录日志
		//couponLog(payment);
		// 如果是活动订单 则不增加销售等收入
		String activateId = activityService.getActivityIdByPayment(payment.getId());
		if(StringUtils.isBlank(activateId)){
			saveConfirmLog(payment);
		}else {
			logger.debug("活动订单不增加销售等收入");
		}
        //// 增加销量
		saleAndTake(payment);
		paymentedMotion(payment.getId());
		// 如果是团购订单,支付成功后即开团成功
		groupBuyService.updateGroupBuyOrderStatus(payment, openId);
	}

	//@Transactional(readOnly = false)
	//private void couponLog(Payment payment) {
	//	List<CouponOperLog> clist = orderDao.getCouponByPaymentId(payment
	//			.getId());
	//	if (clist != null && clist.size() > 0) {
	//		for (CouponOperLog clog : clist) {
	//			if (clog.getCouponFund() == null) {
	//				continue;
	//			}
	//			couponFundService.subCoupon(clog.getCouponFund().getShop()
	//					.getId(), clog.getFund());
	//			// 记录日志
	//			clog.preInsert();
	//			clog.setCouponFundId(clog.getCouponFund().getId());
    //
	//			clog.setNote("付款订单[" + clog.getOrderId() + "]完成，邀请劵消费"
	//					+ clog.getFund());
	//			clog.setType(ApiConstants.COUPON_ALL);
	//			couponOperLogService.insert(clog);
	//		}
	//	}
	//}

	@Transactional(readOnly = false)
	private void saleAndTake(Payment payment) {
		List<ProductItemDto> products = orderDao.getProductsBypaymentId(payment
				.getId());
		if (products != null && products.size() != 0) {
			for (ProductItemDto product : products) {
				String productId = product.getProductId();
				int quantity = product.getQuantity();
				productService.updateSales(productId, quantity);
			}

		}
	}

	/**
	 * 付款成功后 销售收入待确认加钱
	 * VIP或者非VIP在自己小店购买后,不增加返佣,
     * 如果是别人在VIP或者非VIP店中购买,则会返给买家销售收入
     * B级用户预售拿货后 上家马上能得到销售分红
	 * @param payment
     */
    //todo 修改为使用是否能退款判断是否马上给予返现
	@Deprecated //现在使用saveConfirmLog
    @Transactional(readOnly = false)
	private void saveLog(Payment payment) {
		// 销售待确认收入(返佣)
		List<OrderDto> orderDtoList = this.getOrderInfoByPayment(payment);
		for(OrderDto orderDto : orderDtoList){
			OrderDto order = getOrder(orderDto.getId(),
					orderDto.getBuyerId(),
					3); // 2:未支付 3:已支付未发货 4:已支付已发货 5:已确认收货
			// 查询出此订单中普通订单项或者团购订单销售项返佣
			logger.debug("处理order:"+order);
			List<ShopFundDto> saleHoldFundList = shopFundService.getSaleHoldFund(order.getId()); // 商品的返佣金额(-)
			logger.debug("saleHoldFundList:"+saleHoldFundList);
			// 此订单中所有分红订单项列表
			List<FundOperSaleLog> saleHoldFundLogList = shopFundService.getSaleHoldFundLog(order.getId()); // 商品的返佣金额(+)
			logger.debug("saleHoldFundLogList:"+saleHoldFundLogList);
			List<ShopFund> bonusHoldFundList = shopFundService.getBonusHoldFund(order.getId()); //上家ID和销售分红金额(-)
			logger.debug("bonusHoldFundList:"+bonusHoldFundList);
			List<FundOperBonusLog> bonusHoldFundLogList = shopFundService.getBonusHoldFundLog(order.getId()); // 上家ID和销售分红金额(+)
			logger.debug("bonusHoldFundLogList:"+bonusHoldFundLogList);
			if (saleHoldFundList != null && saleHoldFundList.size() != 0
					&& saleHoldFundLogList != null && saleHoldFundLogList.size() != 0) {
				ProductItemDto productItemDto;
				int unableRefundCount = 0;
				for (int i = 0; i < saleHoldFundList.size(); i++) {
					productItemDto = productItemService.getProductItemDto(saleHoldFundList.get(i).getOrderItemId());
					logger.debug("处理productItemDto："+productItemDto);
					// 返佣
					ShopFundDto fund = saleHoldFundList.get(i);
					logger.debug("得到saleHoldFund："+fund);
					FundOperSaleLog saleLog = saleHoldFundLogList.get(i);
					logger.debug("得到FundOperSaleLog："+saleLog);
					// 销售分红
					ShopFund bonusFund = bonusHoldFundList.get(i);
					logger.debug("得到bonusFund："+bonusFund);
					FundOperBonusLog bonusdlog = bonusHoldFundLogList.get(i);
					logger.debug("得到FundOperBonusLog："+bonusdlog);
					// 卖家店铺
					Shop shop = shopService.get(fund.getId());
					logger.debug("操作的店铺是："+shop);
					/**
					 * 如果是普通订单或买家开团或买家参团订单,则表示买家在店主的小店中购买商品,则给卖家返佣
					 * 如果是拿样订单(orderItemType = 2 || orderItemType = 6 || orderItemType = 8),则表示店主在商城中购买,则不给返佣,因为店主购买时是零售价-返佣(-销售分红)
					 * 增加卖家店铺的返佣收入
					 */

					/**
					 * 需求变更:不可退款商品支付成功后增加待确认收入,后台订单已发货后,变为可提现
					 */
					if((productItemDto.getProductType().equals(ApiConstants.NORMAL) || productItemDto.getProductType().equals(ApiConstants.GROUPBUY_BUYER))
							&& saleLog.getShopFundId() != null && saleLog.getFund() != null){
						logger.debug("处理普通订单和团购商品项");
						// 如果是不能退货的商品且不是拼团商品,支付成功后增加可提现收入(不可退货的拼团商品只有拼团成功后才进入可提现流程)
						if(!productItemDto.getCanRefund() && !productItemDto.getProductType().equals(ApiConstants.GROUPBUY_BUYER)){
							if(this.isSuperReserve(payment.getId())){
								break;
							}
							logger.debug("处理非团购的不可退款商品项");
							// 增加店铺可提现返佣资金
							shopFundService.addSaleAvaiableFund(fund.getId(),fund.getSaleHoldFund());
							logger.debug("成功增加Sale可提现资金["+fund.getId()+"@"+fund.getSaleHoldFund()+"]");
							// 根据店铺Id获取所有的资金详情
							ShopFund upFund = shopFundService.get(fund.getId());
							logger.debug("得到店铺资金:"+upFund);
							if (upFund == null) {
								break;
							}
							// 是普通订单,而且是vip卖家才增加销售分红(增加到销售返佣金额中)
							if(shop.getAdvancedShop() == 1){
								// 直接增加可提现销售分红到返佣资金中
								shopFundService.addSaleAvaiableFund(fund.getId(),bonusFund.getBonusHoldFund());
								// 设置要操作的资金总数 = 返佣 + 销售分红
								saleLog.setFund(saleLog.getFund().add(bonusdlog.getFund()));
								//  操作后的剩余可提现金额 = 原来的可提现金额 + 销售分红
								saleLog.setRemainFund(upFund.getSaleAvailableFund().add(bonusdlog.getFund()));
								logger.debug("高级店铺成功增加Sale可提现资金(分红)["+fund.getId()+"@"+bonusFund.getBonusHoldFund()+"]");
							}else{
								saleLog.setFund(saleLog.getFund());
								saleLog.setRemainFund(upFund.getSaleAvailableFund());
							}
							if(saleLog.getFund() != null && saleLog.getFund().compareTo(new BigDecimal(0)) == 1){
								// 插入日志
								saleLog.preInsert();
								// 1为接口操作，收入
								saleLog.setFundType(FundOperLog.NORMAL_INCOME);
								saleLog.setNote("不能退货商品支付单[" + payment.getPaymentNo() + "]支付完成，增加销售可提现收入[" + saleLog.getFund() + "]");
								saleLog.setFundFieldType(new FundFieldType(FundFieldType.SALE_AVAILABLE_FUND));
								fundOperLogService.insert(saleLog);
								logger.debug("插入saleLog:"+saleLog);
								Customer buyer = customerService.get(orderDto.getBuyerId());
								MessageEarning earning = new MessageEarning();
								Shop p = new Shop();
								p.setId(shop.getId());
								earning.setShop(p);
								earning.setType(MessageEarning.TYPE_SALE); //销售返佣类型
								earning.setConsumer(buyer == null ? "" : buyer.getUserName());
								earning.setEarning(saleLog.getFund());
								earning.setOrderNumber(order.getOrderNum());
								earning.setProductName(productItemDto.getProductName());
								messageService.pushEarningMsgTask(earning);
								logger.debug("推送MessageEarning:"+earning);
								unableRefundCount++;
							}
						}
						// 如果商品不可退款,且是拼团订单,且在拼团中时,增加待确认收入
						// 如果商品可退款 增加待确认收入
						else if((!productItemDto.getCanRefund() && productItemDto.getProductType().equals(ApiConstants.GROUPBUY_BUYER))
								|| productItemDto.getCanRefund()) {
							if(this.isSuperReserve(payment.getId())){
								break;
							}
							logger.debug("处理团购商品项或者是可退款的其他商品项");
							shopFundService.refundSuccess(fund);
							logger.debug("增加holdSaleFund:"+fund);
							// 根据店铺Id获取所有的资金详情
							ShopFund upFund = shopFundService.get(fund.getId());
							logger.debug("得到店铺资金:"+upFund);
							if (upFund == null) {
								break;
							}
							// 将返佣和销售分红都添加到返佣金额中
							if(shop.getAdvancedShop() == 1){
								// bonusFund为负数,此处为在原来的基础上加上待确认销售分红收入
								ShopFundDto bonus = new ShopFundDto();
								bonus.setId(shop.getId());
								bonus.setSaleHoldFund(bonusFund.getBonusHoldFund());
								// 再增加销售分红到销售返佣中
								shopFundService.refundSuccess(bonus);
								saleLog.setFund(saleLog.getFund().add(bonusdlog.getFund()));
								saleLog.setRemainFund(upFund.getSaleHoldFund().add(bonusdlog.getFund()));
								logger.debug("高级店铺成功增加SaleHold(分红)["+fund.getId()+"@"+bonusFund.getBonusHoldFund()+"]");
							}else{
								saleLog.setFund(saleLog.getFund());
								saleLog.setRemainFund(upFund.getSaleHoldFund());
							}
							saleLog.preInsert();
							saleLog.setFundType(FundOperLog.NORMAL_INCOME);
							saleLog.setNote("支付单[" + payment.getPaymentNo() + "]支付完成，订单号["+order.getOrderNum()+"]，增加销售待确认收入[" + saleLog.getFund() + "]");
							saleLog.setFundFieldType(new FundFieldType(FundFieldType.SALE_HOLD_FUND));
							logger.debug("插入saleHoldLog:"+saleLog);
							fundOperLogService.insert(saleLog);
						}
					}

					/**
					 * 如果卖家是特约小店
					 * 1. 给上级VIP销售分红
					 */
					if(shop.getAdvancedShop() != 1 && bonusdlog.getShopFundId() != null && bonusdlog.getFund() != null){
						logger.debug("给上级分红处理");
						// 获取上级店铺资金
						ShopFund parentUpFund = shopFundService.get(bonusFund != null ? bonusFund.getId() : null);
						if (parentUpFund == null) {
							break;
						}
						//如果不是店主预售拿货,则增加待确认收入, 否则,增加可提现收入
						if (bonusFund != null && !StringUtils.isBlank(bonusFund.getId())
								&& (productItemDto.getCanRefund() || (!productItemDto.getCanRefund()
								&& (productItemDto.getProductType().equals(ApiConstants.GROUPBUY_BUYER)
								|| productItemDto.getProductType().equals(ApiConstants.GROUPBUY_SELLER))))) {
							// bonusFund为负数,此处为在原来的基础上加上待确认销售分红收入
							shopFundService.addBonusBoldFund(bonusFund);
							// 操作后的资金余额 = 操作前的金额 + (-) 操作金额
							bonusdlog.setRemainFund(parentUpFund.getBonusHoldFund().add(bonusdlog.getFund()));
							bonusdlog.preInsert();
							// 1为接口操作，收入
							bonusdlog.setFundType(FundOperLog.NORMAL_INCOME);
							bonusdlog.setNote("下级支付单[" + payment.getPaymentNo()	+ "]支付完成，上级销售分红待确认资金增加[" + bonusdlog.getFund() + "]");
							bonusdlog.setFundFieldType(new FundFieldType(FundFieldType.BONUS_HOLD_FUND));
						}else if(bonusFund != null && !StringUtils.isBlank(bonusFund.getId())
								&& !productItemDto.getCanRefund()
								&& !productItemDto.getProductType().equals(ApiConstants.GROUPBUY_BUYER)
								&& !productItemDto.getProductType().equals(ApiConstants.GROUPBUY_SELLER)){
							// 直接将待确认收入变为可提现收入
							shopFundService.addBonusAvaiableFundOfReserve(bonusFund.getId(),bonusFund.getBonusHoldFund());
							// 设置操作后的资金余额 = 操作前的金额 + (-) 操作后的金额
							bonusdlog.setRemainFund(parentUpFund.getBonusAvailableFund().add(bonusdlog.getFund()));
							bonusdlog.preInsert();
							// 1为接口操作，收入
							bonusdlog.setFundType(FundOperLog.NORMAL_INCOME);
							// 如果是预售拿货 则直接增加上级店铺销售分红
							bonusdlog.setNote("下级不能退货商品支付单[" + payment.getPaymentNo()	+ "]支付完成，上级销售分红资金资金结算完成[" + bonusdlog.getFund() + "]");
							bonusdlog.setFundFieldType(new FundFieldType(FundFieldType.BONUS_AVAILABLE_FUND));
							// 立即给上家发送财富消息
							Customer buyer = customerService.get(shop.getId());
							MessageEarning earning = new MessageEarning();
							Shop p = new Shop();
							p.setId(bonusFund.getId());
							earning.setShop(p);
							earning.setType(MessageEarning.TYPE_BONUS); //销售分红消息类型  5
							earning.setConsumer(buyer == null ? "" : buyer.getUserName());
							earning.setEarning(bonusdlog.getFund());
							earning.setOrderNumber(order.getOrderNum());
							earning.setProductName(productItemDto.getProductName());
							messageService.pushEarningMsgTask(earning);
						}
						bonusdlog.setRelateId(shop.getId());
						fundOperLogService.insert(bonusdlog);
					}
					// 如果是VIP卖家
					// 设置为已结算(返佣和分红)
					//else if(shop.getAdvancedShop() == 1){
					//	// 不操作销售返佣和销售分红,直接将结算状态变为已结算,让后台处理团队分红
					//	orderDao.updateIsFund(order.getId());
					//}
				}
				// 此订单中全部都为不可退款订单项
				if(unableRefundCount == saleHoldFundList.size()){
					logger.debug("此订单中全部都为不可退款订单项,设置is_fund = 1");
					orderDao.updateIsFund(order.getId());
				}
			}
		}
	}

	// 买家获取订单列表
	public List<OrderDto> getBuyerOrderList(String buyerId, Pager page, String orderType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("buyerId", buyerId);
		map.put("orderType", orderType);
		map.put("page", page);
		return orderDao.getBuyerOrderList(map);
	}

	// 买家获取订单分页
	public int countByBuyerId(String buyerId, String orderType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("buyerId", buyerId);
		map.put("orderType", orderType);
		return orderDao.countByBuyerId(map);
	}

	@Transactional(readOnly = false)
	public void buyerCloseOrder(String orderId, String buyerId) {
		// 退货取消订单的库存,1为所有订单
		OrderDto ord = getOrder(orderId, buyerId, 1);
		if (ord == null || ord.getProductItems() == null
				|| ord.getProductItems().size() == 0) {
			return;
		}
		//关闭支付宝订单
		Payment oldPayment = paymentService.getByOrderId(orderId);
		PaymentLog log1 = new PaymentLog();
		log1.setPayment(oldPayment);
		log1.setTransactionSuccess("3");
		log1.setGatewayCode(ApiConstants.PAY_TYPE_ALIPAY);
		PaymentLog paymentLog = paymentLogService.getPaymentLog(log1);
		//取消支付单
		if (paymentLog != null && oldPayment != null) {
			//关闭交易
			if(AlipayUtils.closeTrade(paymentLog.getTransactionNo(), oldPayment.getPaymentNo())){
				logger.debug("支付宝关闭交易订单成功" + "支付宝交易号["
						+ paymentLog.getTransactionNo() + "],支付单号["
						+ oldPayment.getPaymentNo() + "]");
			}
		}
		// Code.Ai ( 如果订单中包含的订单项是预购订单项,则增加小店库存;如果是其他 则按原来逻辑处理)
		// 取消订单不增加库存
		//for (ProductItemDto item : ord.getProductItems()) {
		//	String checkOrderType =  orderItemService.getOrderItemType(item.getProductItemId(),orderId) + "";
		//	productItemService.updateQuantiy(item.getProductItemId(), -(item.getQuantity() + item.getPromotionQuantity()), checkOrderType);
		//}

		orderDao.cancelOrder(orderId);

		// 查看paymentId下面的所有订单是否都取消，如果取消，删除payment
		String paymentId = ord.getPaymentId();
		List<String> orderIds = orderDao.checkAllPayment(paymentId);
		if (orderIds == null || orderIds.size() == 0) {
			//paymentService.deleteById(paymentId);  TODO 取消删除，观察有问题没
		}
		// 记录日志
		OrderLog log = new OrderLog();
		log.preInsert();
		Order order = new Order();
		order.setId(orderId);
		log.setOrder(order);
		log.setOperName(buyerId);
		log.setAction("取消订单");
		log.setResult(ApiMsgConstants.SUCCESS_MSG);
		log.setNote("取消订单" + ord.getOrderNum() + "成功");
		orderLogService.insert(log);
	}

	/**
	 * 取消全额抵扣订单
	 */
	//@Transactional(readOnly = false)
	//public void buyerCloseCouponAllOrder(String orderId) {
	//	// 退货取消订单的库存,1为所有订单
	//	OrderDto ord = getOrder(orderId, null, 1);
	//	if (ord == null || ord.getProductItems() == null
	//			|| ord.getProductItems().size() == 0) {
	//		return;
	//	}
	//	String buyerId = ord.getBuyerId();
	//	//关闭支付宝订单
	//	Payment oldPayment = paymentService.getByOrderId(orderId);
	//	PaymentLog log1 = new PaymentLog();
	//	log1.setPayment(oldPayment);
	//	log1.setTransactionSuccess("3");
	//	log1.setGatewayCode(ApiConstants.PAY_TYPE_ALIPAY);
	//	PaymentLog paymentLog = paymentLogService.getPaymentLog(log1);
	//	//取消支付单
	//	if (paymentLog != null && oldPayment != null) {
	//		//关闭交易
	//		if(AlipayUtils.closeTrade(paymentLog.getTransactionNo(), oldPayment.getPaymentNo())){
	//			logger.debug("支付宝关闭交易订单成功" + "支付宝交易号["
	//					+ paymentLog.getTransactionNo() + "],支付单号["
	//					+ oldPayment.getPaymentNo() + "]");
	//		}
	//	}
	//	List<ProductItemDto> list = ord.getProductItems();
	//	//查看订单项是否全部为全额抵扣,如果订单项全是全额抵扣，全部退回库存，并取消订单
	//	if(isAllCouponAllOrder(list)){
	//		// 退回库存
	//		for (ProductItemDto item : ord.getProductItems()) {
	//			productItemService.updateQuantiy(item.getProductItemId(),
	//					-item.getQuantity(), item.getProductType());
	//		}
	//		orderDao.buyerCloseOrder(orderId);
    //
	//		// 查看paymentId下面的所有订单是否都取消，如果取消，删除payment
	//		String paymentId = ord.getPaymentId();
	//		List<String> orderIds = orderDao.checkAllPayment(paymentId);
	//		if (orderIds == null || orderIds.size() == 0) {
	//			paymentService.deleteById(paymentId);
	//		}
	//		// 记录日志
	//		OrderLog log = new OrderLog();
	//		log.preInsert();
	//		Order order = new Order();
	//		order.setId(orderId);
	//		log.setOrder(order);
	//		log.setOperName(null);
	//		log.setAction("取消全额抵扣订单");
	//		log.setResult(ApiMsgConstants.SUCCESS_MSG);
	//		log.setNote("订单全为全额抵扣，取消订单" + ord.getOrderNum() + "成功");
	//		orderLogService.insert(log);
	//	}else{
	//		List<OrderItemDto> newList = new ArrayList<>();
	//		for (ProductItemDto item : ord.getProductItems()) {
	//			OrderItemDto dto = new OrderItemDto();
	//			//如果此订单项是全额抵扣，退回库存并减去订单金额
	//			if (ApiConstants.COUPON_ALL.equals(item.getProductType())) {
	//				productItemService.updateQuantiy(item.getProductItemId(),
	//						-item.getQuantity(), item.getProductType());
	//				//更新订单金额
	//				orderDao.updatePrice(orderId,item.getItemPrice());
	//				//记录日志
	//				OrderLog log = new OrderLog();
	//				log.preInsert();
	//				Order order = new Order();
	//				order.setId(orderId);
	//				log.setOrder(order);
	//				log.setOperName(null);
	//				log.setAction("取消全额抵扣订单项["+item.getOrderItemId()+"]");
	//				log.setResult(ApiMsgConstants.SUCCESS_MSG);
	//				log.setNote("删除订单" + ord.getOrderNum() + "里订单项"+item.getOrderItemId()+"金额为"+item.getItemPrice());
	//				orderLogService.insert(log);
	//				logger.debug("15分钟未付款，删除全额抵扣订单项"+item.getOrderItemId());
	//				//删除订单项
	//				orderItemService.deleteById(item.getOrderItemId());
    //
	//			}else{
	//				dto.setProductItemId(item.getProductItemId());
	//				dto.setQuantity(item.getQuantity());
	//				newList.add(dto);
	//			}
	//		}
	//		//更新邮费
	//		BigDecimal newFreight = getFreight(buyerId, newList);
	//		if(ord.getFreightPrice()!=null&&ord.getFreightPrice().compareTo(newFreight)>0){
	//			orderDao.updateFreight(orderId,newFreight);
	//		}
	//	}
    //
	//}

	//private boolean isAllCouponAllOrder(List<ProductItemDto> list) {
	//	for (ProductItemDto item : list) {
	//		if (!ApiConstants.COUPON_ALL.equals(item.getProductType())) {
	//			return false;
	//		}
	//	}
	//	return true;
	//}

	/**
	 * 买家确认收货 orderType = 4 --> 5
	 * @param buyerId 买家ID
	 * @param orderId 订单ID
     * @return
     */
	//@Transactional(readOnly = false)
	//public Map<String, Object> checkShipping(String buyerId, String orderId) {
	//	Map<String, Object> resultMap = new HashMap<>();
	//	OrderDto order = null;
	//	RLock lock = DistLockHelper.getLock("checkShipping");
	//	try {
	//		lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
	//		// 通过订单id查询出待收货的订单
	//		order = getOrder(orderId, buyerId, 4);
	//		if (order == null || order.getProductItems() == null
	//				|| order.getProductItems().size() == 0) {
	//			resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
	//			resultMap.put(ApiConstants.MSG, ApiConstants.ORDER_NOT_EXSIT);
	//			return resultMap;
	//		}
	//		// 更新订单状态为确认收货
	//		orderDao.checkShipping(orderId);
	//	} finally {
	//		if (lock.isLocked())
	//			lock.unlock();
	//	}
	//	// 记录日志
	//	OrderLog log = new OrderLog();
	//	log.preInsert();
	//	Order ord = new Order();
	//	ord.setId(orderId);
	//	log.setOrder(ord);
	//	log.setOperName(buyerId);
	//	log.setAction("确认收货");
	//	log.setResult(ApiMsgConstants.SUCCESS_MSG);
	//	log.setNote("确认收货" + order.getOrderNum() + "成功");
	//	orderLogService.insert(log);
	//	resultMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
	//	resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
    //
     //   // 添加7天后自动评论任务
     //   reviewReceive.autoAddReviewJob(order.getOrderNum());
	//	return resultMap;
	//}

    /**
     * 自动确认收货
     * 查询物流信息,如果物流显示买家已经收货,但是未点击确认收货
     * 则调用此方法修改订单状态 orderType = 4 --> 5
     * @param buyerId
     * @param orderId
     * @return
     */
	@Transactional(readOnly = false)
	public Map<String, Object> autoCheckShipping(String buyerId, String orderId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		OrderDto order = null;
		RLock lock = DistLockHelper.getLock("autoCheckShipping");
		try {
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
			// 通过订单id查询出已收货的订单
			order = getOrder(orderId, buyerId, 4);
			if (order == null || order.getProductItems() == null
					|| order.getProductItems().size() == 0) {
				resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				resultMap.put(ApiConstants.MSG, ApiConstants.ORDER_NOT_EXSIT);
				return resultMap;
			}
			// 更新订单状态为确认收货
			orderDao.checkShipping(orderId);
		} finally {
			if (lock.isLocked())
				lock.unlock();
		}
		// 记录日志
		OrderLog log = new OrderLog();
		log.preInsert();
		Order ord = new Order();
		ord.setId(orderId);
		log.setOrder(ord);
		log.setOperName(buyerId);
		log.setAction("确认收货");
		log.setResult(ApiMsgConstants.SUCCESS_MSG);
		log.setNote("确认收货" + order.getOrderNum() + "成功");
		orderLogService.insert(log);

        // 添加7天后自动评论任务
        reviewReceive.autoAddReviewJob(order.getOrderNum());

        // 每天晚上3点将买家已收到货但是未点击确认收货的订单的状态修改为已收货(7天前的所有满足条件的订单)
        // 然后主动调用增加收入任务
		checkShippingEarning(buyerId, orderId, 5);
		resultMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		return resultMap;
	}
	/**
	 * 确认收货7天后增加收入任务 (每天凌晨4点)
	 * @param buyerId
	 * @param orderId
	 * @param orderType
	 */
	@Transactional(readOnly = false)
	public void checkShippingEarning(String buyerId, String orderId,int orderType) {
		log.debug("增加收入操作...buyerId:"+buyerId+" orderId:"+orderId+" orderType:"+orderType);
		// 根据订单id和买家id获取订单类型为5的所有订单列表 (订单类型已完成)
        //去掉了类型为预售拿样的订单,预售拿样订单在支付成功之后就将分红给了上家和上上家
		OrderDto order = getOrder(orderId, buyerId, orderType); //orderType = 5
		log.debug("获得order:"+order);
		if (order == null||ApiConstants.YES.equals(order.getIsFund())) {
			return;
		}
		//获取订单中的商品项列表
		List<ProductItemDto> list = order.getProductItems();
		for (ProductItemDto item : list) {
			// 结算需要提前结算的订单项
			reconciliationService.reconciliation(item.getProductItemId(),item.getOrderItemId(),item.getQuantity(), order.getOrderNum());
		}
		// 更新订单结算状态 标识此订单已经结算过
		reconciliationService.updateReconciliationStatus(orderId);

		for (ProductItemDto item : list) {
			// 计算销售奖励 (返佣)
			BigDecimal saleEarning = BigDecimal.ZERO;
			// 计算销售分红收入
			BigDecimal bonusEarning = BigDecimal.ZERO;
			//// 计算一级团队分红
			//BigDecimal firstBonusEarning = BigDecimal.ZERO;
            //// 计算二级团队分红
			//BigDecimal secondBonusEarning = BigDecimal.ZERO;

			//数量
			int quantity;
			//退款完成数量
			int requantity = 0;
			// 计算本次销售额
			BigDecimal saled = BigDecimal.ZERO;
			// 拿到店铺id
			// 如果不是普通且不是拿样则跳过 (如果是预售拿样,因为在下单支付成功之后就已经结算过,所以现在不需要结算,V猫庄园中的商品不参加分红)
			log.debug("处理ProductItemDto:"+item);
			if (!ApiConstants.NORMAL.equals(item.getProductType())          			// 普通
               	 	&& !ApiConstants.SUPER.equals(item.getProductType())           		// 拿样
					&& !ApiConstants.GROUPBUY_SELLER.equals(item.getProductType())		// 卖家开团
					&& !ApiConstants.GROUPBUY_BUYER.equals(item.getProductType())		// 买家开团或买家参团
               ) {
				log.info("猫币抵扣订单项/预售进货订单项/V猫庄园订单项["+item.getOrderItemId()+"]不增加收入");
				continue;
			}
			log.debug("订单项["+item.getOrderItemId()+"]为普通订单，或者拿样订单，可以增加收入");
			//如果已经增加过收入，不再增加
            // 卖家店铺ID
			String shopId = item.getShopId();
            // 商品名称
			String productName = item.getProductName();
            // 商品ID
			String productId = item.getProductId();
            // 上级店铺id
			String parentShopId = item.getParentShopId();
            // 上上级店铺
			String parentParentShopId = item.getParentParentShopId();
			// 如果有退款申请，并且退款申请为退货中以前，撤销退款
			if (Refund.RETURN_STATUS_UNTREATED.equals(item.getReturnStatus())
					|| Refund.RETURN_STATUS_CHECK_SUCC.equals(item
					.getReturnStatus())) {
				Refund refund = new Refund();
				refund.setId(item.getRefundId());
				logger.debug("如果有退款申请，并且退款申请为退货中以前，撤销退款");
				refundService.cancelRefund(refund);
			}
			// 如果有退款申请，并且退款申请为退货中，抛出错误
			if (Refund.RETURN_STATUS_RETURNS_IN.equals(item.getReturnStatus())
					|| ((Refund.RETURN_STATUS_COMPLETED.equals(item
					.getReturnStatus())) && (!Refund.REFUND_STATUS_COMPLETED
					.equals(item.getReFundStatus())))) {
				logger.error("正在退货，无法确认收货");
				throw new ApiException("正在退货，无法确认收货");
			}
			// 如果有退款完成，拿出退款的商品个数
			if (Refund.REFUND_STATUS_COMPLETED.equals(item.getReFundStatus())
					&& Refund.RETURN_STATUS_COMPLETED.equals(item
					.getReturnStatus())) {
				requantity = item.getReQuantity() == null ? 0 : item
						.getReQuantity();
			}
			logger.debug("得出退款的商品个数requantity："+requantity);
            if (item.getItemSaleEarning() != null && !Objects.equals(item.getItemSaleEarning(), BigDecimal.ZERO)) {//返佣
                // 添加返佣
                saleEarning = saleEarning.add(item.getItemSaleEarning().multiply(new BigDecimal(item.getQuantity() - requantity)));
				logger.debug("得出saleEarning："+saleEarning);
			}
			if (item.getItemBonusEarning() != null && !Objects.equals(item.getItemSaleEarning(), BigDecimal.ZERO)) {
                // 增加销售分红
                bonusEarning = bonusEarning.add(item.getItemBonusEarning().multiply(new BigDecimal(item.getQuantity() - requantity)));
				logger.debug("得出bonusEarning："+bonusEarning);
            }
            //if (item.getItemFirstBonusEarning() != null) {
            //    // 一级团队分红
            //    firstBonusEarning = firstBonusEarning.add(item.getItemFirstBonusEarning().multiply(new BigDecimal(item.getQuantity() - requantity)));
            //}
            //if (item.getItemSecondBonusEarning() != null) {
            //    // 二级团队分红
            //    secondBonusEarning = secondBonusEarning.add(item.getItemSecondBonusEarning().multiply(new BigDecimal(item.getQuantity() - requantity)));
            //}
            // 商品项价格 (订单中的数量 - 优惠的数量 - 退款的数量) * 订单中的价格
//			if (item.getItemPrice() != null) {
//                saled = saled.add(item.getItemPrice().multiply(new BigDecimal(item.getQuantity() - requantity)));
//            }
            // 商品项数量
			quantity = (item.getQuantity() - requantity);
			// 根据店铺ID查询店铺的所有资金 (ec_shop_fund)
			ShopFund upFund = shopFundService.get(shopId);
			logger.debug("根据店铺ID查询店铺的所有资金："+upFund);
			// 如果是能退款且是普通订单 或者 是 买家开团/参团订单 将待确认变成可提现，
			// 只有ApiConstants.GROUPBUY_BUYER，没有GROUPBUY_SELLER原因是销售分红和团队分红都在购买的时候售价里面减了
			if(saleEarning.compareTo(BigDecimal.ZERO) != 0
					&& (ApiConstants.NORMAL.equals(item.getProductType()) || ApiConstants.GROUPBUY_BUYER.equals(item.getProductType()))
					&& item.getCanRefund()){
				// 增加卖家收入
                Shop pp = new Shop();
                pp.setId(shopId);
                pp = shopService.get(pp);
				logger.debug("增加店铺的收入-shop:"+pp);
				// 如果是 vip 销售返佣 = 销售返佣+销售分红
				String isVIP = "";
				if(pp.getAdvancedShop() == 1){
                    saleEarning = saleEarning.add(bonusEarning);
					logger.debug("VIP店铺，增加saleEarning+bonusEarning("+bonusEarning+")="+saleEarning);
					isVIP = "vip";
                }
                log.debug("增加"+isVIP+"店铺[" + shopId + "],销售返佣[" + saleEarning
                        + "],订单号为["+order.getId()+"@" + order.getOrderNum()+"]");

				// 减去待确认,增加可提现
				// 查询此用户的待确认收入是否小于等于0
				logger.debug("减去待确认,增加可提现");
				if(upFund.getSaleHoldFund().compareTo(BigDecimal.ZERO) < 0 || upFund.getSaleHoldFund().subtract(saleEarning).compareTo(BigDecimal.ZERO) < 0){
					logger.error("SaleHoldFund减去saleEarning<0");
					// 增加可提现收入,将待确认收入设置为0
					shopFundService.updateShopEarning(shopId, saleEarning, BigDecimal.ZERO);
				}else {
					logger.debug("SaleHoldFund减去saleEarning>=0");
					shopFundService.checkShipping(shopId, saleEarning, BigDecimal.ZERO);
				}

				Customer buyer = customerService.get(buyerId);
				// 向小店店主推送财富消息 - 销售奖励
				MessageEarning earning = new MessageEarning();
				Shop shop = new Shop();
				shop.setId(shopId);
				earning.setShop(shop);
				earning.setType(MessageEarning.TYPE_SALE);
				earning.setConsumer(buyer == null ? "" : buyer.getUserName());
				earning.setEarning(saleEarning);
				earning.setProductName(productName);
				earning.setOrderNumber(order.getOrderNum());
				earning.setQuantity(quantity);
				logger.debug("推送销售奖励消息:"+earning);
				messageService.pushEarningMsgTask(earning);
				// 记录销售收入log
				String log = "";
				if(!item.getCanRefund()){
					log = "不可退款已发货";
				}else{
					log = "确认收货";
				}
				fundOperLogService.insert(shopId, FundFieldType.SALE_HOLD_FUND, saleEarning.negate(), upFund.getSaleHoldFund().subtract
						(saleEarning), orderId, FundOperLog.NORMAL_INCOME,
						isVIP+"订单[" + order.getOrderNum() + "]商品[" + productName + "]" + log + "， 减去销售待确认资金[" + saleEarning + "]");
				fundOperLogService.insert(shopId, FundFieldType.SALE_AVAILABLE_FUND, saleEarning, upFund.getSaleAvailableFund().add
						(saleEarning), orderId, FundOperLog.NORMAL_INCOME,
						isVIP+"订单[" + order.getOrderNum() + "]商品[" + productName + "]"+ log + "， 增加销售可提现资金[" + saleEarning + "]");
			}

            /**
             * 卖家不是VIP
             * 如果有上家-->将上家待确认销售分红改为可提现销售分红
             */
            Shop shop = shopService.get(shopId);
			if(bonusEarning.compareTo(BigDecimal.ZERO) != 0
					&& !StringUtils.isBlank(parentShopId)
					&& shop.getAdvancedShop() != 1
					&& item.getCanRefund()){
                // 如果此店铺的有上家,增加上级销售分红可提现收入,且没有增加过收入
                log.debug("增加上级店铺[" + parentShopId + "],销售分红收入[" + bonusEarning
                        + "],订单号为[" + order.getOrderNum()+"]");
                // 获取上级卖家的收入
                ShopFund parentUpFund = shopFundService.get(parentShopId);
				if(parentUpFund.getBonusHoldFund().subtract(bonusEarning).compareTo(BigDecimal.ZERO) < 0){
					logger.error("BonusHoldFund减去bonusEarning<0");
					shopFundService.addBonusAvaiableFundOnly(parentShopId, bonusEarning);
				}else{
					logger.debug("BonusHoldFund减去bonusEarning>=0");
					// 减去待确认收入,增加可提现收入
					shopFundService.addBonusAvaiableFund(parentShopId, bonusEarning);
				}

                Customer buyer = customerService.get(shopId);
                // 向上级小店店主推送财富消息 - 销售分红收入
                MessageEarning earning = new MessageEarning();
                Shop p = new Shop();
                p.setId(parentShopId);
                earning.setShop(p);
                earning.setType(MessageEarning.TYPE_BONUS);
                earning.setConsumer(buyer == null ? "" : buyer.getUserName());
                earning.setEarning(bonusEarning);
                earning.setProductName(productName);
				logger.debug("推送销售分红消息:"+earning);
                messageService.pushEarningMsgTask(earning);
                // 记录销售分红收入log
				String log = "";
				if(!item.getCanRefund()){
					log = "不可退款已发货";
				}else{
					log = "确认收货";
				}
                fundOperLogService.insert(parentShopId,FundFieldType.BONUS_HOLD_FUND,bonusEarning.negate(),parentUpFund.getBonusHoldFund().subtract(bonusEarning),shopId,FundOperLog.NORMAL_INCOME,
                        "订单[" + order.getOrderNum() + "]商品[" + productName + "]" + log + "，减去上级卖家销售分红待确认资金[" + bonusEarning + "]");
                fundOperLogService.insert(parentShopId,FundFieldType.BONUS_AVAILABLE_FUND,bonusEarning,parentUpFund.getBonusAvailableFund().add(bonusEarning),shopId,FundOperLog.NORMAL_INCOME,
                        "订单[" + order.getOrderNum() + "]商品[" + productName + "]" + log + "，增加上级卖家销售分红可提现资金[" + bonusEarning + "]");
            }

			// 为店铺增加等级经验，目前转换率为1块钱一个等级,后期可细化
			// 获取总收入
			BigDecimal totalSaled1 = orderDao.getTotalSaled(shopId);
			if (totalSaled1 != null) {
				int saled1 = totalSaled1.intValue();
				shopService.updateLevel(shopId, saled1);
			}
		}
		//增加订单为已结算卖家收入
		logger.debug("增加订单为已结算");
		orderDao.updateIsFund(order.getId());
	}

    /**
     * 每周日晚上七点结算团队分红
     * @param buyerId 卖家ID
     * @param orderId 订单ID
     * @param orderType 订单类型 = 5
     */
    @Transactional(readOnly = false)
    public void CheckTeamShippingEarning(String buyerId, String orderId,int orderType){
        // 获取所有未结算团队分红的订单列表
    }
	// 通过id获取已经发货的商品
	public OrderDto getOrder(String orderId, String buyerId, int type) {
		Map<String, Object> map = new HashMap<>();
		map.put("orderId", orderId);
		map.put("orderType", type);
		map.put("buyerId", buyerId);
		Pager page = new Pager();
		page.setPageNo(1);
		page.setPageSize(1);
		page.doPage();
		map.put("page", page);
		List<OrderDto> list = orderDao.getBuyerOrderList(map);
		if (null != list && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	@Transactional(readOnly = false)
	public void buyerDelete(String orderId) {
		orderDao.buyerDelete(orderId);
	}

	// 获取自动取消的订单id
	public List<String> getOrderListBydealTime(int i) {
		return orderDao.getOrderListBydealTime(i);
	}

	public List<Map<String, Object>> getAutoCheckShippingList(Integer dayLimit) {
		return orderDao.getAutoCheckShippingList(dayLimit);
	}

	public Map<String, Object> getTotalPriceByPaymentId(String paymentId) {

		return orderDao.getTotalPriceByPaymentId(paymentId);
	}

	/**
	 * 打开购物车时,计算总价格
	 * @param buyerId 买家ID
	 * @param list 商品列表
	 * @return 返回总价格
	 */
	public Map<String, Object> getCartTotalPrice(String buyerId, JSONArray list) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 计算总金额
		BigDecimal paymentTotalPrice = BigDecimal.ZERO;
		BigDecimal paymentFreightPrice = BigDecimal.ZERO;
		BigDecimal paymentCouponPrice = BigDecimal.ZERO;
		JSONArray  cartList  = new JSONArray();
		// 所有购物车商品合起来
		for (Object object : list) {
			JSONObject json = (JSONObject) object;
			String shopId = json.getString("shopId");
			String productType = json.getString("productType");
			JSONArray elist = json.getJSONArray("cartList");
			//如果是普通订单
			if(ApiConstants.NORMAL.equals(productType)){
				for(Object obj :elist){
					JSONObject gson = (JSONObject) obj;
					gson.put("shopId", shopId);
				}
			}
			//合并购物车 商品项 并添加
			cartList.addAll(elist);
		}

		Map<String, List<OrderItemDto>> map = new HashMap<>();
		//买家
		Customer buyer = new Customer();
		buyer.setId(buyerId);
		map = separateOrder(cartList, map, buyer);
		// 如果有一个子订单失败，停止下单并返回错误
		resultMap = getProductItemTotalPrice(buyerId, map);
		if (ApiMsgConstants.FAILED_CODE == (int) resultMap
				.get(ApiConstants.CODE)) {
			throw new ApiException(resultMap.get(ApiConstants.MSG)
					.toString());
		}// 算出总价格
		else if (ApiMsgConstants.SUCCESS_CODE == (int) resultMap.get(ApiConstants.CODE)) {
			paymentTotalPrice = paymentTotalPrice.add((BigDecimal) resultMap.get("itemPrice"));
			paymentFreightPrice = paymentFreightPrice
					.add((BigDecimal) resultMap.get("itemfreightPrice"));
			paymentCouponPrice = paymentCouponPrice.add((BigDecimal) resultMap
					.get("itemcouponPrice"));
		}
		resultMap.put("totalPrice", paymentTotalPrice.add(paymentFreightPrice)
				.subtract(paymentCouponPrice));
		resultMap.put("totalFreightPrice", paymentFreightPrice);
		resultMap.put("totalCouponPrice", paymentCouponPrice);
		resultMap.remove("itemPrice");
		resultMap.remove("productItem");
		resultMap.remove("itemfreightPrice");
		resultMap.remove("itemcouponPrice");
		String isPay = ApiConstants.YES;
		BigDecimal shopCouponFund = (BigDecimal) resultMap
				.get("shopCouponFund");
		if (paymentCouponPrice.compareTo(shopCouponFund) > 0) {
			isPay = ApiConstants.NO;
			resultMap.put(ApiConstants.MSG, ApiMsgConstants.COUPON_NOT_ENTHOU);
		}
		resultMap.put("isPay", isPay);
		return resultMap;
	}

	private Map<String, Object> getProductItemTotalPrice(String buyerId,
														 Map<String, List<OrderItemDto>> map) {
		Map<String, Object> resultMap = new HashMap<>();
		// 计算总未付款金额
		BigDecimal orderPrice = BigDecimal.ZERO;
		// 计算总快递费付款金额
		BigDecimal totalFreightPrice = BigDecimal.ZERO;
		// 计算总猫币金额
		BigDecimal totalcouponPrice = BigDecimal.ZERO;
		// 买家猫币金额
		BigDecimal couponFundPrice = BigDecimal.ZERO;
		// 获取我的猫币金额
		Shop shop = new Shop();
		shop.setId(buyerId);
		CouponFund couponFund = new CouponFund();
		couponFund.setShop(shop);
		CouponFund conFund = couponFundService.get(couponFund);
		if (conFund != null) {
			couponFundPrice = conFund.getAvailableFund();
		}
		Map<String, Integer> allCartCount = new HashMap<String, Integer>();
		// 用来装商品项数量和优惠规则信息
		List<PromotionDto> promotionList = new ArrayList<>();
		for (String distributionId : map.keySet()) {
			// 计算快递费
			BigDecimal freightPrice = BigDecimal.ZERO;
			// 所有商品购卷金额
			BigDecimal totalProductcouponPrice = BigDecimal.ZERO;
			List<OrderItemDto> list = map.get(distributionId);
			// 计算快递费
			BigDecimal freight = getFreight(buyerId, list);
			if (freight != null) {
				freightPrice = freightPrice.add(freight);
			}

			// 计算商品的总价格
			BigDecimal totalPrice = BigDecimal.ZERO;
			for (OrderItemDto item : list) {
				// 5:预售拿样 6:V猫庄园
				String checkOrderType = item.getOrderItemType();
				Map<String, Object> productMap = getProductItem(item);
				ProductItem productItem = (ProductItem) productMap
						.get("productItem");
				if (productItem == null) {
					return productMap;
				}

				String subString = productItem == null ? "":productItem.getProduct().getName();


				//虚拟物品只能店主购买
				Boolean virtualProduct = productItem.getProduct().getVirtualProduct();
				boolean isSeller = false;
				List<CustomerRole> customerRoles = customerService.get(buyerId).getRoleList();
				for(CustomerRole cr : customerRoles){
					if(cr.getRole().getRoleName() == "seller"){
						isSeller = true;
					}
				}
				if(virtualProduct !=null && virtualProduct && isSeller){//卖家购买
					resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
					resultMap.put(ApiConstants.MSG, subString+" 该商品你不能购买，请下载V猫小店");
					return resultMap;
				}

				//如果商品属于全额抵扣，判断此商品是否已经购买过
				//if(ApiConstants.COUPON_ALL.equals(checkOrderType)){
				//	int count = orderDao.countCouponProduct(productItem.getId(),buyerId,checkOrderType);
				//	if(count>0||item.getQuantity()>1){
				//		resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				//		resultMap.put(ApiConstants.MSG,  (subString+"每天只可购买1件！"));
				//		return resultMap;
				//	}
				//}

				String ruleType = checkOrderType;
				boolean isLimit = orderDao.isProductInLimit(item.getProductItemId());
				if(isLimit){
					ruleType = ApiConstants.TOPIC;//专题
					if(allCartCount.get(productItem.getId()) == null){
						allCartCount.put(productItem.getId(), item.getQuantity());
					}else{
						Integer preCount = allCartCount.get(productItem.getId());
						allCartCount.put(productItem.getId(), item.getQuantity()+preCount);
					}
				}
				Map<String, Object> rule = getProductLimit(productItem.getId());
				if(rule != null){
					int result = applyLimit(rule, productItem.getId(), checkOrderType, ruleType, buyerId,
							ruleType.equalsIgnoreCase(ApiConstants.TOPIC) ? allCartCount.get(productItem.getId()) : item.getQuantity());
					if(result == 1){//卖家购买
						resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
						resultMap.put(ApiConstants.MSG, subString+" 该商品你不能购买，请下载V猫小店");
						return resultMap;
					}else if(result == 2){//买家购买
						resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
						resultMap.put(ApiConstants.MSG, subString+" 该商品你不能购买");
						return resultMap;
					}else if(result == 3){//超过限制
						int interval = (Integer)rule.get("interval");
						int times = (Integer)rule.get("times");
						resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
						resultMap.put(ApiConstants.MSG, subString+(interval == -1 ? "":+(interval+1)+"天内")+"限购"+times+"件！");
						return resultMap;
					}
				}
				// 拿样订单
				item.setItemPrice(productItem.getRetailPrice());
				//数量乘以单价
				totalPrice = totalPrice.add(item.getItemPrice().multiply(
						new BigDecimal(item.getQuantity())));
				// Code.Ai ( ) 判断商品项对应的商品的活动规则
				// 2016.04.01 将具有优惠商品的赠送商品数添加到ec_order_item promotion_quantity 字段中
				if(ApiConstants.SUPER.equals(checkOrderType) || (ApiConstants.RESERVE).equals(checkOrderType) ||
                        ApiConstants.VCATLEROY.equals(checkOrderType)){
					Map<String,Object> ruleMap = productItemService.getRuleByProductItemId(item.getProductItemId());
					if(ruleMap != null && ruleMap.size() != 0 && (item.getQuantity() >= (Integer)ruleMap.get("buyCount"))) {
						//增加客户的商品量
						item.setQuantity(item.getQuantity() + (item.getQuantity() / (Integer) ruleMap.get("buyCount")) * (Integer) ruleMap.get("freeCount"));
						PromotionDto promotionDto = new PromotionDto();
						promotionDto.setBuyCount((Integer) ruleMap.get("buyCount"));
						promotionDto.setFreeCount((Integer) ruleMap.get("freeCount"));
						promotionDto.setQuantity(item.getQuantity());
						promotionDto.setProductItemId(item.getProductItemId());
						promotionList.add(promotionDto);
					}
				}
				// 当部分抵扣时，计算抵扣金额总和
				//if (ApiConstants.COUPON_PART.equals(checkOrderType)) {
				//	totalProductcouponPrice = totalProductcouponPrice
				//			.add(productItem.getCouponValue().multiply(
				//					new BigDecimal(item.getQuantity())));
				//} else if (ApiConstants.COUPON_ALL.equals(checkOrderType)) {
				//	totalProductcouponPrice = totalProductcouponPrice.add(item
				//			.getItemPrice().multiply(
				//					new BigDecimal(item.getQuantity())));
				//}
				resultMap.remove("productItem");
			}
			// 计算总金额
			totalFreightPrice = totalFreightPrice.add(freightPrice);
			orderPrice = orderPrice.add(totalPrice);
			totalcouponPrice = totalcouponPrice.add(totalProductcouponPrice);
		}

		resultMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		resultMap.put("itemPrice", orderPrice);
		resultMap.put("itemfreightPrice", totalFreightPrice);
		resultMap.put("itemcouponPrice", totalcouponPrice);
		resultMap.put("shopCouponFund", couponFundPrice);
		resultMap.put("promotion",promotionList);
		return resultMap;
	}

	public int applyLimit(Map<String, Object> rule, String productItemId, String productType, String ruleType, String buyerId, int cartCount){
		if(rule == null || rule.isEmpty()){
			return 0;
		}
		Date startTime = (Date)rule.get("start_time");
		Date endTime = (Date)rule.get("end_time");
		Date now = new Date();
		if(now.getTime() >= startTime.getTime() && now.getTime() <= endTime.getTime()){
			int interval = (Integer)rule.get("interval");
			int times = (Integer)rule.get("times");
			Integer count = null;//以前购买的商品，已经有订单
			List<String> list = new ArrayList<String>();
			Customer customer = customerService.get(buyerId);
			Integer registered = customer.getRegistered();
			Integer userType = (Integer)rule.get("user_type");
			//0 卖买家 1 卖家 2 买家
			if(userType == 0) {
				logger.debug("没有限制，都可购买");
			}else if(userType == 1 && registered != 1){
				return 1;
			}else if(userType == 2 && registered == 1){
				return 2;
			}
			if(ruleType.equalsIgnoreCase(ApiConstants.TOPIC)){
				list.add(ApiConstants.NORMAL);
				list.add(ApiConstants.SUPER);
				list.add(productType);
			}else{
				list.add(productType);
			}
			if(interval == -1){
				count = orderDao.countPurchasedProductWithoutInterval(productItemId, buyerId, list);
			}else{
				count = orderDao. countPurchasedProduct(productItemId, buyerId, list, interval);
			}
			if(count == null){
				count = 0;
			}
			if(count >= times || cartCount + count > times){//cartCount 购物车购买数量
				return 3;
			}
		}
		return 0;
	}

	public Map<String, Object> getProductLimit(String productItemId){
		return orderDao.getProductLimit(productItemId);
	}

	//public int countCouponProduct(String productItemId, String buyerId,
	//							  String productType) {
	//	return orderDao.countCouponProduct(productItemId,buyerId,productType);
	//}

	@Transactional(readOnly = false)
	public void deleteOrderByPaymentId(String paymentId){
		List<String> orderIdList = orderDao.getOrderIdsByPaymentId(paymentId);
		for (int i = 0; i < orderIdList.size(); i++) {
			String orderId = orderIdList.get(i);
			orderDao.deleteOrderActivity(orderId);
			orderDao.deleteOrderAddress(orderId);
			orderDao.deleteOrderItem(orderId);
			orderDao.deleteOrderLog(orderId);
			orderDao.deleteOrder(orderId);
		}
		orderDao.deletePaymentLog(paymentId);
		orderDao.deletePayment(paymentId);
	}

	public Map<String,Object> getOrderType(String paymentNum) {

		return orderDao.getOrderType(paymentNum);
	}

	public List<ReviewDto> getReviewProductItemList(String orderId) {

		return orderDao.getReviewProductItemList(orderId);
	}

	public Map<String,Object> getOrderByItemId(String orderItemId) {
		return orderDao.getOrderByItemId(orderItemId);
	}

	public List<Map<String, Object>> getCheckShippingEarningList(int dayLimit) {
		return orderDao.getCheckShippingEarningList(dayLimit);
	}

	private BigDecimal getFreight(String buyerId, List<OrderItemDto> list) {
		BigDecimal freight = BigDecimal.ZERO;

		FreightConfig freightConfig = this.freightConfigService
				.getFreightBylist(buyerId, list);
		if (freightConfig != null) {
			long totalWeight = 0L;
			for (OrderItemDto item : list) {
				long weight = this.productItemService.getWeight(item
						.getProductItemId());
				totalWeight += weight * item.getQuantity();
			}
			long buf = totalWeight % 1000L;
			int count = 0;
			if(buf>0){
				count = (int)(totalWeight/1000L);
			}else{
				count = (int)(totalWeight/1000L)-1;
			}

			if (freightConfig.getIncrementPrice() != null) {
				freight = freightConfig.getFirstPrice().add(
						freightConfig.getIncrementPrice().multiply(
								new BigDecimal(count)));
			}
		}
		return freight;
	}


	//public List<String> getCouponOrderListBydealTime(int min) {
	//	return orderDao.getCouponOrderListBydealTime(min);
	//}

	public void updateOrderStatus(Order order){
		orderDao.updateOrderStatus(order);
	}
    public Boolean isSuperReserve(String paymentId){
        return orderDao.isSuperReserve(paymentId);
    }
    public List<OrderDto> getOrderInfoByPayment(Payment payment){
        return orderDao.getOrderInfoByPayment(payment);
    }

	// 获取预售订单log
	public List<OrderReserveLogDto> getOrderReserveLogs(String shopId,String productItemId){
		return orderDao.getOrderReserveLogs(shopId,productItemId);
	}

	public Map<String,Integer> getMyOrderCount(String customerId){
		return orderDao.getMyOrderCount(customerId);
	}

	// 卖家获取订单列表
	public List<OrderDto> getSellerOrderList(String buyerId, Pager page, String orderType, String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("buyerId", buyerId);
		map.put("orderType", orderType);
		map.put("page", page);
		map.put("condition", condition);
		return orderDao.getSellerOrderList(map);
	}

	// 卖家获取订单分页
	public int countBySellerId(String buyerId, String orderType, String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("buyerId", buyerId);
		map.put("orderType", orderType);
		map.put("condition", condition);
		return orderDao.countBySellerId(map);
	}
	public List<ProductItemDto> getOrderItemsInfo(String orderId) {
		// 判断是否为团购商品普通购买订单
		List<ProductItemDto> list = orderDao.getOrderItemsInfo(orderId);
		for(ProductItemDto p : list){
			Map<String,Object> result = orderDao.isGroupBuyProductNormalBuy(p.getOrderItemId());
			if(result != null){
				if(p.getProductType().equals("1")){
					p.setProductType("9");
				}else if(p.getProductType().equals("2")){
					p.setProductType("8");
				}
				p.setGroupBuyId((String)result.get("groupBuyId"));
			}
		}
		return list;
	}

	public OrderDto getSellerOrderDetail(OrderDto order,String buyerId){
		Map<String, Object> map = new HashMap<>();
		map.put("buyerId", buyerId);
		map.put("orderId", order.getId());
		Pager page = new Pager();
		page.setPageNo(1);
		page.setPageSize(1);
		page.doPage();
		map.put("page", page);
		List<OrderDto> list = orderDao.getSellerOrderList(map);
		if (null != list && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 取消订单
	 * @param orderId
	 * @param buyerId
	 * @param type
     */
	@Transactional(readOnly = false)
	public void cancelOrder(String orderId, String buyerId, int type) {
		// 退货取消订单的库存,1为所有订单
		OrderDto ord = getOrder(orderId, buyerId, 1);
		if (ord == null || ord.getProductItems() == null
				|| ord.getProductItems().size() == 0) {
			return;
		}
		//关闭支付宝订单
		Payment oldPayment = paymentService.getByOrderId(orderId);
		PaymentLog log1 = new PaymentLog();
		log1.setPayment(oldPayment);
		log1.setTransactionSuccess("3");
		log1.setGatewayCode(ApiConstants.PAY_TYPE_ALIPAY);
		PaymentLog paymentLog = paymentLogService.getPaymentLog(log1);
		//取消支付单
		if (paymentLog != null && oldPayment != null) {
			//关闭交易
			if(AlipayUtils.closeTrade(paymentLog.getTransactionNo(), oldPayment.getPaymentNo())){
				logger.debug("支付宝关闭交易订单成功" + "支付宝交易号["
						+ paymentLog.getTransactionNo() + "],支付单号["
						+ oldPayment.getPaymentNo() + "]");
			}
		}
		// 还原库存
		for (ProductItemDto item : ord.getProductItems()) {
			// 取消订单不增加库存
			//productItemService.updateQuantiy(item.getProductItemId(), -(item.getQuantity() + item.getPromotionQuantity()), item.getProductType());
			// 如果是团购订单, 已锁定
			GroupBuySponsorDto groupBuySponsorDto = new GroupBuySponsorDto();
			groupBuySponsorDto.setId(ord.getGroupBuySponsorId());
			groupBuySponsorDto = groupBuySponsorService.getGroupBuySponsor(groupBuySponsorDto);
			if(groupBuySponsorDto != null && groupBuySponsorDto.getLocked()){
				//  更新锁定状态为未锁定
				groupBuySponsorService.lockGroupBuy(ord.getGroupBuySponsorId(),ApiConstants.GROUP_BUY_UNLOCKED);
			}
		}
		orderDao.cancelOrder(orderId);
		// 查看paymentId下面的所有订单是否都取消，如果取消，删除payment
		//String paymentId = ord.getPaymentId();
		//List<String> orderIds = orderDao.checkAllPayment(paymentId);
		//if (orderIds == null || orderIds.size() == 0) {
			// paymentService.deleteById(paymentId); TODO 取消删除，观察有问题没
		//}
		// 记录日志
		OrderLog log = new OrderLog();
		log.preInsert();
		Order order = new Order();
		order.setId(orderId);
		log.setOrder(order);
		log.setOperName(buyerId);
		String action = "";
		if(type == SellerCancelOrderRequest.DONTBUY){
			action = "我不想买了";
		}else if(type == SellerCancelOrderRequest.INFOERROR){
			action = "信息写错了,重新拍";
		}else if(type == SellerCancelOrderRequest.PAYFAIL){
			action = "支付失败";
		}else if(type == SellerCancelOrderRequest.OTHER){
			action = "其他原因";
		}else if(SellerCancelOrderRequest.TIMEOUT == type){
			action = "超时未支付,自动取消";
		}
		log.setAction(action);
		log.setResult(ApiMsgConstants.SUCCESS_MSG);
		log.setNote("取消订单" + ord.getOrderNum() + "成功");
		orderLogService.insert(log);
	}

	/**
	 * 卖家确认收货 orderType = 4 --> 5
	 * @param buyerId 买家ID
	 * @param orderId 订单ID
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> sellerCheckShipping(String buyerId, String orderId) {
		Map<String, Object> resultMap = new HashMap<>();
		OrderDto order = null;
		RLock lock = DistLockHelper.getLock("sellerCheckShipping");
		try {
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
			// 通过订单id查询出已收货的订单
			order = getOrder(orderId, buyerId, 4);
			if (order == null || order.getProductItems() == null
					|| order.getProductItems().size() == 0) {
				resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
				resultMap.put(ApiConstants.MSG, ApiConstants.ORDER_NOT_EXSIT);
				return resultMap;
			}
			// todo 遍历订单项 如果有订单项在申请退款中
			List<ProductItemDto> itemList = order.getProductItems();
			for(ProductItemDto p : itemList){
				if (Refund.RETURN_STATUS_UNTREATED.equals(p.getReturnStatus())
						|| Refund.RETURN_STATUS_CHECK_SUCC.equals(p
						.getReturnStatus())) {
					Refund refund = new Refund();
					refund.setId(p.getRefundId());
					refundService.cancelRefund(refund);
				}
				// 如果有退款申请，并且退款申请为退货中，抛出错误
				if (Refund.RETURN_STATUS_RETURNS_IN.equals(p.getReturnStatus())
						|| ((Refund.RETURN_STATUS_COMPLETED.equals(p
						.getReturnStatus())) && (!Refund.REFUND_STATUS_COMPLETED
						.equals(p.getReFundStatus())))) {
					throw new ApiException("正在退货，无法确认收货");
				}
			}
			// 更新订单状态为确认收货
			orderDao.checkShipping(orderId);
		} finally {
			if (lock.isLocked())
				lock.unlock();
		}
		// 记录日志
		OrderLog log = new OrderLog();
		log.preInsert();
		Order ord = new Order();
		ord.setId(orderId);
		log.setOrder(ord);
		log.setOperName(buyerId);
		log.setAction("确认收货");
		log.setResult(ApiMsgConstants.SUCCESS_MSG);
		log.setNote("确认收货" + order.getOrderNum() + "成功");
		orderLogService.insert(log);
		resultMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);

        // 添加7天后自动评论任务
        reviewReceive.autoAddReviewJob(order.getOrderNum());
		return resultMap;
	}

	// 获取退款订单数量
	public int countSellerRefundOrder(String customerId){
		return orderDao.countSellerRefundOrder(customerId);
	}
	// 获取退款列表
	public List<ProductItemDto> getSellerRefundOrderList(String customerId,Pager page){
		return orderDao.getSellerRefundOrderList(customerId,page);
	}

	@Transactional(readOnly = false)
	public Payment againCreatePayment(List<MergePayRequest> orderIdList){
		Payment payment = new Payment();
		payment.preInsert();
		payment.setPaymentNo(buildOrderid());
		paymentService.insert(payment);
		for(MergePayRequest order : orderIdList){
			orderDao.paymentedByOrderId(order.getOrderId(), payment.getId());
		}
		return payment;
	}

	/**
	 * 支付成功后的操作
	 */
	@Transactional(readOnly = false)
	public void paymentedMotion(String paymentId){
		// 检查销售额是否满足升级条件
		// 如果订单项中包含特殊商品且满足规则
		Map<String,Object> isSpecialProduct =  shopService.isSpecialProduct(paymentId);
		if(isSpecialProduct != null){
			String shopId = (String)isSpecialProduct.get("shopId");
			Date paymentDate = (Date)isSpecialProduct.get("paymentDate");
			Shop shop = new Shop();
			shop.setId(shopId);
			shop = shopService.get(shop);
			if(shop != null && shopService.isReach(shopId,paymentDate) && shop.getAdvancedShop() == 0){
				messageService.pushRemindMessage(shopId, ApiConstants.UPGRADE_DEFAULT_TITLE, ApiConstants
						.UPGRADE_DEFAULT_CONTENT);
				shopService.updateAdvanced(shopId,2);
			}
		}

		// 获取卖家的累计销售额,如果达到要求,就给相应的 shopId发送个人消息
		BigDecimal defaultSalesLimit = new BigDecimal(DictUtils.getDictValue("ec_upgrade_sales_limit",""));
		List<Map<String,Object>> results = shopService.getAllShopByPayment(paymentId);
		if(results != null){
			for(Map<String,Object> result : results){
				BigDecimal nowSales = shopService.getTotalSale((String)result.get("shopId"));
				if(nowSales == null){
					continue;
				}
				if(nowSales.compareTo(defaultSalesLimit) >= 0 && result.get("vip").toString().equals("0")){
					messageService.pushRemindMessage((String)result.get("shopId"), ApiConstants.UPGRADE_DEFAULT_TITLE, ApiConstants
							.UPGRADE_DEFAULT_CONTENT);
					shopService.updateAdvanced((String)result.get("shopId"),2);
				}
				// 向店主发送短信
				logger.debug("向店主发送短信");
				String phone = (String)result.get("phoneNumber");
				String text = "【V猫小店】亲爱的喵主，您的小店又有人下单啦！订单号[" + result.get("orderNum") + "]，打开APP进入“我的客户”即可查看客户订单哦！";
				logger.debug(text);
				try {
					SmsClient.sendSms(text,phone);
				} catch (IOException e) {
					logger.info("向[" + phone + "]发送短信[" + text + "]失败：" + e.getMessage());
				}
			}
		}
		// 更新庄园订单的激活状态
		// 查询是否是虚拟商品
//		Map<String, Object> isVirtualProduct = productService.getProductsByPaymentId(paymentId);
//		if(isVirtualProduct != null){ //如果是v猫庄园商品 则激活
//			String shopId = (String)isVirtualProduct.get("shopId");
//			String productId = (String)isVirtualProduct.get("productId");
//			ShopInfoDto shopInfoDto = new ShopInfoDto(shopId,productId);
//			List<ShopInfoDto>  list= shopInfoService.getShopInfo(shopInfoDto);
//			if(list != null && list.size() == 1){
//				list.get(0).setIsActivate(1);
//				shopInfoService.updateShopInfo(shopInfoDto);
//			}
//		}
		// 更新拼团订单的状态
	}

	// 拼团详情状态
	public int returnGroupBuyStatus(OrderDto orderDto){
		ProductItemDto item = orderDto.getProductItems().get(0);
		GroupBuySponsorDto groupBuyInfo = new GroupBuySponsorDto();
		groupBuyInfo.setId(orderDto.getGroupBuySponsorId());
		groupBuyInfo = groupBuySponsorService.getGroupBuySponsor(groupBuyInfo);
		// 点击拼团详情时的状态判断
		// 判断：
		// 1. 拼团未结束,且为开团，显示开团成功页
		// 2. 拼团未结束，且为参团，显示参团成功页
		// 3. 拼团成功，显示 拼团成功页
		// 4  拼团失败，显示 拼团失败页
		if (orderDto.getGroupBuyStatus() == 1 &&                                                    // 拼团进行中
				(item.getProductType().equals(ApiConstants.GROUPBUY_SELLER)                         // 卖家开团
						|| (item.getProductType().equals(ApiConstants.GROUPBUY_BUYER)
						&& groupBuyInfo.getSponsorId().equals(orderDto.getBuyerId())))) {           // 下单者是开团者-->买家开团                                                                // 买家开团
			return 8;
		} else if (orderDto.getGroupBuyStatus() == 1
				&& item.getProductType().equals(ApiConstants.GROUPBUY_BUYER)
				&& !groupBuyInfo.getSponsorId().equals(orderDto.getBuyerId())) {                    // 买家参团
			return 9;
		} else if (orderDto.getGroupBuyStatus() == 2 || orderDto.getGroupBuyStatus() == 4) {		// 拼团成功或者失败
			return 4;
		}
		return 0;
	}

	// 重构订单数据结构
	public Map<String, Object> refactorOrderItem(Map<String, Object> orderMap, OrderDto orderDto,String type){
		List<Map<String, Object>> productItems   = new ArrayList<>();
		int                       size           = orderDto.getProductItems().size();
		Map<String, Object>       productItemMap = new HashMap<>();
		List<ProductItemDto>      itemList       = new ArrayList<>();

		Map<String, Object>  nextProductItemMap = null;
		List<ProductItemDto> nextItemList       = null;
		// 先创建一个新节点
		productItemMap.put("shopId", orderDto.getProductItems().get(0).getShopId());
		productItemMap.put("shopName", orderDto.getProductItems().get(0).getShopName());
		itemList.add(orderDto.getProductItems().get(0));
		productItemMap.put("itemList", itemList);
		productItems.add(productItemMap);
		orderMap.put("productItems", productItems);
		boolean flg   = true;
		int     index = 0;
		do {
			index++;
			if (index == size) {
				break;
			}
			String shopId       = orderDto.getProductItems().get(index - 1).getShopId();
			String shopName       = orderDto.getProductItems().get(index - 1).getShopName();
			String nextShopId   = orderDto.getProductItems().get(index).getShopId();
			String nextShopName = orderDto.getProductItems().get(index).getShopName();
			if (!shopName.equals(nextShopName)) {
				// 如果不同 则创建一个新节点
				nextProductItemMap = new HashMap<>();
				nextItemList = new ArrayList<>();
				nextProductItemMap.put("shopId", nextShopId);
				nextProductItemMap.put("shopName", nextShopName);
				nextItemList.add(orderDto.getProductItems().get(index));
				nextProductItemMap.put("itemList", nextItemList);
				productItems.add(nextProductItemMap);
				flg = false;
			} else {
				if (flg) {
					itemList.add(orderDto.getProductItems().get(index));
					productItemMap.put("itemList", itemList);
				} else {
					nextItemList.add(orderDto.getProductItems().get(index));
					nextProductItemMap.put("itemList", nextItemList);
				}
			}
		} while (index != size - 1);
		return orderMap;
	}

	@Transactional(readOnly = false)
	public void cancelOrderWithTimeOut(String orderId){
		OrderDto order = new OrderDto();
		order.setId(orderId);
		order = this.getSellerOrderDetail(order,"");
		if(order != null && order.getOrderType().equals("2")){  // 待支付
			this.cancelOrder(order.getId(),"",5);
		}
	}
	public Map<String,Object> getRefundCount(String orderId){
		return orderDao.getRefundCount(orderId);
	}
	@Transactional(readOnly = false)
	public void extendReceiptDate(String orderId){
		orderDao.extendReceiptDate(orderId);
	}
	public List<Map<String ,Object>> getWeixinInfoByPayment(Payment payment){
		return orderDao.getWeixinInfoByPayment(payment);
	}

	@Transactional(readOnly = false)
	public void changeOrderAddress(ChangeOrderAddressRequest param, String customerId, OrderDto order){
		String addressId = param.getAddressId();
		String provinceId = param.getProvinceId();
		String province = param.getProvince();
		String city = param.getCity();
		String cityId = param.getCityId();
		String district = param.getDistrict();
		String districtId = param.getDistrictId();
		String detailAddress = param.getDetailAddress();
		String deliveryName = param.getDeliveryName();
		String deliveryPhone = param.getDeliveryPhone();
		String addressName = param.getAddressName();
		String isDefault = param.getDefaultAddress();

		// 修改订单地址
		Map<String,Object> map = new HashMap<>();
		map.put("city",city);
		map.put("cityId",cityId);
		map.put("deliveryName",deliveryName);
		map.put("deliveryPhone",deliveryPhone);
		map.put("detailAddress",detailAddress);
		map.put("district",district);
		map.put("districtId",districtId);
		map.put("province",province);
		map.put("provinceId",provinceId);
		map.put("orderId",param.getOrderId());
		orderDao.changeOrderAddress(map);

		// 如果是默认地址,取消之前的默认地址
		//if(isDefault.equals(ApiConstants.YES)) {
		//	customerAddressService.cancalDefault(customerId);
		//}
		//CustomerAddress cuAdd = new CustomerAddress();
		//Customer cus = new Customer();
		//cus.setId(customerId);
		//Address adds = new Address();
		//adds.setId(addressId);
		//adds.setProvince(province);
		//adds.setProvinceId(provinceId);
		//adds.setCity(city);
		//adds.setCityId(cityId);
		//adds.setDistrict(district);
		//adds.setDistrictId(districtId);
		//adds.setDetailAddress(detailAddress);
		//adds.setDeliveryName(deliveryName);
		//adds.setDeliveryPhone(deliveryPhone);
		//cuAdd.setCustomer(cus);
		//cuAdd.setAddress(adds);
		//CustomerAddress customerAddress =  new CustomerAddress();
		//customerAddress.setAddressName(addressName);
		//customerAddress.setDefault(isDefault.equals("1"));
		//customerAddress.setAddress(adds);
		//customerAddress.setCustomer(cus);
		//customerAddressService.updateAddress(customerAddress);
	}

	@Transactional(readOnly = false)
	private void saveConfirmLog(Payment payment) {
		// 销售待确认收入(返佣)
		List<OrderDto> orderDtoList = this.getOrderInfoByPayment(payment);
		for(OrderDto orderDto : orderDtoList){
			OrderDto order = getOrder(orderDto.getId(),
					orderDto.getBuyerId(),
					3); // 2:未支付 3:已支付未发货 4:已支付已发货 5:已确认收货
			// 查询出此订单中普通订单项或者团购订单销售项返佣
			logger.debug("处理order:"+order);
			List<ShopFundDto> saleHoldFundList = shopFundService.getSaleHoldFund(order.getId()); // 商品的返佣金额(-)
			logger.debug("saleHoldFundList:"+saleHoldFundList);
			// 此订单中所有分红订单项列表
			List<FundOperSaleLog> saleHoldFundLogList = shopFundService.getSaleHoldFundLog(order.getId()); // 商品的返佣金额(+)
			logger.debug("saleHoldFundLogList:"+saleHoldFundLogList);
			List<ShopFund> bonusHoldFundList = shopFundService.getBonusHoldFund(order.getId()); //上家ID和销售分红金额(-)
			logger.debug("bonusHoldFundList:"+bonusHoldFundList);
			List<FundOperBonusLog> bonusHoldFundLogList = shopFundService.getBonusHoldFundLog(order.getId()); // 上家ID和销售分红金额(+)
			logger.debug("bonusHoldFundLogList:"+bonusHoldFundLogList);
			if (saleHoldFundList != null && saleHoldFundList.size() != 0
					&& saleHoldFundLogList != null && saleHoldFundLogList.size() != 0) {
				ProductItemDto productItemDto;
				int unableRefundCount = 0;
				for (int i = 0; i < saleHoldFundList.size(); i++) {
					productItemDto = productItemService.getProductItemDto(saleHoldFundList.get(i).getOrderItemId());
					logger.debug("处理productItemDto："+productItemDto);
					// 返佣
					ShopFundDto fund = saleHoldFundList.get(i);
					logger.debug("得到saleHoldFund："+fund);
					FundOperSaleLog saleLog = saleHoldFundLogList.get(i);
					logger.debug("得到FundOperSaleLog："+saleLog);
					// 销售分红
					ShopFund bonusFund = bonusHoldFundList.get(i);
					logger.debug("得到bonusFund："+bonusFund);
					FundOperBonusLog bonusdlog = bonusHoldFundLogList.get(i);
					logger.debug("得到FundOperBonusLog："+bonusdlog);
					// 卖家店铺
					Shop shop = shopService.get(fund.getId());
					logger.debug("操作的店铺是："+shop);
					/**
					 * 如果是普通订单或买家开团或买家参团订单,则表示买家在店主的小店中购买商品,则给卖家返佣
					 * 如果是拿样订单(orderItemType = 2 || orderItemType = 6 || orderItemType = 8),则表示店主在商城中购买,则不给返佣,因为店主购买时是零售价-返佣(-销售分红)
					 * 增加卖家店铺的返佣收入
					 */

					/**
					 * 需求变更:不可退款商品支付成功后增加待确认收入,后台订单已发货后,变为可提现
					 */
					if((productItemDto.getProductType().equals(ApiConstants.NORMAL) || productItemDto.getProductType().equals(ApiConstants.GROUPBUY_BUYER))
							&& saleLog.getShopFundId() != null
							&& saleLog.getFund() != null){
						//logger.debug("处理普通订单和团购商品项");
						//if(this.isSuperReserve(payment.getId())){
						//	break;
						//}
						//logger.debug("处理非团购的不可退款商品项");
						//// 增加店铺可提现返佣资金
						//shopFundService.addSaleAvaiableFund(fund.getId(),fund.getSaleHoldFund());
						//logger.debug("成功增加Sale可提现资金["+fund.getId()+"@"+fund.getSaleHoldFund()+"]");
						//// 根据店铺Id获取所有的资金详情
						//ShopFund upFund = shopFundService.get(fund.getId());
						//logger.debug("得到店铺资金:"+upFund);
						//if (upFund == null) {
						//	break;
						//}
						//// 是普通订单,而且是vip卖家才增加销售分红(增加到销售返佣金额中)
						//if(shop.getAdvancedShop() == 1){
						//	// 直接增加可提现销售分红到返佣资金中
						//	shopFundService.addSaleAvaiableFund(fund.getId(),bonusFund.getBonusHoldFund());
						//	// 设置要操作的资金总数 = 返佣 + 销售分红
						//	saleLog.setFund(saleLog.getFund().add(bonusdlog.getFund()));
						//	//  操作后的剩余可提现金额 = 原来的可提现金额 + 销售分红
						//	saleLog.setRemainFund(upFund.getSaleAvailableFund().add(bonusdlog.getFund()));
						//	logger.debug("高级店铺成功增加Sale可提现资金(分红)["+fund.getId()+"@"+bonusFund.getBonusHoldFund()+"]");
						//}else{
						//	saleLog.setFund(saleLog.getFund());
						//	saleLog.setRemainFund(upFund.getSaleAvailableFund());
						//}
						//if(saleLog.getFund() != null && saleLog.getFund().compareTo(new BigDecimal(0)) == 1){
						//	// 插入日志
						//	saleLog.preInsert();
						//	// 1为接口操作，收入
						//	saleLog.setFundType(FundOperLog.NORMAL_INCOME);
						//	saleLog.setNote("不能退货商品支付单[" + payment.getPaymentNo() + "]支付完成，增加销售可提现收入[" + saleLog.getFund() + "]");
						//	saleLog.setFundFieldType(new FundFieldType(FundFieldType.SALE_AVAILABLE_FUND));
						//	fundOperLogService.insert(saleLog);
						//	logger.debug("插入saleLog:"+saleLog);
						//	Customer buyer = customerService.get(orderDto.getBuyerId());
						//	MessageEarning earning = new MessageEarning();
						//	Shop p = new Shop();
						//	p.setId(shop.getId());
						//	earning.setShop(p);
						//	earning.setType(MessageEarning.TYPE_SALE); //销售返佣类型
						//	earning.setConsumer(buyer == null ? "" : buyer.getUserName());
						//	earning.setEarning(saleLog.getFund());
						//	earning.setOrderNumber(order.getOrderNum());
						//	earning.setProductName(productItemDto.getProductName());
						//	messageService.pushEarningMsgTask(earning);
						//	logger.debug("推送MessageEarning:"+earning);
						//	unableRefundCount++;
						//}
						// 如果商品不可退款,且是拼团订单,且在拼团中时,增加待确认收入
						// 如果商品可退款 增加待确认收入
						if(this.isSuperReserve(payment.getId())){
							break;
						}
						logger.debug("处理已支付订单的分红待确认收入");
						shopFundService.refundSuccess(fund);
						logger.debug("增加holdSaleFund:"+fund);
						// 根据店铺Id获取所有的资金详情
						ShopFund upFund = shopFundService.get(fund.getId());
						logger.debug("得到店铺资金:"+upFund);
						if (upFund == null) {
							break;
						}
						// 将返佣和销售分红都添加到返佣金额中
						if(shop.getAdvancedShop() == 1){
							// bonusFund为负数,此处为在原来的基础上加上待确认销售分红收入
							ShopFundDto bonus = new ShopFundDto();
							bonus.setId(shop.getId());
							bonus.setSaleHoldFund(bonusFund.getBonusHoldFund());
							// 再增加销售分红到销售返佣中
							shopFundService.refundSuccess(bonus);
							saleLog.setFund(saleLog.getFund().add(bonusdlog.getFund()));
							saleLog.setRemainFund(upFund.getSaleHoldFund().add(bonusdlog.getFund()));
							logger.debug("高级店铺成功增加SaleHold(分红)["+fund.getId()+"@"+bonusFund.getBonusHoldFund()+"]");
						}else{
							saleLog.setFund(saleLog.getFund());
							saleLog.setRemainFund(upFund.getSaleHoldFund());
						}
						saleLog.preInsert();
						saleLog.setFundType(FundOperLog.NORMAL_INCOME);
						saleLog.setNote("支付单[" + payment.getPaymentNo() + "]支付完成，订单号["+order.getOrderNum()+"]，增加销售待确认收入[" + saleLog.getFund() + "]");
						saleLog.setFundFieldType(new FundFieldType(FundFieldType.SALE_HOLD_FUND));
						logger.debug("插入saleHoldLog:"+saleLog);
						fundOperLogService.insert(saleLog);
					}

					/**
					 * 如果卖家是特约小店 增加待确认销售分红
					 */
					if(shop.getAdvancedShop() != 1
							&& bonusdlog.getShopFundId() != null
							&& bonusdlog.getFund() != null){
						logger.debug("给上级分红处理");
						// 获取上级店铺资金
						ShopFund parentUpFund = shopFundService.get(bonusFund != null ? bonusFund.getId() : null);
						if (parentUpFund == null) {
							break;
						}
						if (bonusFund != null && !StringUtils.isBlank(bonusFund.getId())) {
							// bonusFund为负数,此处为在原来的基础上加上待确认销售分红收入
							shopFundService.addBonusBoldFund(bonusFund);
							// 操作后的资金余额 = 操作前的金额 + (-) 操作金额
							bonusdlog.setRemainFund(parentUpFund.getBonusHoldFund().add(bonusdlog.getFund()));
							bonusdlog.preInsert();
							// 1为接口操作，收入
							bonusdlog.setFundType(FundOperLog.NORMAL_INCOME);
							bonusdlog.setNote("下级支付单[" + payment.getPaymentNo()	+ "]支付完成，上级销售分红待确认资金增加[" + bonusdlog.getFund() + "]");
							bonusdlog.setFundFieldType(new FundFieldType(FundFieldType.BONUS_HOLD_FUND));
						}
						//else if(bonusFund != null && !StringUtils.isBlank(bonusFund.getId())
						//		&& !productItemDto.getCanRefund()
						//		&& !productItemDto.getProductType().equals(ApiConstants.GROUPBUY_BUYER)
						//		&& !productItemDto.getProductType().equals(ApiConstants.GROUPBUY_SELLER)){
						//	// 直接将待确认收入变为可提现收入
						//	shopFundService.addBonusAvaiableFundOfReserve(bonusFund.getId(),bonusFund.getBonusHoldFund());
						//	// 设置操作后的资金余额 = 操作前的金额 + (-) 操作后的金额
						//	bonusdlog.setRemainFund(parentUpFund.getBonusAvailableFund().add(bonusdlog.getFund()));
						//	bonusdlog.preInsert();
						//	// 1为接口操作，收入
						//	bonusdlog.setFundType(FundOperLog.NORMAL_INCOME);
						//	// 如果是预售拿货 则直接增加上级店铺销售分红
						//	bonusdlog.setNote("下级不能退货商品支付单[" + payment.getPaymentNo()	+ "]支付完成，上级销售分红资金资金结算完成[" + bonusdlog.getFund() + "]");
						//	bonusdlog.setFundFieldType(new FundFieldType(FundFieldType.BONUS_AVAILABLE_FUND));
						//	// 立即给上家发送财富消息
						//	Customer buyer = customerService.get(shop.getId());
						//	MessageEarning earning = new MessageEarning();
						//	Shop p = new Shop();
						//	p.setId(bonusFund.getId());
						//	earning.setShop(p);
						//	earning.setType(MessageEarning.TYPE_BONUS); //销售分红消息类型  5
						//	earning.setConsumer(buyer == null ? "" : buyer.getUserName());
						//	earning.setEarning(bonusdlog.getFund());
						//	earning.setOrderNumber(order.getOrderNum());
						//	earning.setProductName(productItemDto.getProductName());
						//	messageService.pushEarningMsgTask(earning);
						//}
						bonusdlog.setRelateId(shop.getId());
						fundOperLogService.insert(bonusdlog);
					}
				}
				// 此订单中全部都为不可退款订单项
				//if(unableRefundCount == saleHoldFundList.size()){
				//	logger.debug("此订单中全部都为不可退款订单项,设置is_fund = 1");
				//	orderDao.updateIsFund(order.getId());
				//}
			}
		}
	}
}

