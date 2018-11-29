package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.common.utils.StatusColor;
import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * 小团购
 */
public class GroupBuyingSponsor extends DataEntity<GroupBuyingSponsor> {
	private static final long serialVersionUID = 7309007870868246266L;

    private GroupBuying groupBuying;// 所属团购
    private Customer sponsor;       // 团购发起人
    private Boolean isLocked;       // 团购锁定状态 默认0
    private Integer status;         // 团购状态 0开团成功 1进行中 2拼团成功 3开团失败 4拼团失败
    private Date startDate;         // 开始时间
    private Date endDate;           // 结束时间
    private Integer type;           // 团购类型 0卖家发起0元团购 1卖家发起 2买家发起
    private Integer joinCount;      // 参团人数

    public String getStatusLabel(){
        Date now = new Date();
        if(null == status){
            return "未知状态";
        }else if(0 == status && startDate.before(now) && now.before(endDate)){
            return "进行中";
        }else if(1 == status && startDate.before(now) && now.before(endDate)){
            return "进行中";
        }else if(2 == status){
            return "拼团成功";
        }else if(3 == status){
            return "拼团失败";
        }else if(4 == status || endDate.before(now)){
            return "拼团失败";
        }else{
            return "未知状态";
        }
    }

    public String getStatusColor(){
        Date now = new Date();
        if(null == status){
            return "未知状态";
        }else if(0 == status && startDate.before(now) && now.before(endDate)){
            return StatusColor.CORNFLOWER_BLUE;
        }else if(1 == status && startDate.before(now) && now.before(endDate)){
            return StatusColor.CORNFLOWER_BLUE;
        }else if(2 == status){
            return StatusColor.GREEN;
        }else if(3 == status || 4 == status || endDate.before(now)){
            return StatusColor.RED;
        }else{
            return StatusColor.BLACK;
        }
    }

    public String getTypeLabel(){
        if(null == type){
            return "未知团购";
        }else if(0 == type){
            return "0元团购";
        }else if(1 == type){
            return "卖家团购";
        }else if(2 == type){
            return "买家团购";
        }else{
            return "未知团购";
        }
    }
    public GroupBuying getGroupBuying() {
        return groupBuying;
    }

    public void setGroupBuying(GroupBuying groupBuying) {
        this.groupBuying = groupBuying;
    }

    public Customer getSponsor() {
        return sponsor;
    }

    public void setSponsor(Customer sponsor) {
        this.sponsor = sponsor;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(Integer joinCount) {
        this.joinCount = joinCount;
    }
}
