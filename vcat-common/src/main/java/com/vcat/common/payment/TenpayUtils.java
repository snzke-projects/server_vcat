package com.vcat.common.payment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tenpay.client.TenpayHttpClient;
import com.tenpay.client.XMLClientResponseHandler;
import com.tenpay.wap.WapPayInitRequestHandler;
import com.tenpay.wap.WapPayRequestHandler;
import com.vcat.common.config.Global;

public class TenpayUtils {
	public final static String BARGAINOR_ID = Global
			.getConfig("tenpay.bargainor.id");
	public final static String BARGAINOR_KEY = Global
			.getConfig("tenpay.bargainor.key");
	public final static String NOTIFY_URL = Global
			.getConfig("tenpay.notify.url");
	public final static String CALLBACK_URL = Global
			.getConfig("tenpay.callback.url");
	private final static Logger LOG = LoggerFactory
			.getLogger(TenpayUtils.class);

	/**
	 * 生成支付链接，如果产生错误，以ERROR开头作为返回信息
	 * 
	 * @param request
	 * @param response
	 * @param orderNo
	 *            订单号
	 * @param totalFee
	 *            支付金额
	 * @param timeExpire 
	 * @param timeStart 
	 * @return
	 * @throws Exception
	 */
	public static String payRequest(HttpServletRequest request,
			HttpServletResponse response, String productName, String orderNo,
			int totalFee,String attach, String timeStart, String timeExpire) throws Exception {
		// 帐号(财付通商户号或者财付通帐号)
		String bargainorId = BARGAINOR_ID;
		// 密钥
		String key = BARGAINOR_KEY;
		// 创建支付初始化请求示例
		WapPayInitRequestHandler reqHandler = new WapPayInitRequestHandler(
				request, response);
		// 初始化
		reqHandler.init();
		// 设置密钥
		reqHandler.setKey(key);
		// -----------------------------
		// 设置请求参数
		// -----------------------------
		reqHandler.setParameter("sp_billno", orderNo);
		reqHandler.setParameter("desc", productName);
		reqHandler.setParameter("bargainor_id", bargainorId);
		reqHandler.setParameter("total_fee", totalFee+"");
		reqHandler.setParameter("notify_url", NOTIFY_URL);
		reqHandler.setParameter("callback_url", CALLBACK_URL);
		//设置自定义参数
		reqHandler.setParameter("attach",attach);
		reqHandler.setParameter("time_start",timeStart);
		reqHandler.setParameter("time_expire",timeExpire);
		// 获取请求带参数的url
		String requestUrl = reqHandler.getRequestURL();

		// 获取debug信息
		String debuginfo = reqHandler.getDebugInfo();
		LOG.debug("debuginfo:" + debuginfo);
		LOG.debug("requestUrl:" + requestUrl);

		// 创建TenpayHttpClient，后台通信
		TenpayHttpClient httpClient = new TenpayHttpClient();
		// 设置请求内容
		httpClient.setReqContent(requestUrl);
		// 远程调用
		if (httpClient.call()) {
			String resContent = httpClient.getResContent();
			LOG.debug("responseContent:" + resContent);
			// ----------------------
			// 应答处理,获取token_id
			// ----------------------
			XMLClientResponseHandler resHandler = new XMLClientResponseHandler();
			resHandler.setContent(resContent);
			String token_id = resHandler.getParameter("token_id");
			if (!token_id.equals("")) {
				// 生成支付请求
				WapPayRequestHandler wapPayRequestHandler = new WapPayRequestHandler(
						request, response);
				wapPayRequestHandler.init();
				wapPayRequestHandler.setParameter("token_id", token_id);
				String wapPayRequestUrl = wapPayRequestHandler.getRequestURL();
				return wapPayRequestUrl;
			} else {
				// 获取token_id调用失败 ，显示错误 页面
				LOG.error("获取token_id调用失败:"
						+ resHandler.getParameter("err_info"));
				return "ERROR:获取token_id失败";
			}
		} else {
			LOG.error("后台调用失败:" + httpClient.getResponseCode()
					+ httpClient.getErrInfo());
			return "ERROR:调用失败";
		}
	}
}
