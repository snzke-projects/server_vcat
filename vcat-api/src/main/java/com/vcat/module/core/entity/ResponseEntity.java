package com.vcat.module.core.entity;

import com.vcat.common.constant.ApiMsgConstants;

import java.util.ArrayList;
import java.util.List;

public class ResponseEntity extends  MsgEntity{
    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    private List list = new ArrayList<>();

    public ResponseEntity(){
        super(ApiMsgConstants.SUCCESS_MSG,ApiMsgConstants.SUCCESS_CODE);
    }
}
