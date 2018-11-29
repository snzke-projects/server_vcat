package com.vcat.api.web;

import com.easemob.server.common.Constants;
import com.easemob.server.httpclient.api.EasemobChatGroups;
import com.easemob.server.httpclient.api.EasemobIMUsers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vcat.api.service.ChatService;
import com.vcat.api.service.CustomerService;
import com.vcat.api.service.RedisService;
import com.vcat.api.service.ShopService;
import com.vcat.common.cloud.QCloudClient;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.mapper.JsonMapper;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.ResponseUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.utils.ThreadPoolUtil;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.config.CacheConfig;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.Level;
import com.vcat.module.ec.entity.Shop;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 聊天接口
 * Created by ylin on 2015/10/22.
 */

@RestController
@ApiVersion({1,2})
public class ChatController extends RestBaseController {
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ShopService shopService;

    private ObjectMapper mapper = JsonMapper.getInstance();

    private boolean isNotOwner(String token, String gid){
        String customerId = StringUtils.getCustomerIdByToken(token);
        Boolean r = chatService.isOwner(gid, customerId);
        return r == null ? true : !r.booleanValue();
    }

    private boolean hasTeam(String token) {
        String shopId = StringUtils.getCustomerIdByToken(token);
        Shop shop = shopService.get(shopId);
        return shop.getParentId() != null;
    }

