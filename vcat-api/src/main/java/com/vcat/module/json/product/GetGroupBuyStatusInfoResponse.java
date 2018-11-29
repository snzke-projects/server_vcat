package com.vcat.module.json.product;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.GroupBuyProductDto;

import java.util.*;

/**
 * Created by Code.Ai on 16/5/18.
 * Description:
 */
public class GetGroupBuyStatusInfoResponse extends MsgEntity {
    public GetGroupBuyStatusInfoResponse() {
        super(ApiMsgConstants.SUCCESS_MSG, ApiMsgConstants.SUCCESS_CODE);
    }

    private int type;
    private int    groupBuyStatus;                                          // 返回团购状态
    private String groupBuySponsorId;                                       // 拼团Id
    private int    headCount;                                               // 需要人数
    private int    joinedCount;                                             // 参团人数
    private Date   endDate;                                                 // 拼团结束时间
    private int    groupBuyStep;                                            // 拼团步骤
    private Map<String, Object>       itemInfo        = new HashMap<>();    // 商品信息
    private List<Map<String, Object>> joinedCustomers = new ArrayList<>();  // 参团人员信息
    private List<GroupBuyProductDto> groupBuyList     = new ArrayList<>();  // 更多团购商品
    private String groupBuyStatusToString;
    private int limitCount ;
    private String shopId;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
    private String shopName;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    public String getGroupBuyStatusToString() {
        return groupBuyStatusToString;
    }

    public void setGroupBuyStatusToString(String groupBuyStatusToString) {
        this.groupBuyStatusToString = groupBuyStatusToString;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<GroupBuyProductDto> getGroupBuyList() {
        return groupBuyList;
    }

    public void setGroupBuyList(List<GroupBuyProductDto> groupBuyList) {
        this.groupBuyList = groupBuyList;
    }

    public int getGroupBuyStep() {
        return groupBuyStep;
    }

    public void setGroupBuyStep(int groupBuyStep) {
        this.groupBuyStep = groupBuyStep;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getGroupBuySponsorId() {
        return groupBuySponsorId;
    }

    public void setGroupBuySponsorId(String groupBuySponsorId) {
        this.groupBuySponsorId = groupBuySponsorId;
    }

    public int getHeadCount() {
        return headCount;
    }

    public void setHeadCount(int headCount) {
        this.headCount = headCount;
    }

    public void setItemInfo(Map<String, Object> itemInfo) {
        this.itemInfo.put("mainUrl", QCloudUtils.createThumbDownloadUrl((String)itemInfo.get("mainUrl"), 300));
        this.itemInfo.put("name",itemInfo.get("itemName"));
        this.itemInfo.put("groupPrice",itemInfo.get("groupPrice"));
        this.itemInfo.put("singlePrice",itemInfo.get("singlePrice"));
        this.itemInfo.put("groupBuyId",itemInfo.get("groupBuyId"));
        this.itemInfo.put("productId",itemInfo.get("productId"));
        this.itemInfo.put("productItemId",itemInfo.get("productItemId"));
        this.itemInfo.put("productName",itemInfo.get("productName"));
        this.itemInfo.put("value",itemInfo.get("value"));
        this.itemInfo.put("name",itemInfo.get("name"));
        this.itemInfo.put("inventory",itemInfo.get("inventory"));
        this.itemInfo.put("title",itemInfo.get("title"));
    }

    public int getJoinedCount() {
        return joinedCount;
    }

    public void setJoinedCount(int joinedCount) {
        this.joinedCount = joinedCount;
    }

    public List<Map<String, Object>> getJoinedCustomers() {
        return joinedCustomers;
    }

    public void setJoinedCustomers(List<Map<String, Object>> joinedCustomers) {
        for(Map<String, Object> map : joinedCustomers){
            Map<String, Object> joinedCustomer = new HashMap<>();
            joinedCustomer.put("name",map.get("name"));
            joinedCustomer.put("paymentDate",map.get("paymentDate"));
            joinedCustomer.put("isSponsor",map.get("isSponsor"));
            joinedCustomer.put("headUrl",map.get("headUrl"));
            this.joinedCustomers.add(joinedCustomer);
        }
    }

    public Map<String, Object> getItemInfo() {
        return itemInfo;
    }

    public int getGroupBuyStatus() {
        return groupBuyStatus;
    }

    public void setGroupBuyStatus(int groupBuyStatus) {
        this.groupBuyStatus = groupBuyStatus;
    }
}
