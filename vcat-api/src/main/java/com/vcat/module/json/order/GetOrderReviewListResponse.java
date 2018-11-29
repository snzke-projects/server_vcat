package com.vcat.module.json.order;

import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.ReviewDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Code.Ai on 16/5/22.
 * Description:
 */
public class GetOrderReviewListResponse extends MsgEntity {
    private List<ReviewDto> list = new ArrayList<>();
    public GetOrderReviewListResponse(){
        super(ApiMsgConstants.SUCCESS_MSG,ApiMsgConstants.SUCCESS_CODE);
    }

    public List<ReviewDto> getList() {
        return list;
    }

    public void setList(List<ReviewDto> list) {
        this.list = list;
    }
}
