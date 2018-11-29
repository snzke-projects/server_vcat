package com.vcat.module.ec.entity;

import com.vcat.common.utils.StatusColor;
import com.vcat.module.core.entity.DataEntity;

import java.util.List;

/**
 * 用户升级申请
 */
public class UpgradeRequest extends DataEntity<UpgradeRequest> {
    private Shop shop;              // 申请店铺
    private Shop originalParent;    // 申请店铺原上家店铺
    private Integer status;         // 申请状态

    private List<UpgradeRequestLog> logList;

    public List<UpgradeRequestLog> getLogList() {
        return logList;
    }

    public void setLogList(List<UpgradeRequestLog> logList) {
        this.logList = logList;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Shop getOriginalParent() {
        return originalParent;
    }

    public void setOriginalParent(Shop originalParent) {
        this.originalParent = originalParent;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusLabel(){
        return getStatusLabel(status);
    }

    public String getStatusLabelColor(){
        return getStatusLabelColor(status);
    }

    public static String getStatusLabel(Integer status){
        switch (status) {
            case 0: return "申请待审核";
            case 1: return "审核通过";
            case 2: return "审核拒绝";
            default: return "未申请";
        }
    }

    public static String getStatusLabelColor(Integer status){
        switch (status) {
            case 0: return StatusColor.CORNFLOWER_BLUE;
            case 1: return StatusColor.GREEN;
            case 2: return StatusColor.RED;
            default: return StatusColor.BLACK;
        }
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
