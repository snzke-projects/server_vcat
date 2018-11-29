package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.entity.User;

import java.util.Date;

public class UpgradeRequestLog extends DataEntity<UpgradeRequestLog> {
    private String  note;        //备注
    private Integer status;      //状态
    private User    operBy;      //操作人员
    private Date    operDate;    //操作时间
    private UpgradeRequest upgradeRequest; //申请记录

    public UpgradeRequest getUpgradeRequest() {
        return upgradeRequest;
    }

    public void setUpgradeRequest(UpgradeRequest upgradeRequest) {
        this.upgradeRequest = upgradeRequest;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getOperBy() {
        return operBy;
    }

    public void setOperBy(User operBy) {
        this.operBy = operBy;
    }

    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
