package com.vcat.common.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ucpaas.rest.client.AbsRestClient;
import com.ucpaas.rest.client.JsonReqClient;
import com.ucpaas.rest.client.XmlReqClient;

public class UcpSmsRestClient {
	private static Logger LOG = LoggerFactory.getLogger(UcpSmsRestClient.class);

	private static final String ACCOUNT_SID = "40b4502042e51797da0707de0ced9117";
	private static final String AUTH_TOKEN = "1d14025ff5ac002abae67317536f4b8c";
	private static final String APP_ID = "476fde74e34a42318f382861d467fe2d";
	public static final boolean isTest=false;
	public static final String server="api.ucpaas.com";
	public static final String sslIP="0";
	public static final int sslPort=0;
	public static final String version="2014-06-30";

	// 验证模版id
	public static final String VERIFY_TMP_ID = "9581";

	static AbsRestClient InstantiationRestAPI(boolean enable) {
		if (enable) {
			return new JsonReqClient();
		} else {
			return new XmlReqClient();
		}
	}

	public static boolean templateSMS(boolean json, String templateId, String to, String param) {
		try {
			String result = InstantiationRestAPI(json).templateSMS(ACCOUNT_SID, AUTH_TOKEN, APP_ID, templateId, to, param);
			LOG.info("Response content is: " + result);
			JSONObject o = JSON.parseObject(result);
			String respCode = o.getJSONObject("resp").getString("respCode");
            return "000000".contentEquals(respCode) ? true :false;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
	}
}
