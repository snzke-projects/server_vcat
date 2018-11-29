package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
public class GroupBuyCustomerDto implements Serializable {
    private String  id;
    private String  groupBuySponsorId;
    private String  orderId;
    private String  orderItemId;
    private String  customerId;
    private Integer status;
    private Date    joinedDate;
    private Boolean isSponsor;


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getGroupBuySponsorId() {
        return groupBuySponsorId;
    }

    public void setGroupBuySponsorId(String groupBuySponsorId) {
        this.groupBuySponsorId = groupBuySponsorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getSponsor() {
        return isSponsor;
    }

    public void setSponsor(Boolean sponsor) {
        isSponsor = sponsor;
    }

    public Date getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Date joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
