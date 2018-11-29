package com.vcat.api.web.v2;

import com.alibaba.fastjson.JSONObject;
import com.qcloud.UploadResult;
import com.vcat.api.service.*;
import com.vcat.api.web.validation.ValidateParams;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.lock.DistLockHelper;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.utils.ZxingHandler;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.config.CacheConfig;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.dto.AgentShopDto;
import com.vcat.module.ec.dto.ShopInfoDto;
import com.vcat.module.ec.entity.*;
import com.vcat.module.json.product.MyAgentInfoRequest;
import com.vcat.module.json.product.MyAgentsRequest;
import com.vcat.module.json.product.MyAgentsResponse;
import com.vcat.module.json.shop.GetParentShopRequest;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import sun.security.krb5.internal.PAData;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * The type Shop controller 2.
 */
@RestController
@ApiVersion(2)
public class ShopController2 extends RestBaseController {
    @Autowired
    private ShopService              shopService;
    @Autowired
    private UpgradeRequestService    upgradeRequestService;
    @Autowired
    private ShopFundService          shopFundService;
    @Autowired
    private UpgradeRequestLogService upgradeRequestLogService;
    @Autowired
    private ShopInfoService          shopInfoService;

    private static Logger logger = Logger.getLogger(ShopController2.class);

    /**
     * 根据邀请码获取上家店铺信息
     * @param params
     * @return
     */
    @ValidateParams
    @ResponseBody
    @RequestMapping(value = "/anon/getParentShop",method = RequestMethod.POST)
    @Cacheable(value = CacheConfig.GET_PARENT_SHOP_CAHCE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
    public Object getParentShop(@Valid @RequestBody GetParentShopRequest params){
        String inviteCode = params.getInviteCode();
        if(StringUtils.isBlank(inviteCode)){
            return new MsgEntity(ApiMsgConstants.PARENT_SHOP_NOT_FIND,
                    ApiMsgConstants.FAILED_CODE);
        }
        Shop parentShop = shopService.getShopByInviteCode(inviteCode);
        if(parentShop == null) {
            return new MsgEntity(ApiMsgConstants.PARENT_SHOP_NOT_FIND,
                    ApiMsgConstants.FAILED_CODE);
        }
        Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("parentShopId", parentShop.getId());
        map.put("inviteCodeId", parentShop.getMyInviteCodeId());
        map.put("parentShopName", parentShop.getName());
        return map;
    }

    /**
     * 获取小店申请状态接口(当用户从主页进入小店升级页面调用)
     * @return
     */
    @ValidateParams
    @RequiresRoles("seller")
    @RequestMapping(value = "/api/getShopStatus",method = RequestMethod.POST)
    @ResponseBody
    public Object getShopStatus(@RequestHeader(value = "token", defaultValue = "") String token){
        if (StringUtils.isEmpty(token)) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
        }
        String shopId = StringUtils.getCustomerIdByToken(token);
        return upgradeRequestService.getMyShopApplyStatus(shopId);
    }

    /**
     * 小店申请升级为VIP (用户点击 升级为 钻石小店 按钮的时候调用)
     * @param
     * @return
     */
    @ValidateParams
    @RequiresRoles("seller")
    @RequestMapping(value = "/api/upGradeToVIP",method = RequestMethod.POST)
    @ResponseBody
    public Object upGradeToVIP(@RequestHeader(value = "token", defaultValue = "") String token){
        if (StringUtils.isEmpty(token)) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
        }
        //1. 验证销售额是否达到标准
        String     shopId            = StringUtils.getCustomerIdByToken(token);
        Shop shop = shopService.get(shopId);
        if(shop.getAdvancedShop() == 1){
            return new MsgEntity(ApiMsgConstants.ISVIP,ApiMsgConstants.FAILED_CODE);
        }
        // 如果销售额达到邀请 或者店铺状态为2
        BigDecimal defaultSalesLimit = new BigDecimal(DictUtils.getDictValue("ec_upgrade_sales_limit",""));
        BigDecimal nowSales = shopService.getTotalSale(shop.getId());
        if(shop.getAdvancedShop() != 2 && nowSales.compareTo(defaultSalesLimit) < 0 ){ // 如果销售额不符合要求,则弹出提示,退出接口
            return new MsgEntity(ApiMsgConstants.NOT_REACH_ACCOUNT,ApiMsgConstants.FAILED_CODE);
        }

