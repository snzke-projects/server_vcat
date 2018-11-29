package com.vcat.api.service;

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
import com.vcat.module.ec.dao.ShortUrlDao;
import com.vcat.module.ec.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ylin on 2015/10/22.
 */
@Service
@Transactional(readOnly = true)
public class ShortUrlService {
    @Autowired
    private ShortUrlDao shortUrlDao;

    @Transactional(readOnly = false)
    public void insert(String longUrl, String shortUrl){
        shortUrlDao.insertShortUrl(IdGen.uuid(), longUrl, shortUrl);
    }

    public String getLongUrl(String shortUrl){
        return shortUrlDao.getLongUrl(shortUrl);
    }

    public String getShortUrl(String longUrl) {
        return shortUrlDao.getShortUrl(longUrl);
    }

}
