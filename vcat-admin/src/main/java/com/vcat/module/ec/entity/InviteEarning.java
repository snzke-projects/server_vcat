package com.vcat.module.ec.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

public class InviteEarning extends DataEntity<InviteEarning>{
	private static final long serialVersionUID = -5842065908020950850L;
	private BigDecimal inviterEarning = BigDecimal.ZERO;// 邀请人奖励
	private BigDecimal inviteeEarning = BigDecimal.ZERO;// 被邀请人奖励
	private Integer availableInvite;            // 可邀请次数
	private Date startTime;				        // 开始时间
	private Date endTime;				        // 结束时间
	private String isActivate = NOT_ACTIVATED;  // 是否激活
    private Integer invitedCount;               // 已邀请次数
    public String getStatusColor(){
        String status = getStatus();
        if("已结束".equals(status)){
            return "green";
        }else if("进行中".equals(status)){
            return "orange";
        }else {
            return "red";
        }
    }
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
    public String getActivateColor(){
        if(ACTIVATED.equals(isActivate)){
            return "green";
        }else {
            return "red";
        }
    }
    public Integer getAvailableInvite() {
        return availableInvite;
    }
    public void setAvailableInvite(Integer availableInvite) {
        this.availableInvite = availableInvite;
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

    public String getIsActivate() {
		return isActivate;
	}
	public void setIsActivate(String isActivate) {
		this.isActivate = isActivate;
	}

    public Integer getInvitedCount() {
        return invitedCount;
    }

    public void setInvitedCount(Integer invitedCount) {
        this.invitedCount = invitedCount;
    }
	public BigDecimal getInviterEarning() {
		return inviterEarning;
	}
	public void setInviterEarning(BigDecimal inviterEarning) {
		this.inviterEarning = inviterEarning;
	}
	public BigDecimal getInviteeEarning() {
		return inviteeEarning;
	}
	public void setInviteeEarning(BigDecimal inviteeEarning) {
		this.inviteeEarning = inviteeEarning;
	}
}
