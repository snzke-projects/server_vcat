package com.vcat.module.ec.entity;

import com.vcat.common.utils.StatusColor;
import com.vcat.module.core.entity.DataEntity;

/**
 * 邀请码
 */
public class InvitationCode extends DataEntity<InvitationCode> {
    private String code;    // 邀请码
    private Integer status; // 状态
    private Customer usedCustomer;  // 使用该邀请码的用户

    public String getStatusLabel(){
        switch (status){
            case 0:return "可使用";
            case 1:return "已使用";
            case 2:return "已停用";
            default:return "未知状态";
        }
    }

    public String getStatusColor(){
        switch (status){
            case 0:return StatusColor.GREEN;
            case 1:return StatusColor.CORNFLOWER_BLUE;
            case 2:return StatusColor.RED;
            default:return StatusColor.BLACK;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Customer getUsedCustomer() {
        return usedCustomer;
    }

    public void setUsedCustomer(Customer usedCustomer) {
        this.usedCustomer = usedCustomer;
    }
}
