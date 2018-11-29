package com.vcat.module.json.product;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/18.
 * Description:
 */
public class GetGroupBuyStatusInfoRequest {
    private Integer type; // 不需要 type
    @NotNull
    private String groupBuySponsorId;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getGroupBuySponsorId() {
        return groupBuySponsorId;
    }

    public void setGroupBuySponsorId(String groupBuySponsorId) {
        this.groupBuySponsorId = groupBuySponsorId;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
