package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * 活动
 */
public class ActivityOffline extends DataEntity<ActivityOffline> {
	private static final long serialVersionUID = 1L;
	private String title;           // 活动标题
	private String address;			// 地址
	private Integer seat;			// 活动席位
	private Integer lastSeat;		// 剩余席位
	private Date startDate;		    // 开始时间
	private Date endDate;		    // 结束时间
	private String imgUrl;          // 活动图片
    private String ticketUrl;       // 入场卷图片
	private String intro;           // 简介
	private String details;         // 详情
	private String templateUrl;     // HTML5活动详情地址
	private Integer isActivate;		// 是否激活
	private Date activateTime;      // 激活时间
    private Integer openStatus;     // 开放报名状态

    public ActivityOffline() {}
    public ActivityOffline(String id) {
        super(id);
    }

    public String getImgUrlPath(){
		return QCloudUtils.createOriginalDownloadUrl(imgUrl);
	}
    public String getTicketUrlPath(){
        return QCloudUtils.createOriginalDownloadUrl(ticketUrl);
    }
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getSeat() {
		return seat;
	}

	public void setSeat(Integer seat) {
		this.seat = seat;
	}

	public Integer getLastSeat() {
		return lastSeat;
	}

	public void setLastSeat(Integer lastSeat) {
		this.lastSeat = lastSeat;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getTemplateUrl() {
		return templateUrl;
	}

	public void setTemplateUrl(String templateUrl) {
		this.templateUrl = templateUrl;
	}

	public Integer getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Integer isActivate) {
		this.isActivate = isActivate;
	}

	public Date getActivateTime() {
		return activateTime;
	}

	public void setActivateTime(Date activateTime) {
		this.activateTime = activateTime;
	}

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }
}
