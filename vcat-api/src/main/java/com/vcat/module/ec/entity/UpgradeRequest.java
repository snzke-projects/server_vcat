package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.List;


public class UpgradeRequest extends DataEntity<UpgradeRequest> {
    private Shop                  shop;          //申请的店铺号
    private Shop                  originalParent;    //原父ID
    private Integer                 status;         //申请状态
    private Integer                 isActivate;     //是否激活
    private List<UpgradeRequestLog> upgradeLogList ;   //申请日志

    public Integer getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(Integer isActivate) {
        this.isActivate = isActivate;
    }

    public Shop getOriginalParent() {
        return originalParent;
    }

    public void setOriginalParent(Shop originalParent) {
        this.originalParent = originalParent;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<UpgradeRequestLog> getUpgradeLogList() {
        return upgradeLogList;
    }

    public void setUpgradeLogList(List<UpgradeRequestLog> upgradeLogList) {
        this.upgradeLogList = upgradeLogList;
    }
}
