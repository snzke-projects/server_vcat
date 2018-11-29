package com.vcat.module.ec.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

public class InviteEarningLog extends DataEntity<InviteEarningLog>{

	private static final long serialVersionUID = -5842065908020950850L;
	private String inviteEarningId;//推荐收入id
	private String inviter;//邀请人，上家店铺id
	private String invitee;//被邀请人，下家店铺id
	private BigDecimal inviterEarning ;// 邀请人奖励
	private BigDecimal inviteeEarning ;// 被邀请人奖励
	private Date createDate;//创建时间
	public String getInviteEarningId() {
		return inviteEarningId;
	}
	public void setInviteEarningId(String inviteEarningId) {
		this.inviteEarningId = inviteEarningId;
	}
	public String getInviter() {
		return inviter;
	}
	public void setInviter(String inviter) {
		this.inviter = inviter;
	}
	public String getInvitee() {
		return invitee;
	}
	public void setInvitee(String invitee) {
		this.invitee = invitee;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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
