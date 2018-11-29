package com.vcat.module.ec.service;

import com.easemob.server.httpclient.api.EasemobChatMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vcat.common.utils.DateUtils;
import com.vcat.module.ec.dao.ChatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ylin on 2015/12/16.
 */
@Service
@Transactional(readOnly = true)
public class ChatService {
    @Autowired
    private ChatDao chatDao;

    @Transactional(readOnly = false)
    public void generateChatMessages(String  start, String end){
//        String start = String.valueOf(DateUtils.parseDate("2015-12-14 00:00:00").getTime());
//        String end = String.valueOf(DateUtils.parseDate("2015-12-16 00:00:00").getTime());
        JsonNodeFactory factory = new JsonNodeFactory(false);
        ObjectNode queryStrNode = factory.objectNode();
        queryStrNode.put("ql", "select * where timestamp>" + start + " and timestamp<" + end);
        queryStrNode.put("limit", "50");
        ObjectNode messages = EasemobChatMessage.getChatMessages(queryStrNode);
        ArrayNode entities = messages.withArray("entities");
        JsonNode cursorNode = messages.get("cursor");
        if(cursorNode == null){
            return;
        }
        String cursor = cursorNode.asText();
        parseBody(entities);
        while (true){
            queryStrNode.put("cursor", cursor);
            messages = EasemobChatMessage.getChatMessages(queryStrNode);
            entities = messages.withArray("entities");
            parseBody(entities);
            cursorNode = messages.get("cursor");
            if(cursorNode == null){
                break;
            }
            cursor = cursorNode.asText();
        }
    }

    private void parseBody(JsonNode entities){
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<JsonNode> iterator = entities.iterator();
        while (iterator.hasNext()){
            JsonNode node = iterator.next();
            String from = node.get("from").asText();
            if("admin".equalsIgnoreCase(from)){
                continue;
            }
            String to = node.get("to").asText();
            Long timestamp = node.get("timestamp").asLong();
            String chatType = node.get("chat_type").asText();
            String msgId = node.get("msg_id").asText();
            JsonNode bodies = node.get("payload").withArray("bodies");
            JsonNode body = bodies.get(0);
            map.put("from",from);
            map.put("to",to);
            map.put("sendTime",new Date(timestamp));
            map.put("chatType",chatType);
            if("txt".equalsIgnoreCase(body.get("type").asText())){
                String msg = body.get("msg").asText();
                map.put("message",msg);
                map.put("id", msgId);
                if(!chatDao.isMessageLogged(msgId)) {
                    chatDao.insertChatLogs(map);
                }
            }
        }
    }
}
