package com.vcat.module.json.order;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/23.
 * Description:
 */
public class DelGroupBuyActivityRequest {
    @NotNull
    private String groupBuySponsorId;

    public String getGroupBuySponsorId() {
        return groupBuySponsorId;
    }

    public void setGroupBuySponsorId(String groupBuySponsorId) {
        this.groupBuySponsorId = groupBuySponsorId;
    }
}
