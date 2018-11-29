package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
public class GroupBuyDto implements Serializable {
    private static final long serialVersionUID = -7354906919854375964L;
    private String     id;                  // 团购Id
    private String     productId;           // 商品Id
    private String     productItemId;       // 商品项Id
    private String     groupBuyName;        // 团购名
    private String     productName;         // 商品名
    private Integer    neededPeople;        // 团购人数
    private BigDecimal singlePrice;         // 单人价
    private Integer    status;              // 状态 0未开始 1开始 2结束
    private Integer    period;              // 拼团周期
    private Date       startDate;           // 开始时间
    private Date       endDate;             // 结束时间
    private Integer    limitCount;          // 限购件数
    private Integer    inventory;           // 团购商品库存
    private Integer    groupBuyPrice;       // 团购价
    private Integer    archived;            // 是否下架

    public Integer getArchived() {
        return archived;
    }

    public void setArchived(Integer archived) {
        this.archived = archived;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getGroupBuyName() {
        return groupBuyName;
    }

    public void setGroupBuyName(String groupBuyName) {
        this.groupBuyName = groupBuyName;
    }

    public Integer getGroupBuyPrice() {
        return groupBuyPrice;
    }

    public void setGroupBuyPrice(Integer groupBuyPrice) {
        this.groupBuyPrice = groupBuyPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getInventory() {
        return inventory == null ? 0 : inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public Integer getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public Integer getNeededPeople() {
        return neededPeople;
    }

    public void setNeededPeople(Integer neededPeople) {
        this.neededPeople = neededPeople;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(String productItemId) {
        this.productItemId = productItemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(BigDecimal singlePrice) {
        this.singlePrice = singlePrice;
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
}
