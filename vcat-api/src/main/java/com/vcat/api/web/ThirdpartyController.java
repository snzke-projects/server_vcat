package com.vcat.api.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vcat.common.constant.ApiMsgConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.tencent.common.Configure;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.protocol.pay_protocol.WebPayReqData;
import com.tencent.protocol.pay_protocol.WebPayResData;
import com.tencent.service.WebPayService;
import com.vcat.api.service.CloudImageService;
import com.vcat.api.service.ExpressApiService;
import com.vcat.api.service.OrderService;
import com.vcat.api.service.PaymentLogService;
import com.vcat.api.service.PaymentService;
import com.vcat.api.service.ShareEarningService;
import com.vcat.api.service.ThirdPartyLoginService;
import com.vcat.common.config.Global;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.mapper.JsonMapper;
import com.vcat.common.thirdparty.WeixinClient;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.kuaidi100.pojo.NoticeRequest;
import com.vcat.kuaidi100.pojo.NoticeResponse;
import com.vcat.module.ec.dto.OrderDto;
import com.vcat.module.ec.entity.CloudImage;
import com.vcat.module.ec.entity.Payment;

@RestController
@ApiVersion({1,2})
public class ThirdpartyController extends RestBaseController {
	@Autowired
	private CloudImageService cloudImageService;
	@Autowired
	private ThirdPartyLoginService thirdPartyLoginService;

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

	@RequestMapping("/anon/qcloud/image/callback")
	public void imageCallback(HttpServletRequest request,
			HttpServletResponse response) {
		String bucket = request.getParameter("bucket");
		String size = request.getParameter("size");
		String appid = request.getParameter("appid");
		String magiContext = request.getParameter("magic_context");
		String sha = request.getParameter("sha");
		String url = request.getParameter("url");
		String fileid = request.getParameter("fileid");
		CloudImage ci = new CloudImage();
		ci.preInsert();
		ci.setAppid(appid);
		ci.setBucket(bucket);
		ci.setFileid(fileid);
		ci.setSha(sha);
		ci.setUrl(url);
		ci.setSize(size);
		ci.setMagiContext(magiContext);
		cloudImageService.insert(ci);
	}