        //2. 申请流程 (提交一个申请)
        UpgradeRequest upgrade = new UpgradeRequest();
        upgrade.setShop(shop);
        upgrade =  upgradeRequestService.get(upgrade);
        if(upgrade == null){
            UpgradeRequest upgradeVIP = new UpgradeRequest();
            upgradeVIP.preInsert();
            upgradeVIP.setShop(shop);
            if(shop.getParentId() != null) {
                Shop originalParent = new Shop();
                originalParent.setId(shop.getParentId());
                upgradeVIP.setOriginalParent(originalParent);
            }
            upgradeVIP.setStatus(0);
            upgradeRequestService.insert(upgradeVIP);
            logger.debug("插入日志表");
            //插入日志表
            UpgradeRequestLog upgradeRequestLog = new UpgradeRequestLog();
            upgradeRequestLog.preInsert();
            upgradeRequestLog.setStatus(0);
            upgradeRequestLog.setUpgradeRequest(upgradeVIP);
            upgradeRequestLog.setNote("");
            upgradeRequestLog.setOperDate(new Date());
            upgradeRequestLogService.insert(upgradeRequestLog);
        }
        else if(upgrade.getStatus() == 1){ //审核已通过
            return new MsgEntity(ApiMsgConstants.ISVIP,ApiMsgConstants.FAILED_CODE);
        }
        else if(upgrade.getStatus() == 2 ){
            //更新申请表状态
            upgrade.setStatus(0);
            upgradeRequestService.update(upgrade);
            //插入一条新申请记录日志
            UpgradeRequestLog upgradeRequestLog = new UpgradeRequestLog();
            upgradeRequestLog.preInsert();
            upgradeRequestLog.setStatus(0);
            upgradeRequestLog.setUpgradeRequest(upgrade);
            upgradeRequestLog.setNote("");
            upgradeRequestLog.setOperDate(new Date());
            upgradeRequestLogService.insert(upgradeRequestLog);
        }
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 查看审核详情 当申请状态为申请中,申请通过,申请不通过都可以调用此接口
     */
    @ValidateParams
    @RequiresRoles("seller")
    @RequestMapping(value = "/api/getUpgradeInfo",method = RequestMethod.POST)
    @ResponseBody
    public Object getUpgradeInfo(@RequestHeader(value = "token", defaultValue = "") String token){
        if (StringUtils.isEmpty(token)) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
        }
        String     shopId            = StringUtils.getCustomerIdByToken(token);
        //根据 shopId 在 upgrade_request 表中查询 状态为1的所有 申请记录
        UpgradeRequest upgradeRequest = new UpgradeRequest();
        Shop shop = new Shop();
        shop.setId(shopId);
        upgradeRequest.setShop(shop);
        List<UpgradeRequestLog> list = upgradeRequestService.findLogs(shopId);
        Map<String,Object>      map  = new HashMap<>();
        map.put("msg",ApiMsgConstants.SUCCESS_MSG);
        map.put("code",ApiMsgConstants.SUCCESS_CODE);
        map.put("list",list);
        return map;
    }

    /**
     * date: 2016.04.05
     * Sprint:6
     * 添加庄园主信息
     * @param token
     * @param productId
     * @return
     * // todo 删除庄园主相关接口
     */
    //@ValidateParams
    //@RequiresRoles("buyer")
    //@RequestMapping(value = "/api/addLeroyerInfo",method = RequestMethod.POST)
    //@ResponseBody
    //public Object addLeroyerInfo(
    //        @RequestHeader(value = "token", defaultValue = "") String token,
    //        @RequestParam(value = "wechatName",defaultValue="") String wechatName,
    //        @RequestParam(value = "productId",defaultValue="") String productId){
    //    ShopInfoDto shopInfoDto = new ShopInfoDto();
    //    String customerId = StringUtils.getCustomerIdByToken(token);
    //    shopInfoDto.setShopId(customerId);
    //    shopInfoDto.setProductId(productId);
    //    RLock lock = DistLockHelper.getLock("addLeroyerInfoLocker");
    //    try {
    //        lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
    //        List<ShopInfoDto> list = shopInfoService.getShopInfo(shopInfoDto);
    //        if(list != null && list.size() >= 1){
    //            return new MsgEntity(ApiMsgConstants.SHOPINFO_EXSIT_MSG,
    //                    ApiMsgConstants.FAILED_CODE);
    //        }
    //        shopInfoService.addLeroyerInfo(wechatName,null,
    //                null, null, customerId, productId);
    //    } finally {
    //        if(lock.isLocked())
    //            lock.unlock();
    //    }
    //    Map<String, Object> map = new HashMap<>();
    //    map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
    //    map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
    //    map.put("wechat",wechatName);
    //    return map;
    //}


    /**
     * 获取庄园主信息接口
     * @param token
     * @return
     * // todo 删除庄园主相关接口
     */

    //@ValidateParams
    //@RequiresRoles("buyer")
    //@RequestMapping(value = "/api/getLeroyerInfo",method = RequestMethod.POST)
    //@Cacheable(value = CacheConfig.GET_LEROY_INFO_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
    //@ResponseBody
    //public Object getLeroyerInfo(@RequestHeader(value = "token", defaultValue = "") String token,
    //                             @RequestParam(value = "params", defaultValue = "") String param
    //                             ){
    //    if (StringUtils.isEmpty(token)) {
    //        return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
    //    }
    //    // 简单验证,保证服务端不会出异常
    //    JSONObject params = null;
    //    try {
    //        params = JSONObject.parseObject(param);
    //    } catch (Exception e) {
    //        logger.error("params 出错"+e.getMessage());
    //        return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
    //                ApiMsgConstants.FAILED_CODE);
    //    }
    //    if (params == null) {
    //        return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
    //                ApiMsgConstants.FAILED_CODE);
    //    }
    //    String     shopId            = StringUtils.getCustomerIdByToken(token);
    //    String     shopInfoId        = params.getString("shopInfoId");
    //    ShopInfoDto shopInfoDto = new ShopInfoDto();
    //    shopInfoDto.setId(shopInfoId);
    //    List<ShopInfoDto> shopInfoDtos = shopInfoService.getShopInfo(shopInfoDto);
    //    Map<String,Object>      map  = new HashMap<>();
    //    map.put("msg",ApiMsgConstants.SUCCESS_MSG);
    //    map.put("code",ApiMsgConstants.SUCCESS_CODE);
    //    map.put("shopInfo",shopInfoDtos.get(0));
    //    return map;
    //}


    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 2016年12月01日  下午 10:52:28 dong4j
     * Sprint:9
     * Gets shop info.
     * 获取店主信息 点击'我的代理商'时调用
     * @param token the token
     * @return the shop info
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/getShopType")
    public Object getShopType(
            @RequestHeader(value = "token", defaultValue = "") String token) {
        String customerId = StringUtils.getCustomerIdByToken(token);
        // 查询用户信息包括小店信息
        Shop shop = shopService.get(customerId);
        if (shop == null) {
            logger.debug("shop is not exsit ");
            return new MsgEntity(ApiMsgConstants.CUSTOMER_NOTEXSIT_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("shopId", shop.getId());
        // 上家的 huanId
        res.put("huanId", shop.getParentId());
        // 上家的 shopId
        res.put("parentId", shop.getParentId());
        res.put("isVIP", (shop.getAdvancedShop() == 1));
        res.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        res.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        return res;
    }

    /**
     * 2016年11月25日  下午 10:37:10 dong4j
     * Sprint:9
     * 获取代理商用户列表
     * @param token
     * @param params type = 0 (白金)
     *                    = 1 (特约)
     *                    = 2 (下下家)
     * 搜索条件:名字
     * 排序方式: 本月分红  累计团队分红
     * @return
     */
    @ValidateParams
    @RequiresRoles("seller")
    @RequestMapping(value = "/api/getMyAgentList",method = RequestMethod.POST)
    @ResponseBody
    public Object getMyAgentList(
            @RequestHeader(value = "token", defaultValue = "") String token,
                              @Valid @RequestBody MyAgentsRequest params){
        //String shopId = "818b1d7a78234a37bd50a74e64c08c80";
        //String shopId = "092a804eed704730a79b68fd4434d346";
        String shopId = StringUtils.getCustomerIdByToken(token);
        int    type   = params.getType();
        int    pageNo = Integer.parseInt(params.getPageNo());
        // 按名字查询
        String name = params.getName();
        // 月排序方式
        String monthOrderType = params.getMonthOrderType();
        // 累计排序方式
        String allOrderType = params.getAllOrderType();
        // type = 0 获取此卖家的所有下家为白金店铺的用户列表
        int    count = 0;
        List<AgentShopDto> list = null;
        Pager page = new Pager();
        page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
        page.setPageNo(pageNo);
        Map<String, Object> request = new HashMap<>();
        request.put("name",name);
        request.put("monthOrderType",monthOrderType);
        request.put("allOrderType",allOrderType);
        request.put("shopId",shopId);
        // 根据类别查询不同的列表
        switch (type){
            case 0:{
                // 白金下家个数
                count = shopService.countChildsIsVIP(request);
                page.setRowCount(count);
                page.doPage();
                list = shopService.getChildListIsVIP(request, page);
                break;
            }
            case 1:{
                // 特约下家个数
                count = shopService.countChildsNotVIP(request);
                page.setRowCount(count);
                page.doPage();
                list = shopService.getChildListNotVIP(request , page);
                break;
            }
            case 2:{
                // 白金下下家
                String chlidShopId = params.getShopId();
                if(chlidShopId == null || chlidShopId.isEmpty() || shopService.get(chlidShopId) == null){
                    return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                            ApiMsgConstants.FAILED_CODE);
                }
                request.put("shopId",chlidShopId); // 白金下家的 id
                count = shopService.countGrandChilds(request);
                page.setRowCount(count);
                page.doPage();
                // 根据白金下家的 id 查找所有下下家是白金的店铺列表,对当前小店的分红贡献
                list = shopService.getGrandChildList(request, page);
                break;
            }
            default:{
                return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                        ApiMsgConstants.FAILED_CODE);
            }
        }
        MyAgentsResponse   response = new MyAgentsResponse();
        response.setList(list);
        response.setPage(page);
        response.setMonthOrderType(monthOrderType);
        response.setAllOrderType(allOrderType);
        return response;
    }

    /**
     * 2016年12月01日  下午 11:03:23 dong4j
     * Sprint:9
     * Gets agent info.
     * 获取代理商详情 下级代理商和下下级代理商(显示对上上级代理商的贡献)
     * @param token the token
     * type = 0 (白金)
     *      = 1 (特约)
     *      = 2 (下下家)
     *      = 3 (我的上家)
     * @return the customer info
     */
    @ValidateParams
    @RequiresRoles("seller")
    @RequestMapping(value = "/api/getMyAgentInfo",method = RequestMethod.POST)
    @ResponseBody
    public Object getMyAgentInfo(@RequestHeader(value = "token", defaultValue = "") String token,
                              @Valid @RequestBody MyAgentInfoRequest params){
        String currentShopId = StringUtils.getCustomerIdByToken(token);
        //String currentShopId = "818b1d7a78234a37bd50a74e64c08c80";
        //String currentShopId = "092a804eed704730a79b68fd4434d346";
        String shopId = params.getShopId();
        int type = params.getType();
        Map<String, Object> request = new HashMap<>();
        request.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        request.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        // 判断 currentShopId 和 shopId之间的关系
        Shop currentShop = shopService.get(currentShopId);
        Shop shop = shopService.get(shopId);
        logger.debug(shop.getId());
        AgentShopDto agentShopDto = null;
        if(currentShop.getParentId() != null){ // 如果当前店铺有上家  只需要返回上家或者上上家店铺信息即可,不需要分红信息
            if(currentShop.getParentId().equals(shopId)){ // 如果当前店铺的上家 id 与 传入的 shopid 相同
                // 传入的 shop 是当前店铺的上家
                agentShopDto = shopService.getShopInfo(shopId);
                agentShopDto.setType(3);
                request.put("shopInfo",agentShopDto);
                return request;
            }
            Shop fatherShop = shopService.get(shopService.get(currentShop.getParentId()).getParentId());
            if(fatherShop != null // 如果当前店铺有上上家
                    && shopId.equals(fatherShop.getId()) // 且传入的 shop 是当前shop 的上上家
                    ){
                // 传入的 shop 是当前店铺的上上家
                agentShopDto = shopService.getShopInfo(shopId);
                agentShopDto.setType(4);
                request.put("shopInfo",agentShopDto);
                return request;
            }
        }
        if(shop.getParentId() != null){ // 如果传入的店铺有上家
            if(shop.getParentId().equals(currentShopId)){ // 如果当前店铺是传入的店铺的上家
                if(shop.getAdvancedShop() == 1){ // 如果是白金下家
                    agentShopDto = shopService.getVIPInfo(shopId);
                    agentShopDto.setHuanId(agentShopDto.getShopId());
                    agentShopDto.setType(0);
                    request.put("shopInfo",agentShopDto);
                    return request;
                }
                else{ // 特约下家
                    agentShopDto = shopService.getNotVIPInfo(shopId);
                    agentShopDto.setHuanId(agentShopDto.getShopId());
                    agentShopDto.setType(1);
                    request.put("shopInfo",agentShopDto);
                    return request;
                }
            }
            Shop grandChildShop = shopService.get(shopService.get(shop.getParentId()).getParentId());
            if(grandChildShop != null
                    && currentShopId.equals(grandChildShop.getId())
                    && grandChildShop.getAdvancedShop() == 1){ // 如果传入的店铺是当前店铺的白金下下家
                agentShopDto = shopService.getGrandVIPInfo(shopId);
                agentShopDto.setHuanId(agentShopDto.getShopId());
                // 我的白金下下家
                agentShopDto.setType(2);
                request.put("shopInfo",agentShopDto);
                return request;
            }
        }

        //<editor-fold desc="注释">
        //// 如果当前 shop的上家 id 和传入的 shopId 相同,则传入的 shop 为当前 shop 的上家
        //if(currentShop != null && shopId.equals(currentShop.getParentId())){
        //    type = 3;
        //}
        //// 如果当前 shop 是传入 shop 的上家且传入的 shop 是白金小店
        //else if(currentShopId.equals(shop.getParentId()) && shop.getAdvancedShop() == 1){
        //    // 白金下家
        //    type = 0;
        //}
        //// 如果当前 shop 是传入 shop 的上家且传入的 shop 是特约小店
        //else if(currentShopId.equals(shop.getParentId()) && shop.getAdvancedShop() != 1){
        //    // 特约下家
        //    type = 1;
        //}
        //// 如果传入的 shop 有上家且 上家的上家 shopId 当前店铺 id 相同
        //else if(shopService.get(shop.getParentId()) != null
        //        && currentShopId.equals(shopService.get(shop.getParentId()).getParentId())
        //        && shopService.get(shop.getParentId()).getAdvancedShop() == 1){
        //    type = 2;
        //}else if(currentShop != null && shopId.equals(shopService.get(currentShop.getParentId()).getParentId())){
        //    type = 4;
        //}
        //AgentShopDto agentShopDto;
        //switch (type){
        //    case 0 :{
        //        agentShopDto = shopService.getVIPInfo(shopId);
        //        agentShopDto.setHuanId(agentShopDto.getShopId());
        //        agentShopDto.setType(0);
        //        // 我的白金下家
        //        break;
        //    }
        //    case 1:{
        //        agentShopDto = shopService.getNotVIPInfo(shopId);
        //        agentShopDto.setHuanId(agentShopDto.getShopId());
        //        // 我的特约下家
        //        agentShopDto.setType(1);
        //        break;
        //    }
        //    case 2:{
        //        agentShopDto = shopService.getGrandVIPInfo(shopId);
        //        agentShopDto.setHuanId(agentShopDto.getShopId());
        //        // 我的白金下下家
        //        agentShopDto.setType(2);
        //        break;
        //    }
        //    case 3:{
        //        agentShopDto = shopService.getShopInfo(shopService.get(currentShopId).getParentId());
        //        // 我的上家
        //        agentShopDto.setType(3);
        //        break;
        //    }
        //    case 4:{
        //        agentShopDto = shopService.getShopInfo(shopService.get(shopService.get(currentShopId).getParentId()).getParentId());
        //        // 我的上上家
        //        agentShopDto.setType(4);
        //        break;
        //    }
        //    default:{
        //        return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
        //                ApiMsgConstants.FAILED_CODE);
        //    }
        //}
        //</editor-fold>
        return request;
    }
}
