package com.vcat.module.ec.dto;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by: dong4j.
 * Date: 2016-11-25.
 * Time: 22:49.
 * Description:
 */
public class AgentShopDto implements Serializable {
    private static final long serialVersionUID = -4100521776783722279L;
    private String shopId;
    private String huanId;
    private String name;
    private boolean isVIP;
    private String phone;
    private Integer childNum = 0;
    private String avatarUrl;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    // 分红
    private BigDecimal totalFund ;
    private BigDecimal monthTotalFund ;
    private BigDecimal totalHoldFund ;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsVIP() {
        return isVIP;
    }

    public void setIsVIP(boolean isVIP) {
        this.isVIP = isVIP;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getChildNum() {
        return childNum;
    }

    public void setChildNum(Integer childNum) {
        this.childNum = childNum;
    }

    public String getAvatarUrl() {
        if(avatarUrl!=null&&avatarUrl.contains("http://")){
            return avatarUrl;
        }else
            return QCloudUtils.createThumbDownloadUrl(avatarUrl, ApiConstants.DEFAULT_AVA_THUMBSTYLE);
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public BigDecimal getTotalFund() {
        return totalFund == null ? new BigDecimal(0) : totalFund;
    }

    public void setTotalFund(BigDecimal totalFund) {
        this.totalFund = totalFund;
    }

    public BigDecimal getMonthTotalFund() {
        return monthTotalFund == null ? new BigDecimal(0) : monthTotalFund;
    }

    public void setMonthTotalFund(BigDecimal monthTotalFund) {
        this.monthTotalFund = monthTotalFund;
    }

    public BigDecimal getTotalHoldFund() {
        return totalHoldFund == null ? new BigDecimal(0) : totalHoldFund;
    }

    public void setTotalHoldFund(BigDecimal totalHoldFund) {
        this.totalHoldFund = totalHoldFund;
    }

    public String getHuanId() {
        return huanId;
    }

    public void setHuanId(String huanId) {
        this.huanId = huanId;
    }

    @Override
    public String toString() {
        return "AgentShopDto{" +
                "shopId='" + shopId + '\'' +
                ", name='" + name + '\'' +
                ", isVIP='" + isVIP + '\'' +
                ", phone='" + phone + '\'' +
                ", childNum=" + childNum +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", totalFund=" + totalFund +
                ", monthTotalFund=" + monthTotalFund +
                ", totalHoldFund=" + totalHoldFund +
                '}';
    }
}
