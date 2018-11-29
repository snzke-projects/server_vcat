package com.vcat.module.json.order;

import javax.validation.constraints.NotNull;

/**
 * Created by Code.Ai on 16/5/21.
 * Description:
 */
public class GetGroupBuyActivityListRequest {
    @NotNull
    private int type;  // 0:全部; 1:进行中; 2:拼团成功; 4: 拼团失败
    @NotNull
    private int pageNo;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
