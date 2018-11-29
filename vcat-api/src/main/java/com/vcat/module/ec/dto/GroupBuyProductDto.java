package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Code.Ai on 16/5/9.
 * Description:
 */
public class GroupBuyProductDto implements Serializable {
    private String     productId;
    private String     productItemId;
    private String     groupBuyId;
    private Integer    headCount;       //参团人数
    private BigDecimal singlePrice;     // 单人价
    private BigDecimal groupPrice;      // 团购价
    private Date       createDate;      //团购创建时间
    private BigDecimal earning;
    private Integer    inventory;
    private Boolean    hasChance;

    public Boolean getHasChance() {
        return hasChance;
    }

    public void setHasChance(Boolean hasChance) {
        this.hasChance = hasChance;
    }

    public String getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(String productItemId) {
        this.productItemId = productItemId;
    }

    private List<CopywriteDto> copywriteList = new ArrayList<>();// 文案

    public BigDecimal getEarning() {
        return earning;
    }

    public void setEarning(BigDecimal earning) {
        this.earning = earning;
    }

    public List<CopywriteDto> getCopywriteList() {
        return copywriteList;
    }

    public void setCopywriteList(List<CopywriteDto> copywriteList) {
        this.copywriteList = copywriteList;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getGroupBuyId() {
        return groupBuyId;
    }

    public void setGroupBuyId(String groupBuyId) {
        this.groupBuyId = groupBuyId;
    }

    public BigDecimal getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(BigDecimal groupPrice) {
        this.groupPrice = groupPrice;
    }

    public Integer getHeadCount() {
        return headCount;
    }

    public void setHeadCount(Integer headCount) {
        this.headCount = headCount;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(BigDecimal singlePrice) {
        this.singlePrice = singlePrice;
    }
}
