package com.easemob.server.httpclient.api;

import java.net.URL;

import com.easemob.server.common.Constants;
import com.easemob.server.common.Roles;
import com.easemob.server.httpclient.vo.ClientSecretCredential;
import com.easemob.server.httpclient.vo.Credential;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easemob.server.common.HTTPMethod;
import com.easemob.server.httpclient.utils.HTTPClientUtils;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST API Demo : 聊天消息导出REST API HttpClient4.3实现
 * 
 * Doc URL: http://www.easemob.com/docs/rest/chatmessage/
 * 
 * @author Lynch 2014-09-15
 * 
 */
public class EasemobChatMessage {

	private static final Logger LOGGER = LoggerFactory.getLogger(EasemobChatMessage.class);
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	private static final String APPKEY = Constants.APPKEY;

    // 通过app的client_id和client_secret来获取app管理员token
    private static Credential credential = new ClientSecretCredential(Constants.APP_CLIENT_ID,
            Constants.APP_CLIENT_SECRET, Roles.USER_ROLE_APPADMIN);

    /**
	 * 获取聊天消息
	 * 
	 * @param queryStrNode
	 *
	 */
	public static ObjectNode getChatMessages(ObjectNode queryStrNode) {

		ObjectNode objectNode = factory.objectNode();

		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);

			objectNode.put("message", "Bad format of Appkey");

			return objectNode;
		}

		try {

			String rest = "";
			if (null != queryStrNode && queryStrNode.get("ql") != null && !StringUtils.isEmpty(queryStrNode.get("ql").asText())) {
				rest = "ql="+ java.net.URLEncoder.encode(queryStrNode.get("ql").asText(), "utf-8");
			}
			if (null != queryStrNode && queryStrNode.get("limit") != null && !StringUtils.isEmpty(queryStrNode.get("limit").asText())) {
				rest = rest + "&limit=" + queryStrNode.get("limit").asText();
			}
			if (null != queryStrNode && queryStrNode.get("cursor") != null && !StringUtils.isEmpty(queryStrNode.get("cursor").asText())) {
				rest = rest + "&cursor=" + queryStrNode.get("cursor").asText();
			}
		
			URL chatMessagesUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatmessages?" + rest);
			
			objectNode = HTTPClientUtils.sendHTTPRequest(chatMessagesUrl, credential, null, HTTPMethod.METHOD_GET);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

}
