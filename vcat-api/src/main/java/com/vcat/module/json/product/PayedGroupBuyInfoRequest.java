package com.vcat.module.json.product;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/13.
 * Description:
 */
public class PayedGroupBuyInfoRequest {
    public static final int ZEROGROUPBUY = 0;
    public static final int STRATGROUPBUY = 1;
    public static final int JOINGROUPBUY = 2;
    public static final int JOINSUCCESS = 3;
    // 发起人表Id
    @NotNull
    private String groupBuySponsorId;
    @NotNull
    private int type;

    public String getGroupBuySponsorId() {
        return groupBuySponsorId;
    }

    public void setGroupBuySponsorId(String groupBuySponsorId) {
        this.groupBuySponsorId = groupBuySponsorId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
