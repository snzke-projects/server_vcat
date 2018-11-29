package com.vcat.api.service;

import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.GroupBuySponsorDao;
import com.vcat.module.ec.dto.GroupBuyCustomerDto;
import com.vcat.module.ec.dto.GroupBuyProductDto;
import com.vcat.module.ec.dto.GroupBuySponsorDto;
import com.vcat.module.ec.entity.GroupBuySponsor;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.Shop;
import com.vcat.module.json.product.GetGroupBuyStatusInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
@Service
@Transactional(readOnly = true)
public class GroupBuySponsorService extends CrudService<GroupBuySponsor> {
    @Autowired
    private GroupBuySponsorDao groupBuySponsorDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private GroupBuyCustomerService groupBuyCustomerService;
    @Autowired
    private ShopService shopService;
    @Override
    protected CrudDao<GroupBuySponsor> getDao() {
        return groupBuySponsorDao;
    }
    @Transactional(readOnly = false)
    public void insertGroupBuySponsor(GroupBuySponsorDto groupBuySponsorDto){
        groupBuySponsorDao.insertGroupBuySponsor(groupBuySponsorDto);
    }

    public GroupBuySponsorDto getGroupBuySponsor(GroupBuySponsorDto groupBuySponsorDto){
        return groupBuySponsorDao.getGroupBuySponsor(groupBuySponsorDto);
    }

    @Transactional(readOnly = false)
    public void updateGroupBuySponsor(GroupBuySponsorDto groupBuySponsorDto){
        groupBuySponsorDao.updateGroupBuySponsor(groupBuySponsorDto);
    }

    @Transactional(readOnly = false)
    public void lockGroupBuy(String groupBuySponsorId,int lockType){
        groupBuySponsorDao.lockGroupBuy(groupBuySponsorId,lockType);
    }

    // 根据shopId获取店铺中上架的团购商品产生的拼团活动列表
    public int getGroupBuyActivityCount(String shopId,int type){
        return groupBuySponsorDao.getGroupBuyActivityCount(shopId,type);
    }

    public List<Map<String,Object>> getMyGroupBuyActivityList(String shopId,int type,Pager page){
        return groupBuySponsorDao.getMyGroupBuyActivityList(shopId,type,page);
    }

    @Transactional(readOnly = false)
    public void updateGroupBuyActivity(String groupBuySponsorId){
        groupBuySponsorDao.updateGroupBuyActivity(groupBuySponsorId);
    }

    @Transactional(readOnly = false)
    public int closeSponsor(String sponsorId){
        return groupBuySponsorDao.closeSponsor(sponsorId);
    }

