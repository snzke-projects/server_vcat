package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.common.utils.StatusColor;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.entity.User;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
public class GroupBuy extends DataEntity<GroupBuy> {
    @Override
    public String toString() {
        return this.id;
    }
    private static final long serialVersionUID = -2965523768980967997L;
    private String      name;               // 团购名
    private Integer     neededPeople;       // 需要人数
    private Product     product;            // 商品
    private ProductItem productItem;        // 规格
    private BigDecimal  singlePrice;        // 单人价
    private Integer     status;             // 状态 0未开始 1开始 2结束
    private Integer     period;             // 拼团周期
    private Date        startDate;          // 开始时间
    private Date        endDate;            // 结束时间
    private User        operBy;             // 操作人
    private Date        operDate;           // 操作时间
    private Integer     displayOrder;       // 排序
    private String      limitId;            // 限购ID
    private Boolean     isLimit;            // 是否限购
    private Integer     times;              // 限购次数
    private Integer     inventory;          // 团购库存
    private BigDecimal  price;              // 团购价

    public String getStatusLabel() {
        if (0 == status) {
            return "未激活";
        } else if (1 == status) {
            return "进行中";
        } else if (2 == status) {
            return "已结束";
        } else {
            return "位置状态";
        }
    }

    public String getStatusColor() {
        if (0 == status) {
            return StatusColor.CORNFLOWER_BLUE;
        } else if (1 == status) {
            return StatusColor.GREEN;
        } else if (2 == status) {
            return StatusColor.RED;
        } else {
            return StatusColor.BLACK;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNeededPeople() {
        return neededPeople;
    }

    public void setNeededPeople(Integer neededPeople) {
        this.neededPeople = neededPeople;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductItem getProductItem() {
        return productItem;
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public BigDecimal getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(BigDecimal singlePrice) {
        this.singlePrice = singlePrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getLimitId() {
        return limitId;
    }

    public void setLimitId(String limitId) {
        this.limitId = limitId;
    }

    public Boolean getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(Boolean limit) {
        isLimit = limit;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
