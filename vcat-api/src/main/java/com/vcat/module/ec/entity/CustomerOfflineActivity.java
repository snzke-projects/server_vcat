package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * 2016.1.3添加
 * 对应ec_activity_offline_customer (客户和线下活动关系表)
 */

public class CustomerOfflineActivity extends DataEntity<CustomerOfflineActivity> {
    private static final long serialVersionUID = 8180984439231515226L;
    private Customer        customer;
    private ActivityOffline activityOffline;
    private Date            createDate; //参加活动时间
    private String          phoneNum;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ActivityOffline getActivityOffline() {
        return activityOffline;
    }

    public void setActivityOffline(ActivityOffline activityOffline) {
        this.activityOffline = activityOffline;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }

    @Override
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