    // 根据 customerId 和 groupBuySponsorId 查询拼团状态,跳转到不同的页面
    public GetGroupBuyStatusInfoResponse gotoPageByStatus(GetGroupBuyStatusInfoResponse response,
                                                          String customerId,
                                                          GroupBuySponsorDto groupBuySponsorDto,
                                                          String from){
        List<GroupBuyProductDto> moreGroupBuyList       = new ArrayList<>();
        Map<String,Object>       map                    = groupBuystatusByCustomer(customerId,groupBuySponsorDto.getId());
        int                      groupBuyStatus         = 0;     // 返回的团购状态
        String                   groupBuyStatusToString = "";
        int                      groupBuyStep           = 0;       // 步骤

        // 如果卖家开团 shopId是卖家的 shopId
        String shopId = "";
        if(groupBuySponsorDto.getType() == 0 || groupBuySponsorDto.getType() == 1){
            shopId = (String) map.get("sponsorId");
        }
        else if(groupBuySponsorDto.getType() == 2){
            shopId = groupBuyCustomerService.getShopIdByGroupBuySponsorId(groupBuySponsorDto.getId());
        }
        if(Objects.equals(map.get("groupBuyStatus"), GroupBuySponsor.SUCCESS + "")){ // 如果此拼团状态为成功
            groupBuyStatus = ApiConstants.GROUPBUY_SUCCESS_PAGE;
            groupBuyStatusToString = "拼团成功页";
            groupBuyStep = ApiConstants.SUCCESS_STEP;
            moreGroupBuyList = productService.getMoreGroupBuyProductList(shopId,from);
            // 如果是拼团成功 点击商品详情时显示发起者Id
        }else if(Objects.equals(map.get("groupBuyStatus"), GroupBuySponsor.FAIL + "")
                || Objects.equals(map.get("groupBuyStatus"), GroupBuySponsor.START_FAIL + "")){
            groupBuyStatus = ApiConstants.GROUPBUY_FAIL_PAGE;
            groupBuyStatusToString = "拼团失败页";
            groupBuyStep = ApiConstants.NO_STEP;
            moreGroupBuyList = productService.getMoreGroupBuyProductList(shopId,from);
            // 拼团失败 商品详情显示为发起者的Id
        }else if(Objects.equals(map.get("groupBuyStatus"), GroupBuySponsor.PROCESS + "")){ // 拼团进行中
            if(map.get("isJoined") != null){ // 参加了拼团
                if(map.get("sponsorId").equals(customerId) && (Integer)map.get("isJoined") == 1){  // 如果是本人开团 已支付
                    groupBuyStatus = ApiConstants.START_SUCCESS_PAGE;
                    groupBuyStatusToString = "开团成功页";
                    groupBuyStep = ApiConstants.WAIT_JOIN_STEP;
                    moreGroupBuyList = productService.getMoreGroupBuyProductList(shopId,from);
                    // 如果参加了此拼团且是当前登录的用户就是开团者且已支付 显示的商品详情为发起者Id
                }else  if(!map.get("sponsorId").equals(customerId) && (Integer)map.get("isJoined") == 1){ // 参团且支付成功
                    groupBuyStatus = ApiConstants.JOIN_SUCCESS_PAGE;
                    groupBuyStatusToString = "参团成功页";
                    groupBuyStep = ApiConstants.WAIT_JOIN_STEP;
                    moreGroupBuyList = productService.getMoreGroupBuyProductList(shopId,from);
                    // 如果当前登录的用户参团且已支付 --> 参团已支付 则显示的商品详情为发起者Id
                }else  if(!map.get("sponsorId").equals(customerId) && (Integer)map.get("isJoined") == 0){ // 已参加未支付
                    groupBuyStatus = ApiConstants.JOIN_PAGE;
                    groupBuyStatusToString = "参团页";
                    groupBuyStep = ApiConstants.WAIT_JOIN_STEP;
                    // 如果登录用户不是发起人 且 已参加但是未支付
                }
            }else{ // 如果没有参团
                if((boolean)map.get("isLocked")){
                    groupBuyStatus = ApiConstants.HAVE_CHANCE_PAGE;
                    groupBuyStatusToString = "有机会页";
                    groupBuyStep = ApiConstants.WAIT_JOIN_STEP;
                    moreGroupBuyList = productService.getMoreGroupBuyProductList(shopId,from);
                    // 未参加拼团
                }else{
                    groupBuyStatus = ApiConstants.JOIN_PAGE;
                    groupBuyStatusToString = "参团页";
                    groupBuyStep = ApiConstants.WAIT_JOIN_STEP;
                }
            }
        }

        // 如果是买家开团 shopId是此团购商品所属店铺的Id
        response.setGroupBuyStatus(groupBuyStatus);
        response.setGroupBuyStatusToString(groupBuyStatusToString);
        response.setGroupBuyStep(groupBuyStep);
        response.setGroupBuyList(moreGroupBuyList);
        response.setShopId(shopId);
        response.setShopName(shopService.get(shopId).getName());
        return response;
    }

    public Map<String,Object> groupBuystatusByCustomer(String customerId, String groupBuySponsorId){
        return groupBuySponsorDao.gotoPageByStatus(customerId, groupBuySponsorId);
    }
}
