package com.vcat.api.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.dao.ChatDao;
import com.vcat.module.ec.dao.CustomerDao;
import com.vcat.module.ec.dao.ShopDao;
import com.vcat.module.ec.entity.Customer;

/**
 * Created by ylin on 2015/10/22.
 */
@Service
@Transactional(readOnly = true)
public class ChatService {
    @Autowired
    private ChatDao chatDao;
    @Autowired
    private CustomerDao custDao;
    @Autowired
    private ShopDao shopDao;

    public boolean isTeamMember(String cid, String pid){
        return chatDao.isTeamMember(cid,pid) > 0;
    }

    public String getGroupOwner(String gid){
        return chatDao.getGroupOwner(gid);
    }

    public String getAlias(String cid, String gid){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("gid",gid);
        params.put("cid", cid);
        return chatDao.getAlias(params);
    }

    @Transactional(readOnly = false)
    public void updateAlias(String alias, String cid, String gid){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("alias",alias);
        params.put("gid",gid);
        params.put("cid", cid);
        chatDao.updateAlias(params);
    }

    public ObjectNode getGroupsByOwner(String pid,String cid){
        Map<String,Object> p = new HashMap<String, Object>();
        p.put("pid",pid);
        p.put("cid",cid);
        List<Map<String,Object>> list = chatDao.getGroupsByOwner(p);
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Map<String, Object> map : list){
            ObjectNode cnode = JsonNodeFactory.instance.objectNode();
            cnode.put("groupid", (String) map.get("id"));
            cnode.put("name", (String) map.get("name"));
            cnode.put("ispublic", (Boolean) map.get("public"));
            cnode.put("description", (String) map.get("description"));
            cnode.put("allowinvites", (Boolean) map.get("allow_invites"));
            cnode.put("maxusers", (Integer) map.get("max_users"));
            cnode.put("groupavatar", getAvatarUrl((String)map.get("group_avatar")));
            cnode.put("affiliations_count", (Long)map.get("affiliations_count"));
            Long r = (Long)map.get("isjoined");
            cnode.put("isjoined", r.longValue() == 0 ? false : true);
            arrayNode.add(cnode);
        }
        rootNode.put("data",arrayNode);
        rootNode.put("code", ApiMsgConstants.SUCCESS_CODE);
        return rootNode;
    }

    //添加推荐达人简介 去掉社区人数
    public ObjectNode getReferrers(){
        List<Map<String,Object>> list = chatDao.getReferrers();
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Map<String, Object> map : list){
            ObjectNode cnode = JsonNodeFactory.instance.objectNode();
            String huanid = (String) map.get("id");
            String templateUrl = ApiConstants.VCAT_DOMAIN + (String)map.get("template_url") + "?id=" + huanid;
            cnode.put("huanid", huanid);
            cnode.put("templateUrl",templateUrl);
            cnode.put("shopnum", String.valueOf(map.get("shop_num")));
            cnode.put("username", (String) map.get("user_name"));
            cnode.put("avatarurl", getAvatarUrl((String) map.get("avatar_url")));
            cnode.put("intro", (String)map.get("intro"));

            arrayNode.add(cnode);
        }
        rootNode.put("data",arrayNode);
        rootNode.put("code", ApiMsgConstants.SUCCESS_CODE);
        return rootNode;
    }

    //获取达人详情
    public Map<String,Object> getDetail(String recommendId){
        Map<String,Object> map = chatDao.getDetail(recommendId);
        if(map.isEmpty()){
            map.put("code",ApiMsgConstants.FAILED_CODE);
            map.put("msg","获取达人详细失败");
            return map;
        }
        String imgUrl =  QCloudUtils.createOriginalDownloadUrl((String) map.get("imgUrl"));
        map.put("imgUrl",imgUrl);
        map.put("code", ApiMsgConstants.SUCCESS_CODE);
        return map;
    }

    public ObjectNode getShopInfo(String cid){
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        Map<String,Object> map = chatDao.getShopInfo(cid);
        objectNode.put("shopnum",String.valueOf(map.get("shop_num")));
        objectNode.put("avatarurl",getAvatarUrl((String) map.get("avatar_url")));
        objectNode.put("username",String.valueOf((String) map.get("user_name")));
        objectNode.put("huanid",String.valueOf((String) map.get("id")));
        rootNode.put("data",objectNode);
        rootNode.put("code", ApiMsgConstants.SUCCESS_CODE);
        return  rootNode;
    }

    @Transactional(readOnly = false)
    public void setParentToNull(String cid){
        shopDao.setParentToNull(cid);
    }

    public List<String> getParentGroups(String cid){
        return chatDao.getParentGroups(cid);
    }

    public ObjectNode getMemberInfo(String cid){
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        Map<String,Object> map = chatDao.getMemberInfo(cid);
        ObjectNode cnode = JsonNodeFactory.instance.objectNode();
        cnode.put("huanid", (String) map.get("id"));
        cnode.put("parentid", (String) map.get("parent_id"));
        cnode.put("shopnum", String.valueOf(map.get("shop_num")));
        cnode.put("username", (String) map.get("user_name"));
        cnode.put("vip", map.get("advanced_shop").toString().equals("1") ? "白金小店":"特约小店");
        cnode.put("phoneNumber", (String) map.get("phone_number"));
        cnode.put("avatarurl", getAvatarUrl((String) map.get("avatar_url")));
        cnode.put("accumulativebonus", StringUtils.toNumberString((BigDecimal) map.get("accumulative_bonus")));
        cnode.put("monthlybonus", StringUtils.toNumberString((BigDecimal) map.get("monthly_bonus")));
        cnode.put("holdbonus", StringUtils.toNumberString((BigDecimal) map.get("hold_bonus")));
        rootNode.put("data",cnode);
        rootNode.put("code", ApiMsgConstants.SUCCESS_CODE);
        return rootNode;
    }

    public ObjectNode getMemberList(String pid){
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        List<Map<String,Object>> list = chatDao.getMemberList(pid);
        for (Map<String,Object> map : list){
            ObjectNode cnode = JsonNodeFactory.instance.objectNode();
            cnode.put("huanid", (String) map.get("id"));
            cnode.put("shopnum", String.valueOf(map.get("shop_num")));
            cnode.put("username", (String) map.get("user_name"));
            cnode.put("vip", map.get("advanced_shop").toString().equals("1") ? "白金小店":"特约小店");
            cnode.put("phoneNumber", (String) map.get("phone_number"));
            cnode.put("avatarurl", getAvatarUrl((String) map.get("avatar_url")));
            arrayNode.add(cnode);
        }
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        Map<String, Object> pp = chatDao.getParentInfo(pid);

        ObjectNode ppNode = JsonNodeFactory.instance.objectNode();
        if(pp!=null) {
            ppNode.put("huanid", (String) pp.get("id"));
            ppNode.put("shopnum", String.valueOf(pp.get("shop_num")));
            ppNode.put("username", (String) pp.get("user_name"));
            ppNode.put("vip", "1".equals(String.valueOf(pp.get("advanced_shop"))) ? "白金小店":"特约小店");
            ppNode.put("phoneNumber", (String) pp.get("phone_number"));
            ppNode.put("avatarurl", getAvatarUrl((String) pp.get("avatar_url")));
        }

        Map<String, Object> self = chatDao.getMemberInfo(pid);
        ObjectNode selfNode = JsonNodeFactory.instance.objectNode();
        if(self!=null) {
            selfNode.put("huanid", (String) self.get("id"));
            selfNode.put("shopnum", String.valueOf(self.get("shop_num")));
            selfNode.put("username", (String) self.get("user_name"));
            selfNode.put("vip", self.get("advanced_shop").toString().equals("1") ? "白金小店":"特约小店");
            selfNode.put("phoneNumber", (String) self.get("phone_number"));
            selfNode.put("avatarurl", getAvatarUrl((String) self.get("avatar_url")));
        }
        objectNode.put("parent", ppNode);
        objectNode.put("self", selfNode);
        objectNode.put("child",arrayNode);
        rootNode.put("data",objectNode);
        rootNode.put("code", ApiMsgConstants.SUCCESS_CODE);
        return rootNode;
    }

    @Transactional(readOnly = false)
    public int unbanUser(String groupid, String huanid){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("groupId",groupid);
        params.put("customerId", huanid);
        return chatDao.unbanUser(params);
    }

    @Transactional(readOnly = false)
    public int banUser(String groupid, String huanid){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("groupId",groupid);
        params.put("customerId", huanid);
        return chatDao.banUser(params);
    }

    public Boolean isOwner(String groupid, String huanid){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("groupId",groupid);
        params.put("customerId", huanid);
        return chatDao.isOwner(params);
    }

    public ObjectNode getBannedList(String gid){
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        List<String> list = chatDao.getBannedList(gid);
        for(String cid : list){
            arrayNode.add(cid);
        }
        rootNode.put("data",arrayNode);
        rootNode.put("code", ApiMsgConstants.SUCCESS_CODE);
        return rootNode;
    }

    @Transactional(readOnly = false)
    public void removeUserFromGroup(String groupid, String huanid){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("groupId",groupid);
        params.put("customerId", huanid);
        chatDao.deleteUserFromGroup(params);
    }

    public boolean isExceeded(String groupid){
        return chatDao.isExceeded(groupid);
    }

    @Transactional(readOnly = false)
    public void addUserToGroup(String groupid, String huanid){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("groupId",groupid);
        params.put("customerId", huanid);
        Integer intObj = chatDao.findGroupUserById(params);
        if(intObj == null ||  intObj < 1) {
            Customer customer = getCustomer(huanid);
            Map<String, Object> groupCustomerParams = new HashMap<String, Object>();
            groupCustomerParams.put("id", IdGen.uuid());
            groupCustomerParams.put("groupId", groupid);
            groupCustomerParams.put("customerId", huanid);
            groupCustomerParams.put("owner", 0);
            groupCustomerParams.put("banned", 0);
            groupCustomerParams.put("alias", customer.getUserName());
            chatDao.insertGroupCustomer(groupCustomerParams);
        }
    }

    public boolean isJoinedGroup(String groupid, String huanid){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("groupId",groupid);
        params.put("customerId", huanid);
        Integer intObj = chatDao.findGroupUserById(params);
        return !(intObj == null || intObj < 1);
    }

    public String getAvatarUrl(String avatarUrl) {
        if(org.apache.commons.lang3.StringUtils.isEmpty(avatarUrl) ){
            return  "";
        } else if(avatarUrl!=null&&avatarUrl.contains("http://")){
            return avatarUrl;
        } else {
            return QCloudUtils.createThumbDownloadUrl(avatarUrl, ApiConstants.DEFAULT_AVA_THUMBSTYLE);
        }
    }

    public Map<String, Object> getGroupInfo(String gid) {
        return chatDao.getGroupInfo(gid);
    }

    public int getCurrentSize(String gid){
        return chatDao.getCurrentSize(gid);
    }

    public ObjectNode getGroupDetails(String gid, boolean isOwner){
        List<Map<String, Object>> maps = chatDao.getGroupUsers(gid);
        Map<String, Object> info = chatDao.getGroupInfo(gid);
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        if(info == null || info.isEmpty()){
            rootNode.put("msg","该群聊不存在。");
            rootNode.put("code", ApiMsgConstants.FAILED_CODE);
            return rootNode;
        }
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for(Map<String, Object> map:maps){
            ObjectNode cnode = JsonNodeFactory.instance.objectNode();
            cnode.put("huanid", (String) map.get("customer_id"));
            cnode.put("alias", (String) map.get("alias"));
            cnode.put("avatarurl", getAvatarUrl((String) map.get("avatar_url")));
            cnode.put("banned", (Boolean) map.get("banned"));
            cnode.put("owner", (Boolean) map.get("owner"));
            cnode.put("username", (String) map.get("user_name"));
            cnode.put("accumulativebonus", StringUtils.toNumberString((BigDecimal) map.get("accumulative_bonus")));
            cnode.put("monthlybonus", StringUtils.toNumberString((BigDecimal) map.get("monthly_bonus")));
            cnode.put("holdbonus", StringUtils.toNumberString((BigDecimal) map.get("hold_bonus")));
            arrayNode.add(cnode);
        }
        node.put("affiliations",arrayNode);
        node.put("affiliations_count",maps.size());
        node.put("name", (String) info.get("name"));
        node.put("description", (String) info.get("description"));
        node.put("ispublic", (Boolean) info.get("public"));
        node.put("allowinvites", (Boolean) info.get("allow_invites"));
        node.put("maxusers", (Integer) info.get("max_users"));
        node.put("groupavatar", getAvatarUrl((String)info.get("group_avatar")));
        node.put("groupid", gid);
        node.put("isowner", isOwner);
        rootNode.put("data",node);
        rootNode.put("code", ApiMsgConstants.SUCCESS_CODE);
        return rootNode;
    }

    public ObjectNode getGroupDetailsForMsg(String gid){
        List<Map<String, Object>> maps = chatDao.getGroupUsers(gid);
        Map<String, Object> info = chatDao.getGroupInfo(gid);
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for(Map<String, Object> map:maps){
            ObjectNode cnode = JsonNodeFactory.instance.objectNode();
            cnode.put("huanid", (String) map.get("customer_id"));
            cnode.put("alias", (String) map.get("alias"));
            cnode.put("avatarurl", getAvatarUrl((String) map.get("avatar_url")));
            cnode.put("banned", (Boolean) map.get("banned"));
            cnode.put("owner", (Boolean) map.get("owner"));
            cnode.put("username", (String) map.get("user_name"));
            cnode.put("accumulativebonus", StringUtils.toNumberString((BigDecimal) map.get("accumulative_bonus")));
            cnode.put("monthlybonus", StringUtils.toNumberString((BigDecimal) map.get("monthly_bonus")));
            cnode.put("holdbonus", StringUtils.toNumberString((BigDecimal) map.get("hold_bonus")));
            arrayNode.add(cnode);
        }
        node.put("affiliations",arrayNode);
        node.put("affiliations_count",maps.size());
        node.put("name", (String) info.get("name"));
        node.put("description", (String) info.get("description"));
        node.put("ispublic", (Boolean) info.get("public"));
        node.put("allowinvites", (Boolean) info.get("allow_invites"));
        node.put("maxusers", (Integer) info.get("max_users"));
        node.put("groupavatar", getAvatarUrl((String)info.get("group_avatar")));
        node.put("groupid", gid);
        return node;
    }

    public ObjectNode getJoinGroups(String cid){
        List<Map<String, Object>> maps = chatDao.getJoinGroups(cid);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
		if (maps != null && !maps.isEmpty())
			for (Map<String, Object> map : maps) {
				ObjectNode node = JsonNodeFactory.instance.objectNode();
				node.put("groupid", (String) map.get("group_id"));
				node.put("name", (String) map.get("name"));
				node.put("description", (String) map.get("description"));
				node.put("ispublic", (Boolean) map.get("public"));
				node.put("allowinvites", (Boolean) map.get("allow_invites"));
				node.put("maxusers", (Integer) map.get("max_users"));
				node.put("groupavatar",
						getAvatarUrl((String) map.get("group_avatar")));
				node.put("owner", (Boolean) map.get("owner"));
				node.put("banned", (Boolean) map.get("banned"));
				node.put("alias", (String) map.get("alias"));
                node.put("affiliations_count", (Long) map.get("affiliations_count"));
				arrayNode.add(node);
			}
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        rootNode.put("data",arrayNode);
        rootNode.put("code", ApiMsgConstants.SUCCESS_CODE);
        return rootNode;
    }

    @Transactional(readOnly = false)
    public void deleteChatGroups(String id){
        chatDao.deleteGroupsCustomer(id);
        chatDao.deleteGroups(id);
    }

    @Transactional(readOnly = false)
    public int modifyChatGroups(ObjectNode requestNode, String chatGroupId, String imageId){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name",requestNode.get("groupname").asText());
        params.put("description",requestNode.get("description").asText());
        params.put("maxUsers",requestNode.get("maxusers").asText());
        params.put("groupAvatar",imageId);
        params.put("id", chatGroupId);
        params.put("ispublic", requestNode.get("ispublic").asBoolean());
        return chatDao.modifyChatGroups(params);
    }

    @Transactional(readOnly = false)
    public int modifyGroupAvatar(String groupAvatar, String gid){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("groupAvatar", groupAvatar);
        params.put("id", gid);
        return chatDao.modifyGroupAvatar(params);
    }

    @Transactional(readOnly = false)
    public void createCharGroups(ObjectNode requestNode, ObjectNode responseNode, String imageId){
        String gid = responseNode.get("data").get("groupid").asText();
        Map<String,Object> groupParams = new HashMap<String, Object>();
        groupParams.put("id",gid);
        groupParams.put("name",requestNode.get("groupname").asText());
        groupParams.put("description",requestNode.get("desc").asText());
        groupParams.put("public",requestNode.get("public").asBoolean());
        groupParams.put("maxUsers",requestNode.get("maxusers").asInt());
        groupParams.put("allowInvites",requestNode.get("approval").asBoolean());
        groupParams.put("groupAvatar", imageId);
        chatDao.insertGroup(groupParams);
        //插入群成员
        Iterator < JsonNode > nodeIterator = requestNode.withArray("members").iterator();
        while (nodeIterator.hasNext()){
            JsonNode node = nodeIterator.next();
            Customer customer = getCustomer(node.asText());
            Map<String,Object> groupCustomerParams = new HashMap<String, Object>();
            groupCustomerParams.put("id",IdGen.uuid());
            groupCustomerParams.put("groupId",gid);
            groupCustomerParams.put("customerId", customer.getId());
            groupCustomerParams.put("owner",0);
            groupCustomerParams.put("banned",0);
            groupCustomerParams.put("alias",customer.getUserName());
            chatDao.insertGroupCustomer(groupCustomerParams);
        }
        //插入群主
        Customer customer = getCustomer(requestNode.get("owner").asText());
        Map<String,Object> groupCustomerParams = new HashMap<String, Object>();
        groupCustomerParams.put("id",IdGen.uuid());
        groupCustomerParams.put("groupId",gid);
        groupCustomerParams.put("customerId",customer.getId());
        groupCustomerParams.put("owner",1);
        groupCustomerParams.put("banned",0);
        groupCustomerParams.put("alias",customer.getUserName());
        chatDao.insertGroupCustomer(groupCustomerParams);
    }

    private Customer getCustomer(String id){
        return custDao.getCustomerById(id);
    }
}
