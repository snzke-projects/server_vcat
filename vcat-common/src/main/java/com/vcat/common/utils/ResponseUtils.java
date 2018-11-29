package com.vcat.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vcat.common.constant.ApiMsgConstants;

/**
 * Created by ylin on 2015/10/22.
 */
public class ResponseUtils {
    private final static String[] KEYS = {"result","success","maxusers","groupname","description"};

    public static ObjectNode convertResopnse(ObjectNode objectNode) {
        JsonNode sc = objectNode.get("statusCode");
        int code = ApiMsgConstants.FAILED_CODE;
        if(sc != null && sc.isIntegralNumber()){
            code = sc.asInt();
            objectNode.remove("statusCode");
        }
        objectNode.put("code", sc);

        JsonNode edn = objectNode.get("error_description");
        if(edn != null){
            objectNode.remove("error_description");
            objectNode.put("msg", edn.asText());
        }
        return objectNode;
    }

    public static boolean isStatusSuccess(ObjectNode objectNode) {
       return objectNode.get("code").asInt() == ApiMsgConstants.SUCCESS_CODE;
    }

    public static boolean isBusinessSuccess(ObjectNode objectNode) {
        for (int i = 0; i < KEYS.length; i++) {
            JsonNode node = objectNode.get("data").get(KEYS[i]);
            if(node != null && !node.asBoolean()){
                return false;
            }
        }
        return true;
    }

}
