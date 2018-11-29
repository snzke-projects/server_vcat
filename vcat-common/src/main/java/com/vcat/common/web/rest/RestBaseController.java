package com.vcat.common.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcat.common.mapper.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RestBaseController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 如果转换失败，返回null
     * @param json
     * @return
     */
    protected Map<String, Object> jsonToMap(final String json) {
        return JsonMapper.getInstance().fromJson(json, Map.class);
    }

}
