package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.common.utils.StatusColor;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.entity.User;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 伯乐奖励执行对象
 */
public class UpgradeBonus extends DataEntity<UpgradeBonus> {
    private UpgradeRequest request; // 升级申请
    private Shop shop;              // 申请店铺
    private Shop originalParent;    // 申请店铺原上家店铺
    private BigDecimal amount;      // 发放金额
    private String note;            // 执行备注
    private Integer status;         // 执行状态
    private User operBy;            // 执行人
    private Date operDate;          // 执行时间

    public UpgradeRequest getRequest() {
        return request;
    }

    public void setRequest(UpgradeRequest request) {
        this.request = request;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Shop getOriginalParent() {
        return originalParent;
    }

    public void setOriginalParent(Shop originalParent) {
        this.originalParent = originalParent;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusLabel(){
        switch (status){
            case 0:return "未发放";
            case 1:return "已发放";
            case 2:return "不发放";
            default:return "未知";
        }
    }

    public String getStatusColor(){
        switch (status){
            case 0:return StatusColor.CORNFLOWER_BLUE;
            case 1:return StatusColor.GREEN;
            case 2:return StatusColor.RED;
            default:return StatusColor.BLACK;
        }
    }

    public User getOperBy() {
        return operBy;
    }

    public void setOperBy(User operBy) {
        this.operBy = operBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }
}
