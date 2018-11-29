package com.vcat.module.json.product;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/12.
 * Description:
 */
public class ShareGroupBuyInfoRequest {
    // 发起人表Id
    @NotNull
    private String groupBuySponsorId;
    private String shareTpe;
    private String payType;

    public String getGroupBuySponsorId() {
        return groupBuySponsorId;
    }

    public void setGroupBuySponsorId(String groupBuySponsorId) {
        this.groupBuySponsorId = groupBuySponsorId;
    }

}
