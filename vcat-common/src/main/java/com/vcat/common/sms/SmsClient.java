package com.vcat.common.sms;

import com.alibaba.fastjson.JSON;
import com.vcat.common.config.Global;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class SmsClient {
	private static Logger LOG = LoggerFactory.getLogger(SmsClient.class);

	// 查账户信息的http地址
	private final static String URI_GET_USER_INFO = Global
			.getConfig("sms.user");
	// 通用发送接口的http地址
	private final static String URI_SEND_SMS = Global.getConfig("sms.send");
	// 模板发送接口的http地址
	private final static String URI_TPL_SEND_SMS = Global
			.getConfig("sms.tpl.send");
	// 编码格式。发送编码格式统一用UTF-8
	private final static String ENCODING = "UTF-8";
	// API KEY
	private final static String apiKey = Global.getConfig("sms.apiKey");

	/**
	 * 短信模板ID - 订单已发货
	 * 模板内容：
	 *   亲爱的#name#，您购买的订单[#order_no#]已经发货啦~
     *   物流公司：#express_name#
     *   运单号：#shipping_no#
     *   实时跟踪包裹请点击：http://m.kuaidi100.com/
 	 */
	public final static long TPL_SHIPPED = 808091;

    /**
     * 短信模板ID - 店铺售出订单已发货
     * 模板内容：
     *   亲爱的V猫店主，您小店售出的订单#order_no#已经发货啦~
     *   物流公司：#express_name#
     *   运单号：#shipping_no#
     *   实时跟踪包裹请点击：http://m.kuaidi100.com/
     */
    public final static long TPL_SHOP_SHIPPED = 1450661;

	/**
	 * 短信模板ID - 退款成功
	 * 模板内容：
	 * 【V猫小店】亲爱的#name#，您的订单：#order_no#退款成功！
	 */
	public final static long TPL_REFUND_SUCC = 808433;

	/**
	 * 短信模板ID - 退款失败
	 * 模板内容：
	 * 【V猫小店】亲爱的#name#，您的订单：#order_no#退款失败！ 失败原因：#msg#
	 */
	public final static long TPL_REFUND_FAILURE = 808431;

	/**
	 * 短信模板ID - 提醒拿样
	 * 模板内容：
	 * 【V猫小店】亲爱的#name#，您的商品：#productName#即将在#endDay#下架，请及时拿样。
	 */
	public final static long TPL_TAKE_PRODUCT = 856209;

    /**
     * 短信模板ID - 提现失败
     * 模板内容：
     * 【V猫小店】亲爱的#name#，您在V猫小店中提现#amount#元失败！
     * 失败原因：#msg#
     * 详询：028-62695731
     */
    public final static long TPL_WITHDRAWAL_FAILURE = 1124689;


	/**
	 * 取账户信息
	 *
	 * @return json格式字符串
	 * @throws java.io.IOException
	 */
	public static String getUserInfo() throws IOException, URISyntaxException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("apikey", apiKey);
		return post(URI_GET_USER_INFO, params);
	}

	/**
	 * 通用接口发短信（推荐）使用前需要到短信网关配置模板，不然发送不会成功
	 *
	 * @param text
	 *            　短信内容
	 * @param mobile
	 *            　接受的手机号
	 * @return json格式字符串
	 * @throws IOException
	 */
	public static String sendSms(String text, String mobile) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("apikey", apiKey);
		params.put("text", text);
		params.put("mobile", mobile);
		return post(URI_SEND_SMS, params);
	}

	/**
	 * 通过模板发送短信(不推荐，建议使用通用接口)
	 *
	 * @param tpl_id
	 *            　模板id
	 * @param tpl_value
	 *            　模板变量值
	 * @param mobile
	 *            　接受的手机号
	 * @return json格式字符串
	 */
	public static SmsResult tplSendSms(long tpl_id, String tpl_value, String mobile){
		Map<String, String> params = new HashMap<String, String>();
		params.put("apikey", apiKey);
		params.put("tpl_id", String.valueOf(tpl_id));
		params.put("tpl_value", tpl_value);
		params.put("mobile", mobile);
		String resultStr = post(URI_TPL_SEND_SMS, params);
		SmsResult smsResult = JSON.parseObject(resultStr,SmsResult.class);
		if(null != smsResult && SmsResult.SUCC.equals(smsResult.getCode())){
			LOG.info("向[" + mobile + "]发送短信[" + tpl_id + "]完成：" + smsResult);
		}else{
			LOG.info("向[" + mobile + "]发送短信[" + tpl_id + "]失败：" + smsResult);
		}
		return smsResult;
	}


	/**
	 * 通过模板发送短信(不推荐，建议使用通用接口)
	 *
	 * @param tpl_id
	 *            　模板id
	 * @param data
	 *            　模板变量值
	 * @param mobile
	 *            　接受的手机号
	 * @return SmsResult
	 * @throws IOException
	 */
	public static SmsResult tplSendSms(long tpl_id, String mobile, Map<String,? extends Object> data)
			throws IOException {
		StringBuffer tplValue = new StringBuffer();

		Set<String> ketSet = data.keySet();

		Iterator<String> keyIterator = ketSet.iterator();
		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			String value = data.get(key) + "";
			tplValue.append("#"+key+"#");
			tplValue.append("=");
			tplValue.append(value);
			tplValue.append("&");
		}
		if(tplValue.length() > 1){
			tplValue.delete(tplValue.length() - 1, tplValue.length());
		}
		return tplSendSms(tpl_id, tplValue.toString(), mobile);
	}

	/**
	 * 基于HttpClient 4.3的通用POST方法
	 *
	 * @param url
	 *            提交的URL
	 * @param paramsMap
	 *            提交<参数，值>Map
	 * @return 提交响应
	 */
	private static String post(String url, Map<String, String> paramsMap) {
		CloseableHttpClient client = HttpClients.createDefault();
		String responseText = "";
		CloseableHttpResponse response = null;
		try {
			HttpPost method = new HttpPost(url);
			if (paramsMap != null) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> param : paramsMap.entrySet()) {
					NameValuePair pair = new BasicNameValuePair(param.getKey(),
							param.getValue());
					paramList.add(pair);
				}
				method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
			}
			response = client.execute(method);
			//client.execute(method);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				responseText = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			LOG.error("send post error:" + e.getMessage(), e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {
				LOG.error("closing error:" + e.getMessage(), e);
			}
		}
		return responseText;
	}

}