	@RequestMapping("/anon/qcloud/image/error/callback")
	public void imageErrorCallback(HttpServletRequest request,
			HttpServletResponse response) {
		Enumeration<?> enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paraName = (String) enu.nextElement();
			logger.info("Q Colud Image info:" + paraName + ": "
					+ request.getParameter(paraName));
		}
	}

	/**
	 * 用于任意页面的微信登录 参考 @see
	 * http://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/anon/wechat/login")
	public void wechatAutoLogin(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		// 判断是否为微信，如果是，自动注册，自动登录，并转发链接加上token
		String code = request.getParameter("code");
		String token = "";
		String url = request.getParameter("url");
		String openId = "";
		String accessToken = "";
		if (!StringUtils.isBlank(code) && !StringUtils.isBlank(url)) {
			logger.info("wxCode=" + code);
			String openJson = WeixinClient.getOpenId(code);
			logger.info("WeixinClient.getOpenId=" + openJson);
			if(openJson.contains("errcode")){
				String enUrl = URLEncoder.encode(url,"UTF-8");
				logger.debug("EncodeUrl:"+enUrl);
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
						"appid=" + Global.getConfig("weixin.mp.appid")+
						"&redirect_uri=" + enUrl +
						"&response_type=code" +
						"&scope=snsapi_userinfo" +
						"#wechat_redirect";
			}else {
				JSONObject params = JSONObject.parseObject(openJson);
				openId = params.getString("openid");
				accessToken = params.getString("access_token");
				token = thirdPartyLoginService.auto(code, openId, accessToken);
			}
		} else {
			logger.warn("code或者url为空!");
			url = ApiConstants.VCAT_DOMAIN + "/buyer/views/store.html";
		}
		logger.debug("redirect_url:"+url);
		Cookie cookie = new Cookie("token",token);
		cookie.setMaxAge(604800);
		cookie.setPath("/");
		response.addCookie(cookie);
		
		Cookie cookie1 = new Cookie("openId",openId);
		cookie1.setMaxAge(604800);
		cookie1.setPath("/");
		response.addCookie(cookie1);
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * @TODO 删除这个方法
	 * 分享商品或者店铺回调 参考 @see
	 * http://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html
	 * 
	 * @param request
	 * @param response
	 */
	@Deprecated
	@RequestMapping("/anon/share/callback")
	public void shareCallback(HttpServletRequest request,
			HttpServletResponse response) {
		// 判断是否为微信分享，如果是，自动注册，自动登录，并转发链接加上token
		String code = request.getParameter("code");
		// String code = "031b9e73eda2228701757757899e722z";
		String token = null;
		String url = "";
		String openId = "";
		String accessToken = "";
		if (!StringUtils.isBlank(code)) {
			logger.info("wxCode=" + code);
			String openJson = WeixinClient.getOpenId(code);
			JSONObject params = JSONObject.parseObject(openJson);
			openId = params.getString("openid");
			accessToken = params.getString("access_token");
			logger.info("wxopenid=" + openId);
			token = thirdPartyLoginService.auto(code, openId, accessToken);
		}
		// 获取商品id，如果商品id为空，表示分享店铺
		String productId = request.getParameter("productId");
		String shopId = request.getParameter("shopId");
		//分享到什么平台，如qq ，wx
		String shareType = request.getParameter("shareType");
		if(StringUtils.isBlank(shareType)){
			logger.debug("shareType is blank, set Wechat");
			shareType = ApiConstants.SHARE_TYPE_WX;
		}
		if (StringUtils.isBlank(productId) && !StringUtils.isBlank(shopId)) {
			// 转发到shop首页
			logger.debug("转发到shop首页");
			url = ApiConstants.VCAT_DOMAIN + "/buyer/views/store.html?shopId="
					+ shopId+"&shareType=" + shareType;
		}
		if (!StringUtils.isBlank(productId) && !StringUtils.isBlank(shopId)) {
			// 转发到productId
			logger.debug("转发到product");
			url = ApiConstants.VCAT_DOMAIN + "/buyer/views/goods.html?shopId="
					+ shopId + "&productId=" + productId+"&shareType=" + shareType;
			//获得分享奖励
			logger.debug("开始获得分享收入：分享店铺id = "+shopId+";分享商品id = "+productId);
			shareEarningService.addShareEarning(shopId,productId,shareType);
		}
		if(!StringUtils.isNullBlank(token)) {
			Cookie cookie = new Cookie("token", token);
			logger.debug("token = " + token + "--------------------");
			cookie.setMaxAge(604800);
			cookie.setPath("/");
			response.addCookie(cookie);
		}

		if(!StringUtils.isNullBlank(openId)) {
			Cookie cookie1 = new Cookie("openId", openId);
			cookie1.setMaxAge(604800);
			cookie1.setPath("/");
			response.addCookie(cookie1);
		}
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 快递回调
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/anon/kuaidi100/callback")
	public NoticeResponse shippingCallback(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		NoticeResponse resp = new NoticeResponse();
		resp.setResult(false);
		resp.setReturnCode("500");
		resp.setMessage("保存失败");
		try {
			String param = request.getParameter("param");
			logger.debug("request:" + param);
			NoticeRequest nReq = (NoticeRequest) JsonMapper.fromJsonString(
					param, NoticeRequest.class);
			String code = nReq.getLastResult().getCom();
			String no = nReq.getLastResult().getNu();
			expressService.insert(code, no, param);
			resp.setResult(true);
			resp.setReturnCode("200");
			resp.setMessage("保存成功");
			logger.debug("response:" + JsonMapper.toJsonString(resp));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return resp;
	}

//	/**
//	 * 微信支付没有openid时获取prepay信息
//	 * @deprecated
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws IOException
//	 */
//	@RequestMapping("/anon/payment/weixin/request/callback")
//	public void weiPayRequestCallBack(HttpServletRequest request,
//			HttpServletResponse response) throws IOException {
//		String remoteIp = StringUtils.getRemoteAddr(request);
//		// 判断是否为微信分享，如果是，自动注册，自动登录，并转发链接加上token
//		String code = request.getParameter("code");
//		// h5回掉页面
//		String url = ApiConstants.VCAT_DOMAIN + "/buyer/views/wechatpay.html";
//		String openId = "";
//		if (!StringUtils.isBlank(code)) {
//			logger.info("wxCode=" + code);
//			String openJson = WeixinClient.getOpenId(code);
//			JSONObject params = JSONObject.parseObject(openJson);
//			openId = params.getString("openid");
//			logger.info("wxopenid=" + openId);
//		}
//		String paymentId = request.getParameter("paymentId");
//		String orderId = request.getParameter("orderId");
//		logger.info("orderId=" + openId + "," + "paymentId=" + paymentId);
//		if (((StringUtils.isBlank(paymentId) && StringUtils.isBlank(orderId)))
//				|| StringUtils.isBlank(openId)) {
//			url = url + "?errCode=400&errMsg="
//					+ ApiMsgConstants.REQUEST_ERROR_MSG;
//			response.sendRedirect(url);
//			return;
//		}
//		// 订单描述
//		String id = "";
//		// 订单类型
//		String type = "";
//		// 商品描述
//		String des = "";
//		String orderNum = "";
//		// 金额单位为分
//		int totalPrice = 0;
//		// 原始金额
//		BigDecimal returnPrice = BigDecimal.ZERO;
//		String attach = "";
//		// 如果是合单支付，即只有paymentId
//		if (StringUtils.isBlank(orderId) && !StringUtils.isBlank(paymentId)) {
//			Payment payment = paymentService.get(paymentId);
//			id = paymentId;
//			type = "payment";
//			if (payment == null) {
//				url = url + "?errCode=400&errMsg="
//						+ ApiMsgConstants.REQUEST_ERROR_MSG;
//				response.sendRedirect(url);
//				return;
//			}
//			orderNum = payment.getPaymentNo();
//			// 获得总金额
//			Map<String, Object> map = orderService
//					.getTotalPriceByPaymentId(paymentId);
//			if (map == null || map.size() == 0) {
//				url = url + "?errCode=400&errMsg="
//						+ ApiMsgConstants.REQUEST_ERROR_MSG;
//				response.sendRedirect(url);
//				return;
//			}
//			returnPrice = (BigDecimal) map.get("totalPrice");
//			totalPrice = returnPrice == null ? 0 : returnPrice.multiply(
//					new BigDecimal(100)).intValue();
//			attach = paymentId;
//			des = (String) map.get("productName");
//		} else
//		// 分单支付，只有orderId
//		if (!StringUtils.isBlank(orderId) && StringUtils.isBlank(paymentId)) {
//			id = orderId;
//			type = "order";
//			// 从新生成支付单
//			// 更新支付状态
//			Payment payment = orderService.autoPayment(orderId);
//			orderNum = payment.getPaymentNo();
//			// 获取金额
//			OrderDto order = orderService.getOrder(orderId, null, 2);
//			if (order == null) {
//				url = url + "?errCode=400&errMsg="
//						+ ApiMsgConstants.REQUEST_ERROR_MSG;
//				response.sendRedirect(url);
//				return;
//			}
//			des = order.getProductItems().get(0).getProductName();
//			returnPrice = order.getTotalPrice();
//			totalPrice = returnPrice == null ? 0 : returnPrice.multiply(
//					new BigDecimal(100)).intValue();
//			attach = payment.getId();
//		}
//		logger.debug("totalPrice:" + returnPrice);
//		String timeStart = DateUtils.getDate("yyyyMMddHHmmss");
//		String timeExpire = DateUtils.getDate(1, "yyyyMMddHHmmss");
//		String notifyUrl = Global.getConfig("wechat.pay.notify.url");
//		String prepayId = "";
//		// 组装返回值
//		JSONObject returnMap = new JSONObject();
//		returnMap.put("appId", Configure.getAppid());
//		returnMap.put("timeStamp", System.currentTimeMillis() + "");
//		returnMap.put("nonceStr",
//				RandomStringGenerator.getRandomStringByLength(32));
//		returnMap.put("signType", "MD5");
//		// 组装微信支付信息
//		WebPayReqData webPayReqData = new WebPayReqData(des, attach, orderNum,
//				totalPrice, "WEB", remoteIp, timeStart, timeExpire, "",
//				notifyUrl, "JSAPI", "", openId);
//
//		try {
//			String resString = new WebPayService().request(webPayReqData);
//			WebPayResData webPayResData = (WebPayResData) Util
//					.getObjectFromXML(resString, WebPayResData.class);
//			if (!"SUCCESS".equals(webPayResData.getResult_code())) {
//				url = url + "?errCode=400&errMsg="
//						+ ApiMsgConstants.GET_SIGN_FAILED + ","
//						+ webPayResData.getReturn_msg();
//				response.sendRedirect(url);
//				return;
//			}
//			logger.debug(webPayResData.getPrepay_id());
//			prepayId = webPayResData.getPrepay_id();
//			returnMap.put("package", "prepay_id=" + prepayId);
//			returnMap.put("paySign", Signature.getSign(returnMap));
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		url = url + "?prepay="
//				+ URLEncoder.encode(returnMap.toString(), "UTF-8") + "&id="
//				+ id + "&type=" + type;
//		// 回掉页面
//		try {
//			response.sendRedirect(url);
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//		}
//	}

}
