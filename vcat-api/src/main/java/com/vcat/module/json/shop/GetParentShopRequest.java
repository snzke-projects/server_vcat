package com.vcat.module.json.shop;

import com.drew.lang.annotations.NotNull;
import com.vcat.module.core.entity.RequestEntity;

public class GetParentShopRequest extends RequestEntity {

    /**
     * inviteCode : 邀请码
     */
    @NotNull
    private String inviteCode;

    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode;}

    public String getInviteCode() { return inviteCode;}
}
