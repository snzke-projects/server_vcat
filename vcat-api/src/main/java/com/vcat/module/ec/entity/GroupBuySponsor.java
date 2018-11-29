package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * Created by Code.Ai on 16/5/11.
 * Description: 发起人
 */
public class GroupBuySponsor extends DataEntity<GroupBuySponsor> {
    private static final long    serialVersionUID = 4578464222148770578L;
    public static final Integer NO_PAY           = 0;
    public static final Integer PROCESS          = 1;  // 进行中
    public static final Integer SUCCESS          = 2;  // 成功
    public static final Integer START_FAIL       = 3;  // 开团失败
    public static final Integer FAIL             = 4;  // 拼团失败

    private String   groupBuyId;
    private Customer customer;
    private Boolean  isLocked;
    private Integer  status;
    private Date     startDate;
    private Date     endDate;
    private GroupBuy groupBuy;
    private Integer  count;


    public GroupBuy getGroupBuy() {
        return groupBuy;
    }

    public void setGroupBuy(GroupBuy groupBuy) {
        this.groupBuy = groupBuy;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getGroupBuyId() {
        return groupBuyId;
    }

    public void setGroupBuyId(String groupBuyId) {
        this.groupBuyId = groupBuyId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
