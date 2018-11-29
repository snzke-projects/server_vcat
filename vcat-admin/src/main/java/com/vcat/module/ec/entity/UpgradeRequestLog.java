package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.entity.User;

import java.util.Date;

/**
 * 用户升级申请审批日志
 */
public class UpgradeRequestLog extends DataEntity<UpgradeRequestLog>{
    private String note;    // 审批备注
    private Integer status; // 审批状态
    private User operBy;    // 审批人
    private Date operDate;  // 审批时间

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

    public String getStatusLabel(){
        return UpgradeRequest.getStatusLabel(status);
    }

    public String getStatusLabelColor(){
        return UpgradeRequest.getStatusLabelColor(status);
    }
}
