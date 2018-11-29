package com.vcat.module.ec.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 分享奖励
 */
public class ShareEarning extends DataEntity<ShareEarning>{
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.JSON_STYLE);
    }

    private static final long serialVersionUID = 1L;
    private Product product;        // 商品
    private Date startTime;         // 奖励开始时间
    private Date endTime;           // 奖励结束时间
    private BigDecimal fund = BigDecimal.ZERO;        // 分享奖励金额
    private Integer availableShare; // 可分享总量
    private Integer sharedCount;    // 已分享数量
    private String title;           // 分享活动主题
    private String imgUrl;          // 活动图片
    private String isActivate = NOT_ACTIVATED;	// 是否激活
    private Date activateTime;      // 激活时间

    public String getStatus(){
        String status = "";
        long now = new Date().getTime();
        if("0".equals(isActivate) || now < startTime.getTime()){
            status = "未开始";
        }else if(now > endTime.getTime()){
            status = "已结束";
        }else if(now > startTime.getTime()){
            status = "进行中";
        }
        return status;
    }
    public String getImgUrlPath() {
        return QCloudUtils.createOriginalDownloadUrl(imgUrl);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getFund() {
        return fund;
    }

    public void setFund(BigDecimal fund) {
        this.fund = fund;
    }

    public Integer getAvailableShare() {
        return availableShare;
    }

    public void setAvailableShare(Integer availableShare) {
        this.availableShare = availableShare;
    }

    public String getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(String isActivate) {
        this.isActivate = isActivate;
    }

    public Integer getSharedCount() {
        return sharedCount;
    }

    public void setSharedCount(Integer sharedCount) {
        this.sharedCount = sharedCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getActivateTime() {
        return activateTime;
    }

    public void setActivateTime(Date activateTime) {
        this.activateTime = activateTime;
    }
}
