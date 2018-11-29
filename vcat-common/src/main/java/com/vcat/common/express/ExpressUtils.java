package com.vcat.common.express;

import com.alibaba.fastjson.JSON;
import com.vcat.common.config.Global;
import com.vcat.common.mapper.JsonMapper;
import com.vcat.kuaidi100.pojo.TaskRequest;
import com.vcat.kuaidi100.pojo.TaskResponse;
import com.vcat.module.common.entity.Logistics;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 快递查询接口 http://m.kuaidi100.com/query?type=shentong&postid=968701410621
 * 
 * @author ylin
 *
 */
public class ExpressUtils {
	public final static String SERVER_URL = "http://m.kuaidi100.com/query";
	public final static String POLL_SERVER_URL = Global
			.getConfig("kuaidi100.poll.url");
	public final static String CALLBACK_URL = Global
			.getConfig("kuaidi100.callback.url");
	public final static String KEY = Global.getConfig("kuaidi100.Key");
//    public final static String POLL_SERVER_URL = "http://www.kuaidi100.com/poll";
//    public final static String CALLBACK_URL = "http://m.v-cat.cn/vcat-api/v1/anon/kuaidi100/callback";
//    public final static String KEY = "GDIMbPDh7307";
	private static final Logger LOG = LoggerFactory
			.getLogger(ExpressUtils.class);

	/**
	 * 查询快递信息
	 * @deprecated
	 * @param com
	 *            快递公司
	 * @param num
	 *            单号
	 * @return 运单对象
	 */
	public static Logistics queryExpress(String com, String num) {
		return JSON.parseObject(queryExpressJSON(com, num), Logistics.class);
	}

	/**
	 * 查询快递信息
	 * @deprecated
	 * @param com
	 *            快递公司
	 * @param num
	 *            运单号
	 * @return 运单信息JSON字符串
	 */
	public static String queryExpressJSON(String com, String num) {
		CloseableHttpClient httpclient = HttpClients.createMinimal();
		HttpGet httpGet = new HttpGet(SERVER_URL + "?type=" + com + "&postid="
				+ num);
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

	/**
	 * 根据快递公司和单号订阅快递信息
	 * 
	 * @param code
	 * @param no
	 * @return
	 */
	public static TaskResponse subscribeExpress(String code, String no) {
		TaskResponse resp = new TaskResponse();
		TaskRequest req = new TaskRequest();
		req.setCompany(code);
		req.setNumber(no);
		req.getParameters().put("callbackurl", CALLBACK_URL);
		req.setKey(KEY);
		try {
			String ret = postData(JsonMapper.toJsonString(req));
			resp = (TaskResponse) JsonMapper.fromJsonString(ret,
					TaskResponse.class);
			if (resp.getResult() == true) {
				LOG.info("订阅成功[" + resp + "]");
			} else {
				LOG.error("订阅失败[" + resp + "]");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		LOG.info(JsonMapper.toJsonString(resp));
		return resp;
	}

	public static String postData(String param) {
		CloseableHttpClient httpclient = HttpClients.createMinimal();
		HttpPost httpPost = new HttpPost(POLL_SERVER_URL);
		List<NameValuePair> postParameters = null;
		try {
			postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("schema", "json"));
			postParameters.add(new BasicNameValuePair("param", param));
			httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
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
		return "{'message':'failed to get data.','returnCode':'500','result':false}";
	}
}
