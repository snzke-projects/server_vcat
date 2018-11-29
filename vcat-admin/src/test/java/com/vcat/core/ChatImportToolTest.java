package com.vcat.core;

import com.easemob.server.common.Constants;
import com.easemob.server.httpclient.api.EasemobChatMessage;
import com.easemob.server.httpclient.api.EasemobIMUsers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vcat.common.utils.DateUtils;
import com.vcat.module.ec.dao.ChatDao;
import com.vcat.module.ec.dao.CustomerDao;
import com.vcat.module.ec.dao.ShopDao;
import com.vcat.module.ec.entity.Customer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.*;

/**
 * Created by ylin on 2015/10/21.
 */
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class ChatImportToolTest extends AbstractJUnit4SpringContextTests {
    private static final JsonNodeFactory factory = new JsonNodeFactory(false);

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private ShopDao shopDao;
    @Autowired
    private ChatDao chatDao;

    /**
     * 导入指定时间聊天记录
     */
    @Test
    public void generateChatMessages(){
        String start = String.valueOf(DateUtils.parseDate("2015-12-01 00:00:00").getTime());
        String end = String.valueOf(DateUtils.parseDate("2016-01-22 00:00:00").getTime());
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

    /**
     * 查出电话，导入到环信用户
     */
    @Test
    public void importUser() throws InterruptedException {
        List<Customer> list =  customerDao.getSellerWithPhone();
        for (int i = 0; i < list.size(); i++) {
            ObjectNode userNode = factory.objectNode();
            userNode.put("username", list.get(i).getId());
            userNode.put("password", Constants.DEFAULT_PASSWORD);
            ObjectNode response = EasemobIMUsers.createNewIMUserSingle(userNode);
            System.out.println(response);
            Thread.sleep(35);
        }

    }

    /**
     * 根据电话号码导入好友关系
     * @return
     */
    @Test
    public void importFriends() throws InterruptedException {
        List<Customer> list =  customerDao.getSellerWithPhone();
        for(Customer customer : list){
            List<String> friends =  shopDao.getFriendsById(customer.getId());
            for (String f : friends){
                ObjectNode response = EasemobIMUsers.addFriendSingle(customer.getId(), f);
                System.out.println(response);
                Thread.sleep(35);
            }
        }
    }


}
