package com.easemob.server.httpclient.api;

import java.net.URL;

import com.easemob.server.httpclient.vo.ClientSecretCredential;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easemob.server.common.Constants;
import com.easemob.server.common.HTTPMethod;
import com.easemob.server.common.Roles;
import com.easemob.server.httpclient.utils.HTTPClientUtils;
import com.easemob.server.httpclient.vo.Credential;
import com.easemob.server.httpclient.vo.EndPoints;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST API Demo : 群组管理 HttpClient4.3实现
 * 
 * Doc URL: http://www.easemob.com/docs/rest/groups/
 * 
 * @author Lynch 2014-09-15
 *
 */
public class EasemobChatGroups {

	private static final Logger LOGGER = LoggerFactory.getLogger(EasemobChatGroups.class);

    // 通过app的client_id和client_secret来获取app管理员token
    private static Credential credential = new ClientSecretCredential(Constants.APP_CLIENT_ID,
            Constants.APP_CLIENT_SECRET, Roles.USER_ROLE_APPADMIN);
	private static JsonNodeFactory factory = new JsonNodeFactory(false);
	private static final String APPKEY = Constants.APPKEY;

    /**
     * 修改群组信息
     * @param dataObjectNode
     * @param chatGroupId
     * @return
     */
	public static ObjectNode modifyChatGroups(ObjectNode dataObjectNode, String chatGroupId) {
        ObjectNode objectNode = factory.objectNode();
        // check appKey format
        if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
            LOGGER.error("Bad format of Appkey: " + APPKEY);
            objectNode.put("message", "Bad format of Appkey");
            return objectNode;
        }

        try {
            URL url = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/")
                    + "/chatgroups/" + chatGroupId);
            objectNode = HTTPClientUtils.sendHTTPRequest(url, credential, dataObjectNode,
                    HTTPMethod.METHOD_PUT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objectNode;
	}

	/**
	 * 获取APP中所有的群组ID
	 * 
	 * @return
	 */
	public static ObjectNode getAllChatgroupids() {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			objectNode = HTTPClientUtils.sendHTTPRequest(EndPoints.CHATGROUPS_URL, credential, null,
					HTTPMethod.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 获取一个或者多个群组的详情
	 * 
	 * @return
	 */
	public static ObjectNode getGroupDetailsByChatgroupid(String[] chatgroupIDs) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			URL groupDetailsByChatgroupidUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/")
					+ "/chatgroups/" + chatgroupIDs.toString());
			objectNode = HTTPClientUtils.sendHTTPRequest(groupDetailsByChatgroupidUrl, credential, null,
					HTTPMethod.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 创建群组
	 * 
	 */
	public static ObjectNode creatChatGroups(ObjectNode dataObjectNode) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		// check properties that must be provided
		if (!dataObjectNode.has("groupname")) {
			LOGGER.error("Property that named groupname must be provided .");
			objectNode.put("message", "Property that named groupname must be provided .");
			return objectNode;
		}
		if (!dataObjectNode.has("desc")) {
			LOGGER.error("Property that named desc must be provided .");
			objectNode.put("message", "Property that named desc must be provided .");
			return objectNode;
		}
		if (!dataObjectNode.has("public")) {
			LOGGER.error("Property that named public must be provided .");
			objectNode.put("message", "Property that named public must be provided .");
			return objectNode;
		}
		if (!dataObjectNode.has("approval")) {
			LOGGER.error("Property that named approval must be provided .");
			objectNode.put("message", "Property that named approval must be provided .");
			return objectNode;
		}
		if (!dataObjectNode.has("owner")) {
			LOGGER.error("Property that named owner must be provided .");
			objectNode.put("message", "Property that named owner must be provided .");
			return objectNode;
		}
		if (!dataObjectNode.has("members") || !dataObjectNode.path("members").isArray()) {
			LOGGER.error("Property that named members must be provided .");
			objectNode.put("message", "Property that named members must be provided .");
			return objectNode;
		}

		try {
			objectNode = HTTPClientUtils.sendHTTPRequest(EndPoints.CHATGROUPS_URL, credential, dataObjectNode,
					HTTPMethod.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 删除群组
	 * 
	 */
	public static ObjectNode deleteChatGroups(String chatgroupid) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			URL deleteChatGroupsUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatgroups/"
					+ chatgroupid);
			objectNode = HTTPClientUtils.sendHTTPRequest(deleteChatGroupsUrl, credential, null,
					HTTPMethod.METHOD_DELETE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 获取群组中的所有成员
	 * 
	 */
	public static ObjectNode getAllMemberssByGroupId(String chatgroupid) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			URL allMemberssByGroupIdUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatgroups/"
					+ chatgroupid + "/users");
			objectNode = HTTPClientUtils.sendHTTPRequest(allMemberssByGroupIdUrl, credential, null,
					HTTPMethod.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 在群组中添加一个人
	 * 
	 */
	public static ObjectNode addUserToGroup(String chatgroupid, String userName) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			URL allMemberssByGroupIdUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatgroups/"
					+ chatgroupid + "/users/" + userName);
			ObjectNode dataobjectNode = factory.objectNode();
			objectNode = HTTPClientUtils.sendHTTPRequest(allMemberssByGroupIdUrl, credential, dataobjectNode,
					HTTPMethod.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 在群组中减少一个人
	 * 
	 */
	public static ObjectNode deleteUserFromGroup(String chatgroupid, String userName) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			URL allMemberssByGroupIdUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatgroups/"
					+ chatgroupid + "/users/" + userName);
			objectNode = HTTPClientUtils.sendHTTPRequest(allMemberssByGroupIdUrl, credential, null,
					HTTPMethod.METHOD_DELETE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}
	
	/**
	 * 获取一个用户参与的所有群组
	 * 
	 * @param username
	 * @return
	 */
	public static ObjectNode getJoinedChatgroupsForIMUser(String username) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}
		if (StringUtils.isBlank(username.trim())) {
			LOGGER.error("Property that named username must be provided .");
			objectNode.put("message", "Property that named username must be provided .");
			return objectNode;
		}

		try {
			URL getJoinedChatgroupsForIMUserUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/")
					+ "/users/" + username + "/joined_chatgroups");
			objectNode = HTTPClientUtils.sendHTTPRequest(getJoinedChatgroupsForIMUserUrl, credential, null,
					HTTPMethod.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}
	
	/**
	 * 群组批量添加成员
	 * 
	 * @param toAddBacthChatgroupid
	 * @param usernames
	 * @return
	 */
	public static ObjectNode addUsersToGroupBatch(String toAddBacthChatgroupid, ObjectNode usernames) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}
		if (StringUtils.isBlank(toAddBacthChatgroupid.trim())) {
			LOGGER.error("Property that named toAddBacthChatgroupid must be provided .");
			objectNode.put("message", "Property that named toAddBacthChatgroupid must be provided .");
			return objectNode;
		}
		// check properties that must be provided
		if (null != usernames && !usernames.has("usernames")) {
			LOGGER.error("Property that named usernames must be provided .");
			objectNode.put("message", "Property that named usernames must be provided .");
			return objectNode;
		}

		try {
			URL getJoinedChatgroupsForIMUserUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/")
					+ "/chatgroups/" + toAddBacthChatgroupid + "/users");
			objectNode = HTTPClientUtils.sendHTTPRequest(getJoinedChatgroupsForIMUserUrl, credential, usernames,
					HTTPMethod.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

}
