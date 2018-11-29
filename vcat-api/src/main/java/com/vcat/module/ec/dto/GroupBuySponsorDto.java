package com.vcat.module.ec.dto;

import com.vcat.module.ec.entity.GroupBuy;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
public class GroupBuySponsorDto implements Serializable {
    private static final long serialVersionUID = -7565110491306479592L;
    private String  id;  // 团购发起表Id
    private String  sponsorId;          // 发起人Id
    private Boolean isLocked;           // 此此团购是否还能参加
    private Date    startDate;          // 此次团购开始时间
    private Date    endDate;            // 结束时间
    private Integer status;             // 状态:
    private Integer joinedCount;        // 参加此次团购的人数(已支付)
    private GroupBuyDto groupBuyDto;    // 团购商品信息
    private Integer applyedCount;       // 已经报名的人数(未支付 + 已支付)
    private int type;                   // 团购类型  0:零元团购,1:卖家发起普通团购,2:买家发起普通团购

    public Integer getApplyedCount() {
        return applyedCount;
    }

    public void setApplyedCount(Integer applyedCount) {
        this.applyedCount = applyedCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public GroupBuyDto getGroupBuyDto() {
        return groupBuyDto;
    }

    public void setGroupBuyDto(GroupBuyDto groupBuyDto) {
        this.groupBuyDto = groupBuyDto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public Integer getJoinedCount() {
        return joinedCount;
    }

    public void setJoinedCount(Integer joinedCount) {
        this.joinedCount = joinedCount;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(String sponsorId) {
        this.sponsorId = sponsorId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GroupBuySponsorDto{" +
                "id='" + id + '\'' +
                ", sponsorId='" + sponsorId + '\'' +
                ", isLocked=" + isLocked +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", joinedCount=" + joinedCount +
                ", groupBuyDto=" + groupBuyDto +
                ", applyedCount=" + applyedCount +
                ", type=" + type +
                '}';
    }
}
