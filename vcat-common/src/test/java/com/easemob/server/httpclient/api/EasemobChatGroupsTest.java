package com.easemob.server.httpclient.api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

/**
 * Created by ylin on 2015/10/21.
 */
public class EasemobChatGroupsTest {
    private EasemobChatGroups easemobChatGroups = new EasemobChatGroups();

    @Test
    public void easemobChatGroups(){
        /** 获取APP中所有的群组ID
         * curl示例:
         * curl -X GET -i "https://a1.easemob.com/easemob-playground/test1/chatgroups" -H "Authorization: Bearer {token}"
         */
        ObjectNode chatgroupidsNode = easemobChatGroups.getAllChatgroupids();
        System.out.println(chatgroupidsNode.toString());

        /**
         * 获取一个或者多个群组的详情
         * curl示例
         * curl -X GET -i "https://a1.easemob.com/easemob-playground/test1/chatgroups/1414379474926191,1405735927133519"
         * -H "Authorization: Bearer {token}"
         */
        String[] chatgroupIDs = {"1414379474926191", "1405735927133519"};
        ObjectNode groupDetailNode = easemobChatGroups.getGroupDetailsByChatgroupid(chatgroupIDs);
        System.out.println(groupDetailNode.toString());

        /** 创建群组
         * curl示例
         * curl -X POST 'https://a1.easemob.com/easemob-playground/test1/chatgroups' -H 'Authorization: Bearer {token}'
         * -d '{"groupname":"测试群组","desc":"测试群组","public":true,"approval":true,"owner":"xiaojianguo001","maxusers":333,"members":["xiaojianguo002","xiaojianguo003"]}'
         */
        ObjectNode dataObjectNode = JsonNodeFactory.instance.objectNode();
        dataObjectNode.put("groupname", "测试群组");
        dataObjectNode.put("desc", "测试群组");
        dataObjectNode.put("approval", true);
        dataObjectNode.put("public", true);
        dataObjectNode.put("maxusers", 333);
        dataObjectNode.put("owner", "xiaojianguo001");
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        arrayNode.add("xiaojianguo002");
        arrayNode.add("xiaojianguo003");
        dataObjectNode.put("members", arrayNode);
        ObjectNode creatChatGroupNode = easemobChatGroups.creatChatGroups(dataObjectNode);
        System.out.println(creatChatGroupNode.toString());

        /**
         * 删除群组
         * curl示例
         * curl -X DELETE 'https://a1.easemob.com/easemob-playground/test1/chatgroups/1405735927133519'
         * -H 'Authorization: Bearer {token}'
         */
        String toDelChatgroupid = "1405735927133519";
        ObjectNode deleteChatGroupNode =  easemobChatGroups.deleteChatGroups(toDelChatgroupid) ;
        System.out.println(deleteChatGroupNode.toString());

        /**
         * 获取群组中的所有成员
         * curl示例
         * curl -X GET 'https://a1.easemob.com/easemob-playground/test1/chatgroups/1405735927133519/users'
         * -H 'Authorization: Bearer {token}'
         */
        String chatgroupid = "1405735927133519";
        ObjectNode getAllMemberssByGroupIdNode = easemobChatGroups.getAllMemberssByGroupId(chatgroupid);
        System.out.println(getAllMemberssByGroupIdNode.toString());

        /**
         * 在群组中添加一个人
         * curl示例
         * curl -X POST 'https://a1.easemob.com/easemob-playground/test1/chatgroups/1405735927133519/users/xiaojianguo002'
         * -H 'Authorization: Bearer {token}'
         */
        String addToChatgroupid = "1405735927133519";
        String toAddUsername = "xiaojianguo002";
        ObjectNode addUserToGroupNode = easemobChatGroups.addUserToGroup(addToChatgroupid, toAddUsername);
        System.out.println(addUserToGroupNode.toString());

        /**
         * 在群组中减少一个人
         * curl示例
         * curl -X DELETE 'https://a1.easemob.com/easemob-playground/test1/chatgroups/1405735927133519/users/xiaojianguo002'
         * -H 'Authorization: Bearer {token}'
         */
        String delFromChatgroupid = "1405735927133519";
        String toRemoveUsername = "xiaojianguo002";
        ObjectNode deleteUserFromGroupNode = easemobChatGroups.deleteUserFromGroup(delFromChatgroupid, toRemoveUsername);
        System.out.println(deleteUserFromGroupNode.asText());

        /**
         * 获取一个用户参与的所有群组
         * curl示例
         * curl -X GET 'https://a1.easemob.com/easemob-playground/test1/users/xiaojianguo002/joined_chatgroups'
         * -H 'Authorization: Bearer {token}'
         */
        String username = "xiaojianguo002";
        ObjectNode getJoinedChatgroupsForIMUserNode = easemobChatGroups.getJoinedChatgroupsForIMUser(username);
        System.out.println(getJoinedChatgroupsForIMUserNode.toString());

        /**
         * 群组批量添加成员
         * curl示例
         * curl -X POST -i 'https://a1.easemob.com/easemob-playground/test1/chatgroups/1405735927133519/users' -H 'Authorization: Bearer {token}' -d '{"usernames":["xiaojianguo002","xiaojianguo003"]}'
         */
        String toAddBacthChatgroupid = "1405735927133519";
        ArrayNode usernames = JsonNodeFactory.instance.arrayNode();
        usernames.add("xiaojianguo002");
        usernames.add("xiaojianguo003");
        ObjectNode usernamesNode = JsonNodeFactory.instance.objectNode();
        usernamesNode.put("usernames", usernames);
        ObjectNode addUserToGroupBatchNode = easemobChatGroups.addUsersToGroupBatch(toAddBacthChatgroupid, usernamesNode);
        System.out.println(addUserToGroupBatchNode.toString());
    }
}
