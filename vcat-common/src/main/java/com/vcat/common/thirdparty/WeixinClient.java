package com.vcat.common.thirdparty;

import java.io.*;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vcat.common.config.Global;

public class WeixinClient {
	private final static Logger logger = LoggerFactory.getLogger(WeixinClient.class);
	public final static String MP_APP_ID = Global.getConfig("weixin.mp.appid");
	public final static String MP_APP_SECRET = Global
			.getConfig("weixin.mp.appsecret");
	private static final Logger LOG = LoggerFactory
			.getLogger(WeixinClient.class);
	public final static String ORDER_SUCESS_MSG_ID = "4Wsu3k7R_wRz5WScKOx9gUiZq_IdA8AWkkNKyfJ_30k";//订单支付成功
	public final static String GROUP_BUY_SUCESS_MSG_ID = "be8Gww_yXUUMLIv2yLjkhrSI-IxuUC9ssGvonzw1u24";//拼团成功

	public static String sendGroupBuySuccessMsg(String openId, String productName,
											   String orderId, String orderNumber,String price){
		String accessToken = getAccessToken();
		JSONObject params = JSONObject.parseObject(accessToken);
		accessToken = params.getString("access_token");
		String body = "{\n" +
				"           \"touser\":\""+openId+"\",\n" +
				"           \"template_id\":\""+GROUP_BUY_SUCESS_MSG_ID+"\",\n" +
				"           \"url\":\"https://m.v-cat.cn/buyer/views/orderDetail.html?orderId="+orderId+"\",            \n" +
				"           \"data\":{\n" +
				"                   \"first\": {\n" +
				"                       \"value\":\"您参团的商品［"+productName+"］已组团成功\",\n" +
				"                       \"color\":\"#173177\"\n" +
				"                   },\n" +
				"                   \"keyword1\":{\n" +
				"                       \"value\":\""+price+"\",\n" +
				"                       \"color\":\"#173177\"\n" +
				"                   },\n" +
				"                   \"keyword2\": {\n" +
				"                       \"value\":\""+orderNumber+"\",\n" +
				"                       \"color\":\"#173177\"\n" +
				"                   },\n" +
				"                   \"remark\":{\n" +
				"                       \"value\":\"点击跟踪订单详情！\\n(右下角“我的/我的订单”也可以进入哦)\",\n" +
				"                       \"color\":\"#173177\"\n" +
				"                   }\n" +
				"           }\n" +
				"       }";
		try {
			return sendMsg(accessToken, ORDER_SUCESS_MSG_ID, body);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "{'message':'failed to get data.','status':'500'}";
	}

	public static String sendOrderSuccessMsg(String openId,String productName, String orderId, String price){
		String accessToken = getAccessToken();
		JSONObject params = JSONObject.parseObject(accessToken);
		accessToken = params.getString("access_token");
		String body = "{\n" +
				"           \"touser\":\""+openId+"\",\n" +
				"           \"template_id\":\""+ORDER_SUCESS_MSG_ID+"\",\n" +
				"           \"url\":\"https://m.v-cat.cn/buyer/views/orderDetail.html?orderId="+orderId+"\",            \n" +
				"           \"data\":{\n" +
				"                   \"first\": {\n" +
				"                       \"value\":\"恭喜您购买成功！\",\n" +
				"                       \"color\":\"#173177\"\n" +
				"                   },\n" +
				"                   \"orderProductName\":{\n" +
				"                       \"value\":\""+productName+"\",\n" +
				"                       \"color\":\"#173177\"\n" +
				"                   },\n" +
				"                   \"orderMoneySum\": {\n" +
				"                       \"value\":\""+price+"元\",\n" +
				"                       \"color\":\"#173177\"\n" +
				"                   },\n" +
				"                   \"Remark\":{\n" +
				"                       \"value\":\"点击跟踪订单详情！\\n(右下角“我的/我的订单”也可以进入哦)\",\n" +
				"                       \"color\":\"#173177\"\n" +
				"                   }\n" +
				"           }\n" +
				"       }";
		try {
			return sendMsg(accessToken, ORDER_SUCESS_MSG_ID, body);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "{'message':'failed to get data.','status':'500'}";
	}

	public static String sendMsg(String accessToken, String msgId, String body) throws UnsupportedEncodingException {
		LOG.debug("发送消息:"+body);
		CloseableHttpClient httpclient = HttpClients.createMinimal();
		HttpPost httpPost = new HttpPost(
				"https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken);
		HttpEntity postEntity = new ByteArrayEntity(body.getBytes("UTF-8"));
		httpPost.setEntity(postEntity);
		try {
			CloseableHttpResponse response = httpclient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if (statusLine.getStatusCode() >= 300) {
				throw new HttpResponseException(statusLine.getStatusCode(),
						statusLine.getReasonPhrase());
			}
			if (entity == null) {
				throw new ClientProtocolException(
						"Response contains no content");
			}
			InputStream is = entity.getContent();
			String ret = getStringFromInputStream(is);
			LOG.debug("收到消息:"+ret);
			return ret;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return "{'message':'failed to get data.','status':'500'}";
	}


	public static String getAccessToken() {
		CloseableHttpClient httpclient = HttpClients.createMinimal();
		HttpGet httpGet = new HttpGet(
				"https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+MP_APP_ID+"&secret="+MP_APP_SECRET);
		try {
			CloseableHttpResponse response = httpclient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if (statusLine.getStatusCode() >= 300) {
				throw new HttpResponseException(statusLine.getStatusCode(),
						statusLine.getReasonPhrase());
			}
			if (entity == null) {
				throw new ClientProtocolException(
						"Response contains no content");
			}
			InputStream is = entity.getContent();
			return getStringFromInputStream(is);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return "{'message':'failed to get data.','status':'500'}";
	}


	/**
	 * https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
	 * @return
     */
	public static String getUserInfo(String accessToken, String openId) {
		CloseableHttpClient httpclient = HttpClients.createMinimal();
		HttpGet httpGet = new HttpGet(
				"https://api.weixin.qq.com/sns/userinfo?access_token="
						+accessToken+"&openid="+openId+"&lang=zh_CN");
		try {
			CloseableHttpResponse response = httpclient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if (statusLine.getStatusCode() >= 300) {
				throw new HttpResponseException(statusLine.getStatusCode(),
						statusLine.getReasonPhrase());
			}
			if (entity == null) {
				throw new ClientProtocolException(
						"Response contains no content");
			}
			InputStream is = entity.getContent();
			return getStringFromInputStream(is);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return "{'message':'failed to get data.','status':'500'}";
	}

	/**
	 *  openid:{"access_token":"OezXcEiiBSKSxW0eoylIeJ6Y53OQy-lDPCImiRD6JQAFtfk3fayx7g0Xp-OFNzlqSp6UVqFE0LJ3t6tqXD6KWTqhzwFf9Gby11qRjdPMgWIG4Xc0I3juB7RnAa8iZAa2kEVi78g_f8_pM5JG9_Jhfw",
	 *  "expires_in":7200,"refresh_token":"OezXcEiiBSKSxW0eoylIeJ6Y53OQy-lDPCImiRD6JQAFtfk3fayx7g0Xp-OFNzlq3eSKlyd-R2buf6-x_83ird9dajn997W1IWfY5HO8GCgYPY_tkRI8zauCnjKGZtQnutW6ahxbxXCNh_dTyNHQXw",
	 *  "openid":"ov4RDs06-rXgPGHL_ssdpzN5Q9QE","scope":"snsapi_base","unionid":"ovEtSs_8qv-KvPOh5O9pxRborY3s"}

	 * @param code
	 * @return
	 */
	public static String getOpenId(String code) {
		CloseableHttpClient httpclient = HttpClients.createMinimal();
		HttpGet httpGet = new HttpGet(
				"https://api.weixin.qq.com/sns/oauth2/access_token?appid="
						+ MP_APP_ID + "&secret=" + MP_APP_SECRET + "&code="
						+ code + "&grant_type=authorization_code");
		try {
			CloseableHttpResponse response = httpclient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if (statusLine.getStatusCode() >= 300) {
				throw new HttpResponseException(statusLine.getStatusCode(),
						statusLine.getReasonPhrase());
			}
			if (entity == null) {
				throw new ClientProtocolException(
						"Response contains no content");
			}
			InputStream is = entity.getContent();
			return getStringFromInputStream(is);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return "{'message':'failed to get data.','status':'500'}";
	}

	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}
