package com.easemob.server.httpclient.api;

import com.easemob.server.common.Constants;
import com.easemob.server.httpclient.utils.HTTPClientUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by ylin on 2015/10/21.
 */
public class EasemobMessagesTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasemobMessagesTest.class);
    private static final JsonNodeFactory factory = new JsonNodeFactory(false);
    private EasemobMessages easemobMessages = new EasemobMessages();
    @Test
    public void easemobMessagese() {
        //  检测用户是否在线
        String targetUserName = "kenshinn";
        ObjectNode usernode = easemobMessages.getUserStatus(targetUserName);
        if (null != usernode) {
            LOGGER.info("检测用户是否在线: " + usernode.toString());
        }

        // 给用户发一条文本消息
        String from = "kenshinnuser000";
        String targetTypeus = "users";
        ObjectNode ext = factory.objectNode();
        ArrayNode targetusers = factory.arrayNode();
        targetusers.add("kenshinnuser001");
        targetusers.add("kenshinnuser002");
        ObjectNode txtmsg = factory.objectNode();
        txtmsg.put("msg", "Hello Easemob!");
        txtmsg.put("type","txt");
        ObjectNode sendTxtMessageusernode = easemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
        if (null != sendTxtMessageusernode) {
            LOGGER.info("给用户发一条文本消息: " + sendTxtMessageusernode.toString());
        }
        // 给一个群组发文本消息
        String targetTypegr = "chatgroups";
        ArrayNode  chatgroupidsNode = (ArrayNode) EasemobChatGroups.getAllChatgroupids().path("data");
        ArrayNode targetgroup = factory.arrayNode();
        targetgroup.add(chatgroupidsNode.get(0).path("groupid").asText());
        ObjectNode sendTxtMessagegroupnode = easemobMessages.sendMessages(targetTypegr, targetgroup, txtmsg, from, ext);
        if (null != sendTxtMessagegroupnode) {
            LOGGER.info("给一个群组发文本消息: " + sendTxtMessagegroupnode.toString());
        }

        // 给用户发一条图片消息
        File uploadImgFile = new File("/home/lynch/Pictures/24849.jpg");
        ObjectNode imgDataNode = EasemobFiles.mediaUpload(uploadImgFile);
        if (null != imgDataNode) {
            String imgFileUUID = imgDataNode.path("entities").get(0).path("uuid").asText();
            String shareSecret = imgDataNode.path("entities").get(0).path("share-secret").asText();

            LOGGER.info("上传图片文件: " + imgDataNode.toString());

            ObjectNode imgmsg = factory.objectNode();
            imgmsg.put("type","img");
            imgmsg.put("url", HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatfiles/" + imgFileUUID).toString());
            imgmsg.put("filename", "24849.jpg");
            imgmsg.put("length", 10);
            imgmsg.put("secret", shareSecret);
            ObjectNode sendimgMessageusernode = easemobMessages.sendMessages(targetTypeus, targetusers, imgmsg, from, ext);
            if (null != sendimgMessageusernode) {
                LOGGER.info("给一个群组发文本消息: " + sendimgMessageusernode.toString());
            }
            // 给一个群组发图片消息
            ObjectNode sendimgMessagegroupnode = easemobMessages.sendMessages(targetTypegr, targetgroup, imgmsg, from, ext);
            if (null != sendimgMessagegroupnode) {
                LOGGER.info("给一个群组发文本消息: " + sendimgMessagegroupnode.toString());
            }

        }
        // 给用户发一条语音消息
        File uploadAudioFile = new File("/home/lynch/Music/music.MP3");
        ObjectNode audioDataNode = EasemobFiles.mediaUpload(uploadAudioFile);
        if (null != audioDataNode) {
            String audioFileUUID = audioDataNode.path("entities").get(0).path("uuid").asText();
            String audioFileShareSecret = audioDataNode.path("entities").get(0).path("share-secret").asText();

            LOGGER.info("上传语音文件: " + audioDataNode.toString());

            ObjectNode audiomsg = factory.objectNode();
            audiomsg.put("type","audio");
            audiomsg.put("url", HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatfiles/" + audioFileUUID).toString());
            audiomsg.put("filename", "music.MP3");
            audiomsg.put("length", 10);
            audiomsg.put("secret", audioFileShareSecret);
            ObjectNode sendaudioMessageusernode = easemobMessages.sendMessages(targetTypeus, targetusers, audiomsg, from, ext);
            if (null != sendaudioMessageusernode) {
                LOGGER.info("给用户发一条语音消息: " + sendaudioMessageusernode.toString());
            }

            // 给一个群组发语音消息
            ObjectNode sendaudioMessagegroupnode = easemobMessages.sendMessages(targetTypegr, targetgroup, audiomsg, from, ext);
            if (null != sendaudioMessagegroupnode) {
                LOGGER.info("给一个群组发语音消息: " + sendaudioMessagegroupnode.toString());
            }
        }
        // 给用户发一条透传消息
        ObjectNode cmdmsg = factory.objectNode();
        cmdmsg.put("action", "gogogo");
        cmdmsg.put("type","cmd");
        ObjectNode sendcmdMessageusernode = easemobMessages.sendMessages(targetTypeus, targetusers, cmdmsg, from, ext);
        if (null != sendcmdMessageusernode) {
            LOGGER.info("给用户发一条透传消息: " + sendcmdMessageusernode.toString());
        }
    }
}
