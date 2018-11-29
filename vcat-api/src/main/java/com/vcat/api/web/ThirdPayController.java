package com.vcat.api.web;

import com.alibaba.fastjson.JSONObject;
import com.alipay.config.AlipayConfig;
import com.alipay.sign.RSA;
import com.alipay.util.AlipayCore;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.tencent.WXPay;
import com.tencent.common.Configure;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.protocol.notify_protocol.PayNotifyReqData;
import com.tencent.protocol.notify_protocol.PayNotifyResData;
import com.tencent.protocol.pay_protocol.WebPayReqData;
import com.tencent.protocol.pay_protocol.WebPayResData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.tencent.service.ScanPayQueryService;
import com.tencent.service.WebPayService;
import com.tenpay.ResponseHandler;
import com.tenpay.wap.WapPayPageResponseHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.vcat.api.service.*;
import com.vcat.common.config.Global;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.lock.DistLockHelper;
import com.vcat.common.payment.AlipayUtils;
import com.vcat.common.payment.TenpayUtils;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.OrderDto;
import com.vcat.module.ec.entity.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@ApiVersion(1)
public class ThirdPayController extends RestBaseController {
	@Autowired
	private ExpressApiService expressService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private PaymentLogService paymentLogService;
	@Autowired
	private ShareEarningService shareEarningService;
	@Autowired
    private MessageService messageService;
    @Autowired
    private ShopService shopService;
	@Autowired
	private GroupBuyService groupBuyService;
	@Autowired
	private GroupBuyCustomerService groupBuyCustomerService;

    // 支付测试
    @RequestMapping("/anon/payment/sdvoiwegawr346dfbdfweg234")
    public Object testPayment(
            @RequestParam(value = "params", defaultValue = "") String param){
		// 如果是正式环境,则报错
		String envirnment = Global.getConfig("vcat.environment");
		if(envirnment.equals("prod")){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(param);
        } catch (Exception e) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String transactionNo = params.getString("transactionNo");
        String paymentId = params.getString("paymentId");
        String ss = callBack(paymentId,transactionNo,ApiConstants.PAY_TYPE_ALIPAY,null);
		if(ss.equals("fail")){
			return "fail";
		}else
			return "success";
    }