    /**
     * 申请入群透传
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/applyForGroup")
    public Object applyForGroup(@RequestHeader(value = "token", defaultValue = "") String token,
                              @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String gid = params.get("groupid").asText();
        String shopId = StringUtils.getCustomerIdByToken(token);
        String pid = chatService.getGroupOwner(gid);
        if(StringUtils.isEmpty(pid) ){
            logger.error("group[" + gid + "]没有群主...");
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if(!chatService.isJoinedGroup(gid, shopId)){
            ChatMessageHelper.sendApplyForGroupMessage(shopId, gid, pid);
        }
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
                ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 申请入群结果
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/applyForGroupResult")
    public Object applyForGroupResult(@RequestHeader(value = "token", defaultValue = "") String token,
                                @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null
                || params.get("huanid") == null || params.get("result") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String gid = params.get("groupid").asText();
        String huanid = params.get("huanid").asText();
        boolean result = params.get("result").asBoolean();
        if(!chatService.isTeamMember(huanid,StringUtils.getCustomerIdByToken(token))){
            return new MsgEntity("他已退出您的社群，不能添加",
                    ApiMsgConstants.FAILED_CODE);
        }
        if(result){
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            arrayNode.add(huanid);
            addToGroup(arrayNode, gid);
        }
        ChatMessageHelper.sendApplyForGroupResultMessage(huanid, gid, result);
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
                ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 更改别名
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/updateAlias")
    public Object updateAlias(@RequestHeader(value = "token", defaultValue = "") String token,
                           @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null || params.get("alias") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String gid = params.get("groupid").asText();
        String alias = params.get("alias").asText();
        String cid = StringUtils.getCustomerIdByToken(token);
        if(alias.length() > 20){
            return  new MsgEntity("字数超过限制，请重新输入",
                    ApiMsgConstants.FAILED_CODE);
        }
        chatService.updateAlias(alias, cid, gid);
        ChatMessageHelper.sendUpdatedAliasMessage(cid, gid, alias);
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
                ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 获得当前社群包含的群组
     * @param token
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/getGroups")
    public Object getGroups(@RequestHeader(value = "token", defaultValue = "") String token) {
        String shopId = StringUtils.getCustomerIdByToken(token);
        Shop shop = shopService.get(shopId);
        if(shop == null || shop.getParentId() == null){
            return new MsgEntity(ApiMsgConstants.PARENT_NONE_TEAM,
                    ApiMsgConstants.FAILED_CODE);
        }
        return chatService.getGroupsByOwner(shop.getParentId(), shopId);
    }

    /**
     * 推荐社群
     * @param token
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/getRecommended")
    public Object getRecommended(@RequestHeader(value = "token", defaultValue = "") String token) {
        return chatService.getReferrers();
    }

    //获取达人详情
    @RequiresRoles("buyer")
    @RequestMapping("/api/getRecommendDetail")
    public Object getRecommendDetail(@RequestHeader(value = "token", defaultValue = "") String token,
                                     @RequestParam(value="recommendId",defaultValue = "")String recommendId) {
        if (recommendId == null || "".equals(recommendId)) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        return chatService.getDetail(recommendId);
    }

    /**
     * 退出社群
     * @param token
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/quitTeam")
    public Object quitTeam(@RequestHeader(value = "token", defaultValue = "") String token) {
        if(!hasTeam(token)){
            return new MsgEntity(ApiMsgConstants.PARENT_NONE_TEAM,
                    ApiMsgConstants.FAILED_CODE);
        }
        String shopId = StringUtils.getCustomerIdByToken(token);
        Shop shop = shopService.get(shopId);
        //解除朋友关系
        ObjectNode response = ResponseUtils.convertResopnse(EasemobIMUsers.deleteFriendSingle(shopId, shop.getParentId()));
        if(ResponseUtils.isStatusSuccess(response)){
            chatService.setParentToNull(shopId);
        }else{
            logger.debug("环信操作失败");
            return new MsgEntity(ApiMsgConstants.FAILED_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        rootNode.put("data",arrayNode);
        rootNode.put("code", ApiMsgConstants.SUCCESS_CODE);
        //退出上家群 getParentGroups
        List<String> gids = chatService.getParentGroups(shopId);
        for(String gid : gids){
            removeUserFromGroup(gid, shopId);
            arrayNode.add(gid);
        }
        ChatMessageHelper.sendQuittedTeamMessage(shop.getParentId(), shopId);
        return rootNode;
    }

    /**
     * 加入社群
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/joinTeam")
    public Object joinTeam(@RequestHeader(value = "token", defaultValue = "") String token,
                                @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("shopNum") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String shopId = StringUtils.getCustomerIdByToken(token);
        Shop shop = shopService.get(shopId);
        if(shop.getParentId()!=null){
            return new MsgEntity(ApiMsgConstants.PARENT_SHOP_BIND,
                    ApiMsgConstants.FAILED_CODE);
        }
        String shopNum = params.get("shopNum").asText();
        if(StringUtils.isBlank(shopNum)){
            return new MsgEntity(ApiMsgConstants.PARENT_SHOP_NOT_FIND,
                    ApiMsgConstants.FAILED_CODE);
        }
        shopNum = shopNum.replaceAll("V", "");
        Shop parentShop = new Shop();
        Level level = new Level();
        parentShop.setLevel(level);
        parentShop.setShopNum(shopNum);
        //判断shopNum是否有对应的店铺
        Shop pShop = shopService.get(parentShop);
        if(pShop==null||shopId.equals(pShop.getId())||shopId.equals(pShop.getParentId())){
            return new MsgEntity(ApiMsgConstants.PARENT_SHOP_NOT_FIND,
                    ApiMsgConstants.FAILED_CODE);
        }
        shop.setParentId(pShop.getId());
        createNewIMUser(shopId);
        createNewIMUser(pShop.getId());
        shopService.updateParant(shop);
        ChatMessageHelper.sendJoinedTeamMessage(pShop.getId(), chatService.getMemberInfo(shopId));
        return chatService.getShopInfo(pShop.getId());
    }

    private void createNewIMUser(String cid){
        ObjectNode datanode = JsonNodeFactory.instance.objectNode();
        datanode.put("username", cid);
        datanode.put("password", Constants.DEFAULT_PASSWORD);
        ObjectNode createNewIMUserSingleNode = EasemobIMUsers.createNewIMUserSingle(datanode);
        if (null != createNewIMUserSingleNode) {
            logger.debug("注册IM用户[单个]: " + createNewIMUserSingleNode.toString());
        }
    }


    /**
     * 得到单个用户信息
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/getMemberInfo")
    public Object getMemberInfo(@RequestHeader(value = "token", defaultValue = "") String token,
                                @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("huanid") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String pid = params.get("huanid").asText();
        return chatService.getMemberInfo(pid);
    }

    /**
     * 根据用户id获得所有下家（好友）
     * @param token
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/getMemberList")
    public Object getMemberList(@RequestHeader(value = "token", defaultValue = "") String token) {
        String huanid = StringUtils.getCustomerIdByToken(token);
        return chatService.getMemberList(huanid);
    }

    /**
     * 解除用户禁言
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/unbanUser")
    public Object unbanUser(@RequestHeader(value = "token", defaultValue = "") String token,
                            @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null ||
                params.get("huanids") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String gid = params.get("groupid").asText();
        JsonNode cids = params.get("huanids");
        if(isNotOwner(token, gid)){
            return new MsgEntity(ApiMsgConstants.NOT_OWNER_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        Iterator<JsonNode> iterator = cids.iterator();
        ArrayNode aliases = JsonNodeFactory.instance.arrayNode();
        while(iterator.hasNext()){
            JsonNode node = iterator.next();
            String huanid = node.asText();
            int r = chatService.unbanUser(gid, huanid);
            logger.debug("unban user ["+huanid+"] result:"+r);
            aliases.add(chatService.getAlias(huanid, gid));
        }
        ChatMessageHelper.sendUnbanMessage(gid, cids, aliases);
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
                    ApiMsgConstants.SUCCESS_CODE);
}

    /**
     * 禁言用户
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/banUser")
    public Object banUser(@RequestHeader(value = "token", defaultValue = "") String token,
                                @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null ||
                params.get("huanids") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String gid = params.get("groupid").asText();
        JsonNode cids = params.get("huanids");
        if(isNotOwner(token, gid)){
            return new MsgEntity(ApiMsgConstants.NOT_OWNER_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        Iterator<JsonNode> iterator = cids.iterator();
        ArrayNode aliases = JsonNodeFactory.instance.arrayNode();
        while(iterator.hasNext()){
            JsonNode node = iterator.next();
            String huanid = node.asText();
            int r = chatService.banUser(gid, huanid);
            logger.debug("ban user ["+huanid+"] result:"+r);
            aliases.add(chatService.getAlias(huanid, gid));
        }
        ChatMessageHelper.sendBanMessage(gid, cids, aliases);
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
                ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 获得禁言列表
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/getBannedList")
    public Object getBannedList(@RequestHeader(value = "token", defaultValue = "") String token,
                                      @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String gid = params.get("groupid").asText();
        return chatService.getBannedList(gid);
    }

    /**
     * 主动退出群聊
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/quitChatGruop")
    public Object quitChatGruop(@RequestHeader(value = "token", defaultValue = "") String token,
                                      @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String groupid = params.get("groupid").asText();
        String huanid = StringUtils.getCustomerIdByToken(token);
        ObjectNode response = ResponseUtils.convertResopnse(EasemobChatGroups.deleteUserFromGroup(groupid, huanid));
        if(ResponseUtils.isStatusSuccess(response) && ResponseUtils.isBusinessSuccess(response)){
            chatService.removeUserFromGroup(groupid, huanid);
            ChatMessageHelper.sendRemovedUserFromGroupMessage(groupid, huanid);
        }else{
            logger.debug("环信操作失败");
        }
        return response;
    }

    /**
     * 将用户移除群组
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/removeUserFromGruop")
    public Object removeUserFromGruop(@RequestHeader(value = "token", defaultValue = "") String token,
                                 @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null ||
                params.get("huanid") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String groupid = params.get("groupid").asText();
        if(isNotOwner(token, groupid)) {
            return new MsgEntity(ApiMsgConstants.NOT_OWNER_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String huanid = params.get("huanid").asText();
        return removeUserFromGroup(groupid, huanid);
    }

    private ObjectNode removeUserFromGroup(String groupid, String huanid){
        ObjectNode response = ResponseUtils.convertResopnse(EasemobChatGroups.deleteUserFromGroup(groupid, huanid));
        if(ResponseUtils.isStatusSuccess(response) && ResponseUtils.isBusinessSuccess(response)){
            chatService.removeUserFromGroup(groupid, huanid);
            ChatMessageHelper.sendRemovedUserFromGroupMessage(groupid, huanid);
        }else{
            logger.debug("环信操作失败:groupid-"+groupid+" huanid-"+huanid);
        }
        return response;
    }

    private ObjectNode addToGroup(JsonNode huanids, String groupid){
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        if(chatService.isExceeded(groupid)){
            node.put("code",ApiMsgConstants.FAILED_CODE);
            node.put("msg",ApiMsgConstants.GROUP_MEMBER_EXCEEDED);
            return node;
        }
        Map<String, Object> info = chatService.getGroupInfo(groupid);
        int maxSize = (Integer)info.get("max_users");
        int size = huanids.size();
        int currentSize = chatService.getCurrentSize(groupid);
        logger.debug("need adding size:" + size);
        if(size + currentSize > maxSize){
            node.put("code",ApiMsgConstants.FAILED_CODE);
            node.put("msg","您还能添加"+(maxSize - currentSize) + "个成员到当前群聊");
            return node;
        }
        node.put("usernames", huanids);
        ObjectNode response = ResponseUtils.convertResopnse(EasemobChatGroups.addUsersToGroupBatch(groupid, node));
        if(ResponseUtils.isStatusSuccess(response) && ResponseUtils.isBusinessSuccess(response)){
            Iterator<JsonNode> iterator = huanids.iterator();
            while(iterator.hasNext()){
                JsonNode n = iterator.next();
                chatService.addUserToGroup(groupid, n.asText());
            }
            ChatMessageHelper.sendAddedUserToGroupMessage(groupid,chatService.getGroupDetailsForMsg(groupid));
        }else{
            logger.debug("环信操作失败");
        }
        return response;
    }

    /**
     * 添加用户到群组
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/addUserToGroup")
    public Object addUserToGroup(@RequestHeader(value = "token", defaultValue = "") String token,
                                  @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null ||
                params.get("huanids") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String groupid = params.get("groupid").asText();
        if(isNotOwner(token, groupid)){
            return new MsgEntity(ApiMsgConstants.NOT_OWNER_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        JsonNode huanids = params.withArray("huanids");
        return addToGroup(huanids, groupid);
    }

    /**
     * 得到群组详情
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/getGroupDetails")
    public Object getGroupDetails(@RequestHeader(value = "token", defaultValue = "") String token,
                                @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String groupid = params.get("groupid").asText();
        boolean isOwner = !isNotOwner(token, groupid);
        return chatService.getGroupDetails(groupid, isOwner);
    }

    /**
     * 获得用户参加的群聊
     * @param token
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/getJoinGroups")
    public Object getJoinGroups(@RequestHeader(value = "token", defaultValue = "") String token) {
        String huanid = StringUtils.getCustomerIdByToken(token);
        return chatService.getJoinGroups(huanid);
    }

    /**
     * 删除群组
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/deleteChatGroups")
    public Object deleteChatGroups(@RequestHeader(value = "token", defaultValue = "") String token,
                                   @RequestParam(value = "params", defaultValue = "") String param){
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String gid = params.get("groupid").asText();
        if(isNotOwner(token, gid)){
            return new MsgEntity(ApiMsgConstants.NOT_OWNER_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        ObjectNode response = ResponseUtils.convertResopnse(
                EasemobChatGroups.deleteChatGroups(gid));
        if(ResponseUtils.isStatusSuccess(response) && ResponseUtils.isBusinessSuccess(response)){
            chatService.deleteChatGroups(gid);
        }else{
            logger.debug("环信操作失败");
        }
        return response;
    }

    /**
     * 修改群组信息
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/modifyChatGroupsWithoutAvatar")
    public Object modifyChatGroupsWithoutAvatar(@RequestHeader(value = "token", defaultValue = "") String token,
                                  @RequestParam(value = "params", defaultValue = "") String param) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String gid = params.get("groupid").asText();
        if(isNotOwner(token, gid)){
            return new MsgEntity(ApiMsgConstants.NOT_OWNER_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        return modifyChatGroup(params, null);
    }

    private ObjectNode modifyChatGroup(JsonNode params, String imageId){
        ObjectNode dataObjectNode = JsonNodeFactory.instance.objectNode();
        String groupname = params.get("groupname").asText();
        String desc = params.get("desc").asText();
        int maxusers = params.get("maxusers").asInt();
        boolean ispublic = params.get("ispublic").asBoolean();
        String groupid = params.get("groupid").asText();
        dataObjectNode.put("groupname", groupname);
        dataObjectNode.put("description", desc);
        dataObjectNode.put("maxusers", maxusers);
        ObjectNode response = ResponseUtils.convertResopnse(EasemobChatGroups.modifyChatGroups(dataObjectNode, groupid));
        if(ResponseUtils.isStatusSuccess(response) && ResponseUtils.isBusinessSuccess(response)){
            dataObjectNode.put("ispublic", ispublic);
            chatService.modifyChatGroups(dataObjectNode, groupid, imageId);
            String groupavatar = null;
            if(imageId != null) {
                groupavatar = chatService.getAvatarUrl(imageId);
                ObjectNode data = (ObjectNode)response.get("data");
                data.put("groupavatar", groupavatar);
            }
            ChatMessageHelper.sendChangedGroupInfoMessage(groupid, groupname, desc, maxusers, ispublic, groupavatar);
        }else{
            logger.debug("环信操作失败");
        }
        return response;
    }

    /**
     * 修改群组信息(包含头像)
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/modifyChatGroups")
    public Object modifyChatGroupsWithAvatar(@RequestHeader(value = "token", defaultValue = "") String token,
                                   @RequestParam(value = "params", defaultValue = "") String param,
                                             MultipartFile groupAvatar) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String gid = params.get("groupid").asText();
        if(isNotOwner(token, gid)){
            return new MsgEntity(ApiMsgConstants.NOT_OWNER_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String imageId = "";
        if(groupAvatar!=null){
            imageId = uploadAvatar(groupAvatar);
        }else{
            logger.debug("file is not exsit");
        }
        return  modifyChatGroup(params,imageId);
    }

    /**
     * 上传群组图像
     * @param token
     * @param param
     * @param groupAvatar
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/uploadGroupAvatar")
    public Object uploadGroupAvatar(@RequestHeader(value = "token", defaultValue = "") String token,
                                  @RequestParam(value = "params", defaultValue = "") String param,
                                  MultipartFile groupAvatar) {
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params = null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错" + e.getMessage(), e);
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null || params.get("groupid") == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String imageId = "";
        if (groupAvatar != null) {
            imageId = uploadAvatar(groupAvatar);
        } else {
            logger.debug("file is not exsit");
            return new MsgEntity(ApiMsgConstants.IMAGE_NOT_EXSIT,
                    ApiMsgConstants.FAILED_CODE);
        }
        String gid = params.get("groupid").asText();
        if(isNotOwner(token, gid)){
            return new MsgEntity(ApiMsgConstants.NOT_OWNER_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if(chatService.modifyGroupAvatar(imageId, gid) > 0 ) {
            return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
                    ApiMsgConstants.SUCCESS_CODE);
        }else{
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
    }

    /**
     * 创建群组(不包含头像)
     * @param token
     * @param param
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/creatChatGroupsWithoutAvatar")
    public Object creatChatGroups(@RequestHeader(value = "token", defaultValue = "") String token,
                                  @RequestParam(value = "params", defaultValue = "") String param){
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params =null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错"+e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String imageId = "";
        return crateGroup(params, imageId);
    }

    private Object crateGroup(JsonNode params, String imageId){
        ObjectNode dataObjectNode = JsonNodeFactory.instance.objectNode();
        int maxusers = params.get("maxusers").asInt();
        int current = params.get("members").size();
        if(current > maxusers ){
            dataObjectNode.put("code",ApiMsgConstants.FAILED_CODE);
            dataObjectNode.put("msg","超过最大成员人数");
            return dataObjectNode;
        }
        dataObjectNode.put("groupname", params.get("groupname"));
        dataObjectNode.put("desc", params.get("desc"));
        dataObjectNode.put("approval", params.get("approval"));
        dataObjectNode.put("public", true);
        dataObjectNode.put("maxusers", params.get("maxusers"));
        dataObjectNode.put("owner", params.get("owner"));
        dataObjectNode.put("members", params.get("members"));
        if(current > 200){
            ThreadPoolUtil.execute(() -> {
                exeCreationChatGroups(dataObjectNode, params, imageId);
            });
            return new MsgEntity(ApiMsgConstants.SUCCESS_MSG, ApiMsgConstants.SUCCESS_CODE);
        }else{
            return exeCreationChatGroups(dataObjectNode, params, imageId);
        }
    }

    private ObjectNode exeCreationChatGroups(ObjectNode dataObjectNode, JsonNode params, String imageId){
        ObjectNode response = ResponseUtils.convertResopnse(EasemobChatGroups.creatChatGroups(dataObjectNode));
        if(ResponseUtils.isStatusSuccess(response)){
            dataObjectNode.put("public", params.get("ispublic"));
            chatService.createCharGroups(dataObjectNode, response, imageId);
            String gid = response.get("data").get("groupid").asText();
            ChatMessageHelper.sendAddedUserToGroupMessage(gid, chatService.getGroupDetailsForMsg(gid));
        }else{
            logger.debug("环信操作失败");
        }
        return response;
    }

    /**
     * 创建群组
     * @param token
     * @param param
     * @param groupAvatar
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/creatChatGroups")
    public Object creatChatGroups(@RequestHeader(value = "token", defaultValue = "") String token,
                                  @RequestParam(value = "params", defaultValue = "") String param,
                                  MultipartFile groupAvatar){
        // 简单验证,保证服务端不会出异常(没有使用faster-json，使用jackson)
        JsonNode params =null;
        try {
            params = mapper.readTree(param);
        } catch (Exception e) {
            logger.error("params 出错"+e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String imageId = "";
        if(groupAvatar!=null){
            imageId = uploadAvatar(groupAvatar);
        }else{
            logger.debug("file is not exsit");
        }
        return crateGroup(params, imageId);
    }


    /**
     * 上传文件
     * @param file
     * @return
     */
    private String uploadAvatar(MultipartFile file) {
        String path = servletContext.getRealPath("/");
        logger.debug("service path = " + path);
        String imagePath = path + File.separator + file.getName() + IdGen.getRandomNumber(10);
        File imageFile = new File(imagePath);
        logger.debug("imageFile path = "+imageFile.getPath());
        try {
            file.transferTo(imageFile);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        String imageId = QCloudClient.uploadImage(imageFile);
        return imageId;
    }
}
