package com.easemob.server.httpclient.api;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

/**
 * Created by ylin on 2015/10/21.
 */
public class EasemobChatMessageTest {
    private EasemobChatMessage easemobChatMessage = new EasemobChatMessage();
    private static final JsonNodeFactory factory = new JsonNodeFactory(false);

    @Test
    public void chatMessageTest(){
        // 聊天消息 获取最新的20条记录
        /*
        ObjectNode queryStrNode = factory.objectNode();
        queryStrNode.put("ql", "select+*+where+from='mm1'+and+to='mm2'");
        queryStrNode.put("limit", "20");
        ObjectNode messages = getChatMessages(queryStrNode);*/

        // 聊天消息 获取7天以内的消息
        String currentTimestamp = String.valueOf(System.currentTimeMillis());
        String senvenDayAgo = String.valueOf(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);
        ObjectNode queryStrNode1 = factory.objectNode();
        queryStrNode1.put("ql", "select * where timestamp>" + senvenDayAgo + " and timestamp<" + currentTimestamp);
        ObjectNode messages1 = easemobChatMessage.getChatMessages(queryStrNode1);

        /*// 聊天消息 分页获取
        ObjectNode queryStrNode2 = factory.objectNode();
        queryStrNode2.put("limit", "20");
        // 第一页
        ObjectNode messages2 = getChatMessages(queryStrNode2);
        // 第二页
        String cursor = messages2.get("cursor").asText();
        queryStrNode2.put("cursor", cursor);
        ObjectNode messages3 = getChatMessages(queryStrNode2);*/
    }
}