    // 支付成功后我们后台的业务逻辑处理
	private String callBack(String paymentId, String transactionNo,
			String gatewayCode,String openId) {
		// 判断商品是否已经做过业务逻辑处理
		List<String> list = paymentService.getOrderIds(paymentId);
		if (list == null || list.isEmpty()) {
			logger.debug("订单已支付，paymentId："+paymentId);
			return "success";
		}
		if (StringUtils.isBlank(transactionNo) || StringUtils.isBlank(paymentId)) {
			return "fail";
		}
		try {
			Payment payment = paymentService.get(paymentId);
			if (payment == null) {
				logger.debug("payment=null,return fail");
				return "fail";
			}else if(!StringUtils.isBlank(payment.getTransactionNo())){
				logger.debug("payment.getTransactionNo="+payment.getTransactionNo()+",说明已经更新过，直接返回sucess");
				return "success";
			}
			Gateway gateway = new Gateway();
			gateway.setCode(gatewayCode);
			payment.setGateway(gateway);
			payment.setTransactionNo(transactionNo);
			// 更新支付状态
			orderService.paymented(payment,openId);
			logger.info("订单[" + paymentId + "]支付成功，支付类型为：" + gatewayCode
					+ ",三方支付单号为：" + transactionNo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "fail";
		}
		return "success";
	}

	/**
	 * 财付通支付
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/payment/tenpay")
	public Object tenpayRequest(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String status = "";
		String url = "";
		String paymentId = params.getString("paymentId");
		String orderId = params.getString("orderId");
		if ((StringUtils.isBlank(paymentId) && StringUtils.isBlank(orderId))) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		// 商品描述
		String des = "";
		String orderNum = "";
		// 分做单位
		int totalPrice = 0;
		// 原始金额
		BigDecimal returnPrice = BigDecimal.ZERO;
		//总抵扣卷金额
		BigDecimal totalCouponPrice = BigDecimal.ZERO;
		//买家可用抵用劵金额 
		BigDecimal availableCouponPrice = BigDecimal.ZERO;
		String attach = "";
		// 如果是合单支付，即只有paymentId
		if (StringUtils.isBlank(orderId) && !StringUtils.isBlank(paymentId)) {
			Payment payment = paymentService.get(paymentId);
			if (payment == null) {
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
			orderNum = payment.getPaymentNo();
		} else
		// 分单支付，只有orderId
		if (!StringUtils.isBlank(orderId) && StringUtils.isBlank(paymentId)) {
			// 从新生成支付单
			// 更新支付状态
			Payment payment = orderService.autoPayment(orderId);
			orderNum = payment.getPaymentNo();
			paymentId = payment.getId();
		}
		// 获得总金额
		Map<String, Object> map = orderService
				.getTotalPriceByPaymentId(paymentId);
		if (map == null || map.size() == 0) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		returnPrice = (BigDecimal) map.get("totalPrice");
		totalPrice = returnPrice == null ? 0 : returnPrice.multiply(
				new BigDecimal(100)).intValue();
		attach = paymentId;
		des = (String) map.get("productName") + "等";
		des = des.replaceAll("&([#]|[\\w])+;", "");
		totalCouponPrice = (BigDecimal) map.get("totalCouponPrice");
		availableCouponPrice = (BigDecimal) map.get("availableCouponPrice");
		//判断此时的抵扣劵是否够抵扣
		if(totalCouponPrice.compareTo(availableCouponPrice)>0){
				return new MsgEntity(ApiMsgConstants.COUPON_NOT_ENTHOU,
						ApiMsgConstants.FAILED_CODE);
		}
		String timeStart = DateUtils.getDate("yyyyMMddHHmmss");
		String timeExpire = DateUtils.getDate(1, "yyyyMMddHHmmss");
		logger.debug("totalPrice:" + returnPrice);
		try {
			String result = TenpayUtils.payRequest(request, response, des,
					orderNum, totalPrice, attach, timeStart, timeExpire);
			if (!result.startsWith("ERROR")) {
				status = ApiConstants.YES;
				url = result;
				logger.debug("财付通支付链接拼接成功，" + "totalPrice[" + returnPrice
						+ "]," + "支付单号为[" + orderNum + "]。" + "拼接url：" + result);
			} else {
				logger.debug("财付通支付链接拼接失败，" + "totalPrice[" + returnPrice
						+ "]," + "支付单号为[" + orderNum + "]。" + "拼接url：" + result);
				// 支付失败页面
				status = ApiConstants.NO;
			}
		} catch (Exception e) {
			// 失败抛异常时，也返回支付失败页面
			logger.error(e.getMessage(), e);
		}
		Map<String, Object> resultmap = new HashMap<String, Object>();
		resultmap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		resultmap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		resultmap.put("url", url);
		resultmap.put("status", status);
		return resultmap;
	}

	@RequestMapping("/anon/payment/tenpay/notify")
	public String tenpayNotify(HttpServletRequest request,
			HttpServletResponse response) {
		// 密钥
		String key = TenpayUtils.BARGAINOR_KEY;
		// 创建实例
		ResponseHandler resHandler = new ResponseHandler(request, response);
		resHandler.setKey(key);
		// uri编码
		try {
			resHandler.setUriEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		Payment payment = new Payment();
		String debugInfo = resHandler.getDebugInfo();
		logger.debug("debugInfo:" + debugInfo);
		// 支付结果：0—成功；其它—失败
		String payResult = resHandler.getParameter("pay_result");
		// 商户订单号
		String orderNo = resHandler.getParameter("sp_billno");
		// 财付通订单号 对于我们平台的transactionNo
		String transactionNo = resHandler.getParameter("transaction_id");
		// 获取附加信息,获知是否为统一支付，或者单个支付
		String attach = resHandler.getParameter("attach");
		// 统一支付或者单个支付
		String paymentId = attach;
		payment.setId(paymentId);
		payment.setTransactionNo(transactionNo);
		RLock lock = DistLockHelper.getLock("tenpayNotify");
		try {
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
			if (resHandler.isTenpaySign()) {
				if (StringUtils.isBlank(attach)) {
					return "fail";
				}
				if ("0".equals(payResult)) {
					// 保存以上业务数据 transactionNo totalFee
					logger.debug("财付通回掉支付成功，商户支付单号[" + orderNo + "]," + "财付通支付单号["
							+ transactionNo + "]," + "回掉结果：" + payResult);
					return callBack(paymentId, transactionNo, ApiConstants.PAY_TYPE_TENPAY,null);
				} else {
					// 记录支付日志
					PaymentLog log = new PaymentLog();
					log.preInsert();
					log.setPayment(payment);
					log.setTransactionSuccess(ApiConstants.NO);
					log.setGatewayCode(ApiConstants.PAY_TYPE_TENPAY);
					log.setNote(resHandler.getParameter("pay_info"));
					paymentLogService.insertBypayment(log);
					logger.debug("财付通回掉支付失败,商户支付单号[" + orderNo + "]," + "财付通支付单号["
							+ transactionNo + "]," + "失败原因："
							+ resHandler.getParameter("pay_info"));
					return "fail";
				}
			} else {
				// 记录支付日志
				PaymentLog log = new PaymentLog();
				log.preInsert();
				log.setPayment(payment);
				log.setTransactionSuccess(ApiConstants.NO);
				log.setGatewayCode(ApiConstants.PAY_TYPE_TENPAY);
				log.setNote("后台通知，验证签名失败");
				paymentLogService.insertBypayment(log);
				logger.debug("财付通回掉支付失败，验证签名失败");
				return "fail";
			}
		}finally {
			if(lock.isLocked())
				lock.unlock();
		}
	}

	@RequestMapping("/anon/payment/tenpay/callback")
	public String tenpayCallback(HttpServletRequest request,
			HttpServletResponse response) {
		// 密钥
		String key = TenpayUtils.BARGAINOR_KEY;
		// 创建实例
		WapPayPageResponseHandler resHandler = new WapPayPageResponseHandler(
				request, response);
		resHandler.setKey(key);
		// uri编码
		try {
			resHandler.setUriEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		String debugInfo = resHandler.getDebugInfo();
		logger.debug("debugInfo:" + debugInfo);
		// 判断签名
		if (resHandler.isTenpaySign()) {
			// 支付结果
			String pay_result = resHandler.getParameter("pay_result");

			String total_fee = resHandler.getParameter("total_fee");
			// 商户订单号
			String paymentNum = resHandler.getParameter("sp_billno");
			Map<String,Object> map = orderService.getOrderType(paymentNum);
			String orderType = (String) map.get("orderType");
			logger.debug("支付成功，回调 支付单号 =  "+paymentNum);
			String fromPage = "";
			if(ApiConstants.POSTAGE.equals(orderType)){
				fromPage = ApiConstants.ACTIVITY;
			}
			BigDecimal totalPrice = new BigDecimal(total_fee);
			// 0表示成功
			if (pay_result.equals(ApiConstants.NO)) {
				// ------------------------------
				// 显示成功
				// ------------------------------
				logger.debug("财付通支付成功,转发通知终端成功页面" + "result:"
						+ resHandler.getAllParameters());
				try {
					String url = "";
					String groupBuySponsorId = "";
					if(ApiConstants.GROUPBUY_SELLER.equals(orderType)){ // 卖家开团
						groupBuySponsorId = (String) map.get("groupBuySponsorId");
						url = ApiConstants.VCAT_DOMAIN
								+ "/buyer/views/group.html?status="
								+ ApiConstants.YES + "&price="
								+ totalPrice.divide(new BigDecimal("100"))
								+ "&productName=" + resHandler.getParameter("desc")+ "&groupBuySponsorId="+groupBuySponsorId + "&fromPage="+fromPage  + "&type=" + ApiConstants.SELLER_LAUNCH;
					}else if(ApiConstants.GROUPBUY_BUYER.equals(orderType)){ // 买家开团/参团
						groupBuySponsorId = (String) map.get("groupBuySponsorId");
						url = ApiConstants.VCAT_DOMAIN
								+ "/buyer/views/group.html?status="
								+ ApiConstants.YES + "&price="
								+ totalPrice.divide(new BigDecimal("100"))
								+ "&productName=" + resHandler.getParameter("desc")+ "&groupBuySponsorId="+groupBuySponsorId + "&fromPage="+fromPage + "&type=" + ApiConstants.BUYER_LAUNCH;
					}else{
						url = ApiConstants.VCAT_DOMAIN
								+ "/buyer/views/payStatus.html?status="
								+ ApiConstants.YES + "&price="
								+ totalPrice.divide(new BigDecimal("100"))
								+ "&productName=" + resHandler.getParameter("desc") + "&fromPage="+fromPage ;
					}
					logger.debug("支付成功，回调 url = "+url);
					response.sendRedirect(url);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}

			} else {
				logger.debug("财付通支付失败,转发通知终端失败页面" + "result:"
						+ resHandler.getAllParameters());
				try {
					response.sendRedirect(ApiConstants.VCAT_DOMAIN
							+ "/buyer/views/payStatus.html?status="
							+ ApiConstants.NO);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}

		} else {
			logger.debug("财付通支付失败,转发通知终端失败页面" + "result:签名不正确");
			try {
				response.sendRedirect(ApiConstants.VCAT_DOMAIN
						+ "/buyer/views/payStatus.html?status="
						+ ApiConstants.NO);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return "";
	}

	/**
	 * 
	 * @param token
	 * @param param
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/payment/weixin/request")
	public Object weiPayRequest(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String remoteIp = StringUtils.getRemoteAddr(request);
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String paymentId = params.getString("paymentId");
		String orderId = params.getString("orderId");
		String openId = params.getString("openId");
		String source = params.getString("source");
		if(StringUtils.isNullBlank(source)){
			source = "wap";
		}
		if ((StringUtils.isBlank(paymentId) && StringUtils.isBlank(orderId))
				|| (source.equalsIgnoreCase("wap") && StringUtils.isBlank(openId))) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		// 商品描述
		String des = "";
		String orderNum = "";
		// 金额单位为分
		int totalPrice = 0;
		// 原始金额
		BigDecimal returnPrice = BigDecimal.ZERO;
		//总抵扣卷金额
		BigDecimal totalCouponPrice = BigDecimal.ZERO;
		//买家可用抵用劵金额 
		BigDecimal availableCouponPrice = BigDecimal.ZERO;
		String attach = "";
		// 如果是合单支付，即只有paymentId
		if (StringUtils.isBlank(orderId) && !StringUtils.isBlank(paymentId)) {
			Payment payment = paymentService.get(paymentId);
			if (payment == null) {
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
			orderNum = payment.getPaymentNo();
		} else
		// 分单支付，只有orderId
		if (!StringUtils.isBlank(orderId) && StringUtils.isBlank(paymentId)) {
			// 从新生成支付单
			// 更新支付状态
			Payment payment = orderService.autoPayment(orderId);
			orderNum = payment.getPaymentNo();
			paymentId = payment.getId();
		}
		// 如果2个参数都不为空
		else if(!StringUtils.isBlank(orderId) && !StringUtils.isBlank(paymentId)){
			// 根据paymentId查询订单 如果有多个订单 则使用paymentId
			Payment        payment = paymentService.get(paymentId);
			if (payment == null) {
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
			List<OrderDto> orders =  orderService.getOrderInfoByPayment(payment);
			if(orders.size() > 1){
				// 如果是合单支付
				orderNum = payment.getPaymentNo();
			}
			// 如果此支付单下只有一个订单
			else if(orders.size() == 1){
				// 重新生成支付单
				Payment p = orderService.autoPayment(orderId);
				orderNum = p.getPaymentNo();
				paymentId = p.getId();
			}
		}
		// 获得总金额
		Map<String, Object> map = orderService
				.getTotalPriceByPaymentId(paymentId);
		if (map == null || map.size() == 0) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		returnPrice = (BigDecimal) map.get("totalPrice");
		totalPrice = returnPrice == null ? 0 : returnPrice.multiply(
				new BigDecimal(100)).intValue();
		attach = paymentId;
		des = (String) map.get("productName");
		des = des.replaceAll("&([#]|[\\w])+;", "");
		if(des.length() > 50){
			des = des.substring(0,40) + "...";
		}
		totalCouponPrice = (BigDecimal) map.get("totalCouponPrice");
		availableCouponPrice = (BigDecimal) map.get("availableCouponPrice");
		//判断此时的抵扣劵是否够抵扣
		//if(totalCouponPrice.compareTo(availableCouponPrice)>0){
		//		return new MsgEntity(ApiMsgConstants.COUPON_NOT_ENTHOU,
		//				ApiMsgConstants.FAILED_CODE);
		//}
		
		logger.debug("totalPrice:" + returnPrice);
		String timeStart = DateUtils.getDate("yyyyMMddHHmmss");
		String timeExpire = DateUtils.getDate(1, "yyyyMMddHHmmss");
		String notifyUrl = Global.getConfig("wechat.pay.notify.url");
		String prepayId = "";

		WXPay wxPay = new WXPay();
		WebPayReqData webPayReqData = null;
		Configure configure = wxPay.getConfigure();
		// 组装微信支付信息
		if(source.equalsIgnoreCase("mobile")){
			wxPay.initConfigurationForMobile();
			webPayReqData = new WebPayReqData(des, attach, orderNum,
					totalPrice, "mobile", remoteIp, timeStart, timeExpire, "",
					notifyUrl, "APP", "", openId,configure);
		}else{
			wxPay.initConfigurationForWap();
			webPayReqData = new WebPayReqData(des, attach, orderNum,
					totalPrice, "WEB", remoteIp, timeStart, timeExpire, "",
					notifyUrl, "JSAPI", "", openId,configure);
		}
		String resString = "";
		try {
			resString = wxPay.requestWebPayService(webPayReqData);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new MsgEntity(ApiMsgConstants.FAILED_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		WebPayResData webPayResData = (WebPayResData) Util
				.getObjectFromXML(resString, WebPayResData.class);
		if (!"SUCCESS".equals(webPayResData.getResult_code())) {
			return new MsgEntity(ApiMsgConstants.GET_SIGN_FAILED + ","
					+ webPayResData.getReturn_msg(),
					ApiMsgConstants.FAILED_CODE);
		}
		logger.debug("微信预支付获取prePayId成功，" + "totalPrice[" + returnPrice
				+ "]," + "支付单号为[" + orderNum + "]。" + "请求返回数据："
				+ webPayResData);
		prepayId = webPayResData.getPrepay_id();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(source.equalsIgnoreCase("wap")) {
			returnMap.put("appId", configure.getAppid());
			returnMap.put("timeStamp", System.currentTimeMillis() + "");
			returnMap.put("nonceStr",
					RandomStringGenerator.getRandomStringByLength(32));
			returnMap.put("signType", "MD5");
			returnMap.put("package", "prepay_id=" + prepayId);
			returnMap.put("paySign", Signature.getSign(returnMap, configure));
		} else {
			returnMap.put("appid", configure.getAppid());
			returnMap.put("partnerid", configure.getMchid());
			returnMap.put("prepayid", prepayId);
			returnMap.put("package", "Sign=WXPay");
			returnMap.put("noncestr",
					RandomStringGenerator.getRandomStringByLength(32));
			returnMap.put("timestamp", (System.currentTimeMillis()/1000) + "");
			returnMap.put("sign", Signature.getSign(returnMap,configure));
			returnMap.put("body", des);
			returnMap.put("totalPrice", returnPrice.toPlainString());
		}
		returnMap.put("paymentId",paymentId);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		resultMap.put("prepay", returnMap);
		return resultMap;
	}

	/**
	 * 微信支付回掉
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/anon/payment/weixin/notify")
	public void weixinNotify(HttpServletRequest request,
			HttpServletResponse response) {
		// 新建返回对象
		PayNotifyResData res = new PayNotifyResData();
		// 获取request中的xml文件
		String xmlString = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					request.getInputStream(), "UTF-8"));
			String line = "";
			StringBuffer buffer = new StringBuffer(2048);
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
			xmlString = URLDecoder.decode(buffer.toString(), "UTF-8");
			br.close();
		} catch (IOException e) {
			res.setReturn_code("FAIL");
			res.setReturn_msg("参数格式校验错误");
			getResponse(response, res);
			logger.error(e.getMessage(), e);
			return;
		}
		// 将xml转换成对象
		PayNotifyReqData payNotifyReqData = (PayNotifyReqData) Util
				.getObjectFromXML(xmlString, PayNotifyReqData.class);
		if (payNotifyReqData == null) {
			res.setReturn_code("FAIL");
			res.setReturn_msg("参数格式校验错误");
			getResponse(response, res);
			return;
		}
		String return_code = payNotifyReqData.getReturn_code();
		String return_msg = payNotifyReqData.getReturn_msg();
		logger.debug("微信回掉服务端,返回结果:" + return_code + ":" + return_msg);
		logger.debug("返回对象:"+payNotifyReqData);
		// 获取附加信息,获知是否为统一支付，或者单个支付
		String attach = payNotifyReqData.getAttach();
		// 支付结果：SUCESS—成功；其它—失败
		String payResult = payNotifyReqData.getResult_code();
		// 商户订单号
		String orderNo = payNotifyReqData.getOut_trade_no();
		// 财付通订单号 对于我们平台的transactionNo
		String transactionNo = payNotifyReqData.getTransaction_id();
		String source = payNotifyReqData.getDevice_info();
		Payment payment = new Payment();
		// 统一支付或者单个支付
		String paymentId = attach;
		payment.setId(paymentId);
		payment.setTransactionNo(transactionNo);
		RLock lock = DistLockHelper.getLock("weixinNotify");
		try {
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
			if ("SUCCESS".equals(return_code)) {
				if (StringUtils.isBlank(attach)) {
					res.setReturn_code("FAIL");
					res.setReturn_msg("参数格式校验错误");
					getResponse(response, res);
					return;
				}
				if ("SUCCESS".equals(payResult)) {
					// 保存以上业务数据 transactionNo totalFee
					logger.debug("微信支付回掉成功，商户支付单号[" + orderNo + "]," + "微信支付单号["
							+ transactionNo + "]," + "回掉结果：" + payResult);
					String openId = payNotifyReqData.getOpenid();
					String result = callBack(paymentId, transactionNo,
							source.equalsIgnoreCase("mobile") ? ApiConstants.PAY_TYPE_WX_MOBILE : ApiConstants.PAY_TYPE_WX,openId);
					res.setReturn_code(result.toUpperCase());
				} else {
					// 记录支付日志
					PaymentLog log = new PaymentLog();
					log.preInsert();
					log.setPayment(payment);
					log.setTransactionSuccess(ApiConstants.NO);
					log.setGatewayCode(ApiConstants.PAY_TYPE_WX);
					log.setNote(payNotifyReqData.getErr_code_des());
					paymentLogService.insertBypayment(log);
					logger.error("微信支付回掉失败，商户支付单号[" + orderNo + "]," + "微信支付单号["
							+ transactionNo + "]," + "失败原因："
							+ payNotifyReqData.getErr_code_des());
					res.setReturn_code("FAIL");
					res.setReturn_msg(payNotifyReqData.getErr_code_des());
					getResponse(response, res);
					return;
				}
			} else {
				// 记录支付日志
				PaymentLog log = new PaymentLog();
				log.preInsert();
				log.setPayment(payment);
				log.setTransactionSuccess(ApiConstants.NO);
				log.setGatewayCode(ApiConstants.PAY_TYPE_WX);
				log.setNote(payNotifyReqData.getErr_code_des());
				paymentLogService.insertBypayment(log);
				logger.error("微信支付回掉失败，商户支付单号[" + orderNo + "]," + "微信支付单号["
						+ transactionNo + "]," + "失败原因：" + payNotifyReqData);
				res.setReturn_code("FAIL");
				res.setReturn_msg(payNotifyReqData.getReturn_msg());
				getResponse(response, res);
				return;
			}
		} finally {
			if(lock.isLocked())
				lock.unlock();
		}
		getResponse(response, res);
		return;
	}

	private void getResponse(HttpServletResponse response, PayNotifyResData res) {
		// 解决XStream对出现双下划线的bug
		XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8",
				new XmlFriendlyNameCoder("-_", "_")));
		// 将要提交给API的数据对象转换成XML格式数据Post给API
		String postDataXML = xStreamForRequestPostData.toXML(res);
		logger.debug("返回结果给微信支付:"+postDataXML);
		response.addHeader("Content-Type", "text/xml");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if (writer != null) {
			writer.write(postDataXML);
			writer.close();
		}

	}

	/**
	 * 微信支付查询
	 * @param request
	 * @param response
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/payment/weixin/queryResult")
	public Object weixinQuery(
			@RequestParam(value = "params", defaultValue = "") String param,
			HttpServletRequest request, HttpServletResponse response) {
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String paymentId = params.getString("paymentId");
		String orderId = params.getString("orderId");
		if ((StringUtils.isBlank(paymentId) && StringUtils.isBlank(orderId))) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		// 成功或者失败状态
		String status = "";
		String orderNum = "";
		String transactionID = "";
		// 分单支付，只有orderId
		if (!StringUtils.isBlank(orderId) && StringUtils.isBlank(paymentId)) {
			Payment payment = paymentService.getByOrderId(orderId);
			if (payment == null) {
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
			transactionID = payment.getTransactionNo();
			orderNum = payment.getPaymentNo();
		} else if (StringUtils.isBlank(orderId)  // 合单支付 只有paymentId
				&& !StringUtils.isBlank(paymentId)) {
			Payment payment = paymentService.get(paymentId);
			if (payment == null) {
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
			transactionID = payment.getTransactionNo();
			orderNum = payment.getPaymentNo();
		}
		// 如果2个参数都不为空
		else if(!StringUtils.isBlank(orderId) && !StringUtils.isBlank(paymentId)){
			// 根据paymentId查询订单 如果有多个订单 则使用paymentId
			Payment payment = paymentService.get(paymentId);
			if (payment == null) {
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
			// 根据支付查询订单
			List<OrderDto> orders = orderService.getOrderInfoByPayment(payment);
			if(orders.size() > 1){ // 合单支付
				transactionID = payment.getTransactionNo();
				orderNum = payment.getPaymentNo();
			}// 分单支付
			else if(orders.size() == 1){
				Payment oldPayment = paymentService.getByOrderId(orderId);
				if(oldPayment == null){
					return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
							ApiMsgConstants.FAILED_CODE);
				}
				transactionID = oldPayment.getTransactionNo();
				orderNum = oldPayment.getPaymentNo();
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		if (StringUtils.isBlank(transactionID)) {
			status = ApiConstants.NO;
			map.put("status", status);
			return map;
		}
        String source = params.getString("source");
        if(StringUtils.isNullBlank(source)){
            source = "wap";
        }
        WXPay wxPay = new WXPay();
        if(source.equalsIgnoreCase("mobile")){
            wxPay.initConfigurationForMobile();
        }else{
            wxPay.initConfigurationForWap();
        }
		// 向微信查询支付结果
		ScanPayQueryReqData req = new ScanPayQueryReqData(transactionID,
				orderNum,wxPay.getConfigure());
		try {
			String resString = new ScanPayQueryService(wxPay.getConfigure()).request(req);
			ScanPayQueryResData payQueryResData = (ScanPayQueryResData) Util
					.getObjectFromXML(resString, ScanPayQueryResData.class);
			if (payQueryResData != null
					&& "SUCCESS".equals(payQueryResData.getTrade_state())) {
				status = ApiConstants.YES;
				map.put("status", status);
				map.put("price", new BigDecimal(payQueryResData.getTotal_fee())
						.divide(new BigDecimal("100")));
				// 查询是否为团购支付
				List<Map<String,Object>> resultList = groupBuyCustomerService.getGroupBuyInfo(paymentId);
				if(resultList != null && resultList.size() == 1){
					Map<String,Object> result = resultList.get(0);
					String orderItemType =  result.get("orderItemType") + "";
					if(orderItemType.equals(ApiConstants.GROUPBUY_SELLER)){
						map.put("groupBuySponsorId", result.get("groupBuySponsorId"));
						map.put("type", ApiConstants.SELLER_LAUNCH);
					}else if (orderItemType.equals(ApiConstants.GROUPBUY_BUYER)){
						map.put("groupBuySponsorId", result.get("groupBuySponsorId"));
						map.put("type", ApiConstants.GROUPBUY_BUYER);
					}
				}
				return map;
			}
			logger.debug("微信支付查询返回结果:" + resString);
			status = ApiConstants.NO;
			map.put("status",status);
			map.put("reason",resString);
		} catch (Exception e) {
			status = ApiConstants.NO;
			map.put("status",status);
			map.put("reason","异常退出");
			logger.error(e.getMessage() + e);
		}
		return map;
	}

	private boolean closeAlipayTrade(Payment payment){
		PaymentLog log = new PaymentLog();
		log.setPayment(payment);
		log.setTransactionSuccess("3");
		log.setGatewayCode(ApiConstants.PAY_TYPE_ALIPAY);
		PaymentLog paymentLog = paymentLogService.getPaymentLog(log);
		//取消支付单
		if (paymentLog != null && payment != null) {
			//关闭交易
			if(AlipayUtils.closeTrade(paymentLog.getTransactionNo(), payment.getPaymentNo())){
				logger.error("支付宝关闭交易订单成功" + "支付宝交易号["
						+ paymentLog.getTransactionNo() + "],支付单号["
						+ payment.getPaymentNo() + "]");
				return true;
			}else{
				logger.error("支付宝关闭交易订单失败" + "支付宝交易号["
						+ paymentLog.getTransactionNo() + "],支付单号["
						+ payment.getPaymentNo() + "]");
				return false;
			}
		}
		return true;
	}
	/**
	 * 支付宝支付
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/payment/alipay")
	public Object alipayRequest(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		// 支付类型
		String payment_type = "1";
		// 必填，不能修改
		// 服务器异步通知页面路径
		String notify_url = Global.getConfig("alipay.notify.url");
		// 页面跳转同步通知页面路径
		String return_url = Global.getConfig("alipay.return.url");

		String paymentId = params.getString("paymentId");
		String orderId = params.getString("orderId");
		String source = params.getString("source");
		if(StringUtils.isNullBlank(source)){
			source = "wap";
		}

		if ((StringUtils.isBlank(paymentId) && StringUtils.isBlank(orderId))) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		// 商品描述
		String des = "";
		String orderNum = "";
//		// 店铺id
//		String shopId = "";
//		// 商品id
//		String productId = "";
//		// 商品类型
//		String productType = ApiConstants.NORMAL;
		//总抵扣卷金额
		BigDecimal totalCouponPrice = BigDecimal.ZERO;
		//买家可用抵用劵金额
		BigDecimal availableCouponPrice = BigDecimal.ZERO;
		// 原始金额
		BigDecimal returnPrice = BigDecimal.ZERO;
		// 如果是合单支付，即只有paymentId
		if (StringUtils.isBlank(orderId) && !StringUtils.isBlank(paymentId)) {
			Payment payment = paymentService.get(paymentId);
			if (payment == null) {
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
			orderNum = payment.getPaymentNo();
			closeAlipayTrade(payment);
		} else if (!StringUtils.isBlank(orderId) && StringUtils.isBlank(paymentId)) {// 分单支付，只有orderId
			//如果之前的支付单已经生成，并等待买家付款，取消支付宝订单
			Payment oldPayment = paymentService.getByOrderId(orderId);
			if(oldPayment ==null){
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
			closeAlipayTrade(oldPayment);
			// 更新支付状态
			Payment payment = orderService.autoPayment(orderId);
			orderNum = payment.getPaymentNo();
			paymentId = payment.getId();
		}
		// 如果2个参数都不为空
		else if(!StringUtils.isBlank(orderId) && !StringUtils.isBlank(paymentId)){
			// 根据paymentId查询订单 如果有多个订单 则使用paymentId
			Payment        payment = paymentService.get(paymentId);
			if (payment == null) {
				return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
						ApiMsgConstants.FAILED_CODE);
			}
			List<OrderDto> orders =  orderService.getOrderInfoByPayment(payment);
			if(orders.size() > 1){
				orderNum = payment.getPaymentNo();
				closeAlipayTrade(payment);
			}
			else if(orders.size() == 1){
				Payment oldPayment = paymentService.getByOrderId(orderId);
				if(oldPayment ==null){
					return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
							ApiMsgConstants.FAILED_CODE);
				}
				closeAlipayTrade(oldPayment);
				// 更新支付状态
				Payment p = orderService.autoPayment(orderId);
				orderNum = p.getPaymentNo();
				paymentId = p.getId();
			}
		}
		// 获得总金额
		Map<String, Object> map = orderService.getTotalPriceByPaymentId(paymentId);
		if (map == null || map.size() == 0) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		returnPrice = (BigDecimal) map.get("totalPrice");
		des = (String) map.get("productName") + "等";
//		shopId = (String) map.get("shopId");
//		productId = (String) map.get("productId");
//		productType = (String) map.get("productType");

		totalCouponPrice = (BigDecimal) map.get("totalCouponPrice");
		availableCouponPrice = (BigDecimal) map.get("availableCouponPrice");
		//判断此时的抵扣劵是否够抵扣
		//if(totalCouponPrice.compareTo(availableCouponPrice)>0){
		//		return new MsgEntity(ApiMsgConstants.COUPON_NOT_ENTHOU,
		//				ApiMsgConstants.FAILED_CODE);
		//}
		// 商户订单号
		String out_trade_no = orderNum;
		// 商户网站订单系统中唯一订单号，必填
		// 订单名称
		String subject = des.replaceAll("&([#]|[\\w])+;", "");
		// 必填

		// 付款金额
		String total_fee = returnPrice.toString();
		// 必填
		// 商品展示地址
		String show_url = "";
		Map<String,Object> orderInfo = orderService.getOrderType(orderNum);
		String orderType = (String) orderInfo.get("orderType");
		if(ApiConstants.POSTAGE.equals(orderType)){
			show_url = ApiConstants.VCAT_DOMAIN+"/buyer/views/activities.html";
		}else
		 show_url = ApiConstants.VCAT_DOMAIN+"/buyer/views/orders.html";
//		// 必填，需以http://开头的完整路径，例如：http://www.商户网址.com/myorder.html
//		if (ApiConstants.SUPER.equals(productType)) {
//			show_url = ApiConstants.VCAT_DOMAIN
//					+ "/buyer/views/sample.html?productId=" + productId;
//		} else {
//			show_url = ApiConstants.VCAT_DOMAIN
//					+ "/buyer/views/goods.html?shopId=" + shopId
//					+ "&productId=" + productId+ "&type="+productType;
//		}
		// 订单描述
		String body = subject;
		// 选填

		// 超时时间
		String it_b_pay = "1d";
		// 选填

		// 钱包token
		String extern_token = "";
		// 选填

		// ////////////////////////////////////////////////////////////////////////////////

		// 把请求参数打包成数组
		Map<String, String> paraTemp = new LinkedHashMap<String, String>();
		paraTemp.put("partner", AlipayConfig.partner);
		paraTemp.put("seller_id", AlipayConfig.seller_id);
		paraTemp.put("out_trade_no", out_trade_no);
		paraTemp.put("subject", subject);
		paraTemp.put("body", body);
		paraTemp.put("total_fee", total_fee);
		paraTemp.put("notify_url", notify_url);
		paraTemp.put("service", "alipay.wap.create.direct.pay.by.user");
		paraTemp.put("payment_type", payment_type);
		paraTemp.put("_input_charset", AlipayConfig.input_charset);
		paraTemp.put("it_b_pay", it_b_pay);
		paraTemp.put("extern_token", extern_token);
		paraTemp.put("return_url", return_url);
		paraTemp.put("show_url", show_url);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		if(source.equalsIgnoreCase("wap")){
			String htmlText = AlipaySubmit.buildRequest(paraTemp, "get", "submit");
			resultMap.put("formString", htmlText);
		}else if(source.equalsIgnoreCase("mobile")){
			Map<String, String> paraCopy = new LinkedHashMap<String, String>();
			paraCopy.putAll(paraTemp);
			paraCopy.remove("show_url");
			paraCopy.put("service", "mobile.securitypay.pay");
			//除去数组中的空值和签名参数
			paraCopy = paraFilter(paraCopy);
			String prestr = createLinkString(paraCopy);
			//生成签名结果
			String mysign = RSA.sign(prestr, AlipayConfig.private_key, AlipayConfig.input_charset);
			//签名结果与签名方式加入请求提交参数组中)
			Map<String, String> retMap = new HashMap<String, String>(20);
			retMap.put("link",prestr+"&sign="+"\""+URLEncoder.encode(mysign,"UTF-8")+"\""+"&sign_type="+"\""+AlipayConfig.sign_type+"\"");
			retMap.putAll(paraTemp);

			List<Map<String,Object>> resultList = groupBuyCustomerService.getGroupBuyInfo(paymentId);
			if(resultList != null && resultList.size() == 1){
				Map<String,Object> result = resultList.get(0);
				String orderItemType =  result.get("orderItemType") + "";
				if(orderItemType.equals(ApiConstants.GROUPBUY_SELLER)){
					resultMap.put("groupBuySponsorId", result.get("groupBuySponsorId"));
					resultMap.put("type", ApiConstants.SELLER_LAUNCH);
				}else if (orderItemType.equals(ApiConstants.GROUPBUY_BUYER)){
					resultMap.put("groupBuySponsorId", result.get("groupBuySponsorId"));
					resultMap.put("type", ApiConstants.GROUPBUY_BUYER);
				}
			}
			resultMap.put("params", retMap);
		}

		logger.debug("支付宝生成支付表单成功，" + "totalPrice[" + returnPrice + "],"
				+ "支付单号为[" + orderNum + "]。" + "请求返回数据：" + resultMap);
		return resultMap;
	}

	public static Map<String, String> paraFilter(Map<String, String> sArray) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		if (sArray == null || sArray.size() <= 0) {
			return result;
		}
		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, "\""+value+"\"");
		}
		return result;
	}

	private String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	/**
	 * 支付宝同步回调
	 */
	@RequestMapping("/anon/payment/alipay/return")
	public String alipayReturn(HttpServletRequest request,
			HttpServletResponse response) {
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		// 商户订单号
		String total_fee = request.getParameter("total_fee");
		String des = request.getParameter("subject");
		String paymentNum  = request.getParameter("out_trade_no");
		// 支付宝交易号
		if(StringUtils.isNullEmpty(request.getParameter("trade_no"))){
			return "";
		}
		String trade_no = new String(request.getParameter("trade_no"));
		Map<String,Object> map = orderService.getOrderType(paymentNum);
		String orderType = (String) map.get("orderType");
		String fromPage = "";
		if(ApiConstants.POSTAGE.equals(orderType)){
			fromPage = ApiConstants.ACTIVITY;
		}
		// 交易状态
		String trade_status = request.getParameter("trade_status");
		logger.debug("支付宝支付同步回调 支付单号 =  "+paymentNum+" trade_no:"+trade_no+" trade_status:"+trade_status);
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

		// 计算得出通知验证结果
		boolean verify_result = AlipayNotify.verify(request);

		if (verify_result) {// 验证成功
			// ////////////////////////////////////////////////////////////////////////////////////////
			// 请在这里加上商户的业务逻辑程序代码
			//如果状态是交易创建，等待卖家付款，记录日志，并更新支付单 商户号
			if("WAIT_BUYER_PAY".equals(trade_status)){
				
				//记录日志
				Payment payment = new Payment();
				payment.setPaymentNo(paymentNum);
				payment.setTransactionNo(trade_no);
				String paymentId = paymentService
						.getPaymentIdByPaymentNum(paymentNum);
				payment.setId(paymentId);
				PaymentLog log = new PaymentLog();
				log.setPayment(payment);
				log.setTransactionSuccess("3");
				log.setGatewayCode(ApiConstants.PAY_TYPE_ALIPAY);
				if(paymentLogService.getPaymentLog(log)==null){
					logger.debug("交易创建，等待卖家付款," + "平台支付单号为[" + paymentNum + "]。" + "支付宝支付单"
							+ "[" + trade_no + "]");
					log.preInsert();
					log.setNote("交易创建，等待卖家付款," + "平台支付单号为[" + paymentNum + "]。" + "支付宝支付单"
							+ "[" + trade_no + "]");
					paymentLogService.insertBypayment(log);
				}
			}
			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			if (trade_status.equals("TRADE_FINISHED")
					|| trade_status.equals("TRADE_SUCCESS")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
				logger.debug("支付宝支付成功,转发通知终端成功页面" + "result:"
						+ request.getParameterMap());
				try {
					String sellerId = paymentService.getSellerId(paymentNum);
					String url = "";
					String groupBuySponsorId = "";
					if(ApiConstants.GROUPBUY_SELLER.equals(orderType)){      // 卖家开团
						groupBuySponsorId = (String) map.get("groupBuySponsorId");
						url = ApiConstants.VCAT_DOMAIN
								+ "/buyer/views/group.html?status="
								+ ApiConstants.YES + "&price="
								+ total_fee
								+ "&productName=" + des + "&fromPage="+fromPage + "&groupBuySponsorId="+groupBuySponsorId + "&type=" + ApiConstants.SELLER_LAUNCH +"&shopId=" + sellerId;
					}
					else if(ApiConstants.GROUPBUY_BUYER.equals(orderType)){ // 买家开/参团
						groupBuySponsorId = (String) map.get("groupBuySponsorId");
						url = ApiConstants.VCAT_DOMAIN
								+ "/buyer/views/group.html?status="
								+ ApiConstants.YES + "&price="
								+ total_fee
								+ "&productName=" + des + "&fromPage="+fromPage + "&groupBuySponsorId="+groupBuySponsorId + "&type=" + ApiConstants.BUYER_LAUNCH +"&shopId=" + sellerId;
					}
					else{
						url = ApiConstants.VCAT_DOMAIN
								+ "/buyer/views/payStatus.html?status="
								+ ApiConstants.YES + "&price="
								+ total_fee
								+ "&productName=" + des + "&fromPage="+fromPage+"&shopId=" + sellerId;
					}
					logger.debug("支付成功，回调 url =  "+url);
					response.sendRedirect(url);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			return "";
		} else {
			try {
				response.sendRedirect(ApiConstants.VCAT_DOMAIN
						+ "/buyer/views/payStatus.html?status="
						+ ApiConstants.NO);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			return "";
		}
	}

	/**
	 * 支付宝异步回调
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/anon/payment/alipay/notify")
	public Object alipayNotify(HttpServletRequest request,
			HttpServletResponse response) {
		RLock lock = DistLockHelper.getLock("alipayNotify");
		try {
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			// 商户订单号
			Payment payment = new Payment();
			String outTradeNo = request.getParameter("out_trade_no");
			if(StringUtils.isBlank(outTradeNo)){
				return "fail";
			}
            payment.setPaymentNo(outTradeNo);
			// 支付宝交易号

			String trade_no = new String(request.getParameter("trade_no"));

			// 交易状态
			String trade_status = new String(request.getParameter("trade_status"));

			logger.debug("支付宝支付异步回调，支付单号:"+outTradeNo+" trade_no:"+trade_no+" trade_status:"+trade_status);

			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

			String paymentId = paymentService
					.getPaymentIdByPaymentNum(outTradeNo);
			if (paymentId == null) {
				return ("fail");
			}
			payment.setId(paymentId);
			payment.setTransactionNo(trade_no);
			if (AlipayNotify.verify(request)) {// 验证成功
				// ////////////////////////////////////////////////////////////////////////////////////////
				// 请在这里加上商户的业务逻辑程序代码

				if("WAIT_BUYER_PAY".equals(trade_status)){
					//记录日志
					PaymentLog log = new PaymentLog();
					log.setPayment(payment);
					log.setTransactionSuccess("3");
					log.setGatewayCode(ApiConstants.PAY_TYPE_ALIPAY);
					if(paymentLogService.getPaymentLog(log)==null){
						logger.debug("交易创建，等待卖家付款," + "平台支付单号为[" + outTradeNo + "]。" + "支付宝支付单"
								+ "[" + trade_no + "]");
						log.preInsert();
						log.setNote("交易创建，等待卖家付款," + "平台支付单号为[" + outTradeNo + "]。" + "支付宝支付单"
								+ "[" + trade_no + "]");
						paymentLogService.insertBypayment(log);
					}
				}
				// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				String status = "";
				if (trade_status.equals("TRADE_FINISHED")) {
					// 判断该笔订单是否在商户网站中已经做过处理
					// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					// 如果有做过处理，不执行商户的业务程序
					// 注意：
					// 退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
					PaymentLog log = new PaymentLog();
					log.preInsert();
					log.setPayment(payment);
					log.setTransactionSuccess(ApiConstants.YES);
					log.setGatewayCode(ApiConstants.PAY_TYPE_ALIPAY);
					log.setNote("支付宝支付单" + "[" + outTradeNo + "]" + "无退款，并支付结束！");
					paymentLogService.insertBypayment(log);
					status = "success";
				} else if (trade_status.equals("TRADE_SUCCESS")) {
					// 判断该笔订单是否在商户网站中已经做过处理
					// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					// 如果有做过处理，不执行商户的业务程序
					// 判断商品是否已经做过业务逻辑处理
					status = callBack(paymentId, trade_no, ApiConstants.PAY_TYPE_ALIPAY,null);
					 logger.debug("支付宝回掉服务器成功，" + "支付单号为[" + outTradeNo + "]。"
								+ "支付宝支付单" + "[" + trade_no + "]");

					// 注意：
					// 付款完成后，支付宝系统发送该交易状态通知
				}
				// ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
				return status;
			} else {// 验证失败
				PaymentLog log = new PaymentLog();
				log.preInsert();
				log.setPayment(payment);
				log.setTransactionSuccess(ApiConstants.NO);
				log.setGatewayCode(ApiConstants.PAY_TYPE_ALIPAY);
				log.setNote("支付失败," + "平台支付单号为[" + outTradeNo + "]。" + "支付宝支付单"
						+ "[" + trade_no + "]");
				logger.debug("支付宝回掉服务器失败" + "平台支付单号为[" + outTradeNo + "]。"
						+ "支付宝支付单" + "[" + trade_no + "]");
				paymentLogService.insertBypayment(log);
				return ("fail");
			}
		} finally {
			if(lock.isLocked())
				lock.unlock();
		}
//		 Map<String, Object> map = new HashMap<String, Object>();
//		 map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		 map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		 String paymentId = request.getParameter("paymentId");
//		 String orderId = request.getParameter("orderId");
//		 String transactionNo = IdGen.getRandomNumber(8);
//		 // 分单支付，只有orderId
//		List<String> list = paymentService.getOrderIds(paymentId);
//		if (list == null || list.isEmpty()) {
//			 map.put("status", ApiConstants.YES);
//			return map;
//		}
//		 if (!StringUtils.isBlank(orderId) && StringUtils.isBlank(paymentId))
//		 {
//		 // 从新生成支付单
//		 // 更新支付状态
//		 Payment payment = orderService.autoPayment(orderId);
//		 paymentId = payment.getId();
//		 }
//		 String result = callBack(paymentId, transactionNo, "zfb");
//		 if ("success".equals(result)) {
//		 map.put("status", ApiConstants.YES);
//		 } else {
//		 map.put("status", ApiConstants.NO);
//		 }
//		 return map;
	}
}
