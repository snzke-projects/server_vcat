package com.vcat.api.web;

import com.easemob.server.httpclient.api.EasemobMessages;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vcat.common.utils.StringUtils;

import java.util.List;

/**
 * Created by ylin on 2015/10/27.
 */
public class ChatMessageHelper {
    private static final JsonNodeFactory factory = new JsonNodeFactory(false);

    public static void sendBanMessage(String gid, JsonNode cids, JsonNode aliases){
        ObjectNode ext = factory.objectNode();
        ext.put("groupid",gid);
        ext.put("huanids",cids);
        ext.put("aliases",aliases);
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(gid);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "ban");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("chatgroups",targetUsers,cmdMsg,"admin",ext);
    }

    public static void sendUnbanMessage(String gid, JsonNode cids, JsonNode aliases){
        ObjectNode ext = factory.objectNode();
        ext.put("groupid", gid);
        ext.put("huanids", cids);
        ext.put("aliases", aliases);
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(gid);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "unban");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("chatgroups",targetUsers,cmdMsg,"admin",ext);
    }

    public static void sendAddedUserToGroupMessage(String gid, ObjectNode node){
        ObjectNode ext = factory.objectNode();
        ext.put("groupid",gid);
        ext.put("groupinfo", node);
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(gid);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "AddedUserToGroup");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("chatgroups",targetUsers,cmdMsg,"admin",ext);
    }

    public static void sendRemovedUserFromGroupMessage(String gid,String huanid){
        ObjectNode ext = factory.objectNode();
        ext.put("groupid",gid);
        ext.put("huanid",huanid);
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(gid);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "RemovedUserFromGroup");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("chatgroups", targetUsers, cmdMsg, "admin", ext);

        ArrayNode targetUsers1 = factory.arrayNode();
        targetUsers1.add(huanid);
        EasemobMessages.sendMessages("users",targetUsers1,cmdMsg,"admin",ext);
    }

    public static void sendUpdatedAliasMessage(String cid, String gid, String alias){
        ObjectNode ext = factory.objectNode();
        ext.put("groupid",gid);
        ext.put("alias",alias);
        ext.put("huanid",cid);
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(gid);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "UpdatedAlias");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("chatgroups", targetUsers, cmdMsg, "admin", ext);
    }

    public static void sendApplyForGroupMessage(String cid, String gid, String pid){
        ObjectNode ext = factory.objectNode();
        ext.put("groupid",gid);
        ext.put("huanid",cid);
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(pid);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "ApplyForGroup");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("users", targetUsers, cmdMsg, "admin", ext);
    }

    public static void sendApplyForGroupResultMessage(String cid, String gid,boolean result){
        ObjectNode ext = factory.objectNode();
        ext.put("groupid",gid);
        ext.put("result", result);
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(cid);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "ApplyForGroupResult");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("users", targetUsers, cmdMsg, "admin", ext);
    }

    public static void sendChangedUsernameMessage(List<String> huanids, String self, String username){
        ObjectNode ext = factory.objectNode();
        ext.put("huanid",self);
        ext.put("username", username);
        ArrayNode targetUsers = factory.arrayNode();
        for(String huanid : huanids){
            if(!StringUtils.isEmpty(huanid)) {
                targetUsers.add(huanid);
            }
        }
        targetUsers.add(self);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "ChangedUsername");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("users", targetUsers, cmdMsg, "admin", ext);
    }

    public static void sendChangedAvatarMessage(List<String> huanids, String self, String avatar){
        ObjectNode ext = factory.objectNode();
        ext.put("huanid",self);
        ext.put("avatar",avatar);
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(self);
        for(String huanid : huanids){
            if(!StringUtils.isEmpty(huanid)) {
                targetUsers.add(huanid);
            }
        }
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "ChangedAvatar");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("users", targetUsers, cmdMsg, "admin", ext);
    }

    public static void sendJoinedTeamMessage(String parent, JsonNode childInfo){
        ObjectNode ext = factory.objectNode();
        ext.put("childInfo",childInfo);
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(parent);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "JoinedTeam");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("users", targetUsers, cmdMsg, "admin", ext);
    }

    public static void sendQuittedTeamMessage(String parent, String child){
        ObjectNode ext = factory.objectNode();
        ext.put("huanid",child);
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(parent);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "QuittedTeam");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("users", targetUsers, cmdMsg, "admin", ext);
    }

    public static void sendChangedGroupInfoMessage(String groupid ,String groupname, String description,
                                                   int maxusers ,boolean ispublic, String groupavatar) {
        ObjectNode ext = factory.objectNode();
        ext.put("groupid",groupid);
        ext.put("groupname",groupname);
        ext.put("description",description);
        ext.put("maxusers",maxusers);
        ext.put("ispublic",ispublic);
        if(groupavatar != null){
            ext.put("groupavatar", groupavatar);
        }
        ArrayNode targetUsers = factory.arrayNode();
        targetUsers.add(groupid);
        ObjectNode cmdMsg = factory.objectNode();
        cmdMsg.put("action", "ChangedGroupInfo");
        cmdMsg.put("type", "cmd");
        EasemobMessages.sendMessages("chatgroups", targetUsers, cmdMsg, "admin", ext);
    }
}
