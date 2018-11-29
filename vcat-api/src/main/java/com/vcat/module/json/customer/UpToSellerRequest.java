package com.vcat.module.json.customer;

import com.drew.lang.annotations.NotNull;
import com.vcat.module.core.entity.RequestEntity;


public class UpToSellerRequest extends RequestEntity {

    /**
     * phoneNum : 18628362906
     * passWord : bc05e382e69358f11da8031981ea7749
     * parentShopId :
     */
    @NotNull
    private String phoneNum;
    @NotNull
    private String passWord;
    @NotNull
    private String parentShopId;

    public String getParentShopId() {
        return parentShopId;
    }

    public void setParentShopId(String parentShopId) {
        this.parentShopId = parentShopId;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
