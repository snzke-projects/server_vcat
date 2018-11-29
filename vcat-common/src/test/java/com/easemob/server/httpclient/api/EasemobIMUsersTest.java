package com.easemob.server.httpclient.api;

import com.easemob.server.common.Constants;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ylin on 2015/10/21.
 */
public class EasemobIMUsersTest {
    private EasemobIMUsers easemobIMUsers = new EasemobIMUsers();
    private static final Logger LOGGER = LoggerFactory.getLogger(EasemobIMUsersTest.class);

    @Test
    public void usersTest() {
        /**
         * 注册IM用户[单个]
         */
        //ObjectNode datanode = JsonNodeFactory.instance.objectNode();
        //datanode.put("username","kenshinnuser100");
        //datanode.put("password", Constants.DEFAULT_PASSWORD);
        //ObjectNode createNewIMUserSingleNode = easemobIMUsers.createNewIMUserSingle(datanode);
        //if (null != createNewIMUserSingleNode) {
        //    LOGGER.info("注册IM用户[单个]: " + createNewIMUserSingleNode.toString());
        //}

        /**
         * IM用户登录
         */
        //ObjectNode imUserLoginNode = easemobIMUsers.imUserLogin(datanode.get("username").asText(), datanode.get("password").asText());
        //if (null != imUserLoginNode) {
        //    LOGGER.info("IM用户登录: " + imUserLoginNode.toString());
        //}

        /**
         * 获取IM用户[主键查询]
         */
        String userName = "0036b1643d7146808b7a1a7a562a59f8";
        ObjectNode getIMUsersByUserNameNode = EasemobIMUsers.getIMUsersByUserName(userName);
        if (null != getIMUsersByUserNameNode) {
            LOGGER.info("获取IM用户[主键查询]: " + getIMUsersByUserNameNode.toString());
            System.out.println(getIMUsersByUserNameNode.size());
        }

        /**
         * 重置IM用户密码 提供管理员token
         */
        //String username = "kenshinnuser100";
        //ObjectNode json2 = JsonNodeFactory.instance.objectNode();
        //json2.put("newpassword", Constants.DEFAULT_PASSWORD);
        //ObjectNode modifyIMUserPasswordWithAdminTokenNode = easemobIMUsers.modifyIMUserPasswordWithAdminToken(username, json2);
        //if (null != modifyIMUserPasswordWithAdminTokenNode) {
        //    LOGGER.info("重置IM用户密码 提供管理员token: " + modifyIMUserPasswordWithAdminTokenNode.toString());
        //}
        //ObjectNode imUserLoginNode2 = easemobIMUsers.imUserLogin(username, json2.get("newpassword").asText());
        //if (null != imUserLoginNode2) {
        //    LOGGER.info("重置IM用户密码后,IM用户登录: " + imUserLoginNode2.toString());
        //}
        //
        ///**
        // * 添加好友[单个]
        // */
        //String ownerUserName = "13973061384";
        //String friendUserName = "15802810815";
        //ObjectNode addFriendSingleNode = easemobIMUsers.addFriendSingle(ownerUserName, friendUserName);
        //if (null != addFriendSingleNode) {
        //    LOGGER.info("添加好友[单个]: " + addFriendSingleNode.toString());
        //}
        //
        ///**
        // * 查看好友
        // */
        //ObjectNode getFriendsNode = easemobIMUsers.getFriends(ownerUserName);
        //if (null != getFriendsNode) {
        //    LOGGER.info("查看好友: " + getFriendsNode.toString());
        //}
        //
        ///**
        // * 解除好友关系
        // **/
        //ObjectNode deleteFriendSingleNode = easemobIMUsers.deleteFriendSingle(ownerUserName, friendUserName);
        //if (null != deleteFriendSingleNode) {
        //    LOGGER.info("解除好友关系: " + deleteFriendSingleNode.toString());
        //}
        //
        ///**
        // * 删除IM用户[单个]
        // */
        //ObjectNode deleteIMUserByuserNameNode = easemobIMUsers.deleteIMUserByuserName(userName);
        //if (null != deleteIMUserByuserNameNode) {
        //    LOGGER.info("删除IM用户[单个]: " + deleteIMUserByuserNameNode.toString());
        //}
        //
        ///**
        // * 删除IM用户[批量]
        // */
        //Long limit = 2l;
        //ObjectNode deleteIMUserByUsernameBatchNode = easemobIMUsers.deleteIMUserByUsernameBatch(limit);
        //if (null != deleteIMUserByUsernameBatchNode) {
        //    LOGGER.info("删除IM用户[批量]: " + deleteIMUserByUsernameBatchNode.toString());
        //}
    }
}
