package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 体验官
 */
public class Activity extends DataEntity<Activity> {
	private static final long serialVersionUID = 1L;
	private String title;           // 体验官标题
	private String address;			// 地址
	private Integer seat;			// 体验官席位
	private Integer lastSeat;		// 剩余席位
    private String feeName;         // 费用名称
	private BigDecimal fee;			// 费用
	private Date startDate;		    // 开始时间
	private Date endDate;		    // 结束时间
	private String imgUrl;          // 活动图片
	private String intro;           // 简介
	private String details;         // 详情
	private String templateUrl;     // HTML5活动详情地址
	private String isActivate;		// 是否激活
	private Date activateTime;      // 激活时间
    private int feedBackStatus;     // 报告发布状态(0:未发布|1:已发布)
    private ActivityFeedBack feedBack;  // 报告

    public Activity() {}
    public Activity(String id) {
        super(id);
    }

    public String getImgUrlPath(){
		return QCloudUtils.createOriginalDownloadUrl(imgUrl);
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

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
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

	public String getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(String isActivate) {
		this.isActivate = isActivate;
	}

	public Date getActivateTime() {
		return activateTime;
	}

	public void setActivateTime(Date activateTime) {
		this.activateTime = activateTime;
	}

    public int getFeedBackStatus() {
        return feedBackStatus;
    }

    public void setFeedBackStatus(int feedBackStatus) {
        this.feedBackStatus = feedBackStatus;
    }

    public ActivityFeedBack getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(ActivityFeedBack feedBack) {
        this.feedBack = feedBack;
    }
}
