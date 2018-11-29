package com.vcat.module.ec.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

public class Coupon extends DataEntity<Coupon> {
	private static final long serialVersionUID = 7309007870868246266L;

	private String name;			//劵名字
	private BigDecimal fund;		//劵金额
	private Integer isActivate;		//是否激活
	private Integer total;			//劵总张数
	private String type;			//劵类型
    private Date startTime;         //开始时间
    private Date endTime;           //结束时间
    private Integer couponUsedCount;//劵已用张数
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
        if(ACTIVATED.equals(isActivate.toString())){
            return "green";
        }else {
            return "red";
        }
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getFund() {
		return fund;
	}
	public void setFund(BigDecimal fund) {
		this.fund = fund;
	}
	public Integer getIsActivate() {
		return isActivate;
	}
	public void setIsActivate(Integer isActivate) {
		this.isActivate = isActivate;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public Integer getCouponUsedCount() {
		return couponUsedCount;
	}
	public void setCouponUsedCount(Integer couponUsedCount) {
		this.couponUsedCount = couponUsedCount;
	}
}
