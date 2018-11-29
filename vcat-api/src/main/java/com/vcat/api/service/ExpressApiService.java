package com.vcat.api.service;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.drew.lang.StringUtil;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.kuaidi100.pojo.NoticeRequest;
import com.vcat.kuaidi100.pojo.Result;
import com.vcat.kuaidi100.pojo.ResultItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.utils.IdGen;
import com.vcat.module.ec.dao.ExpressApiDao;

@Service
@Transactional(readOnly = true)
public class ExpressApiService {
	@Autowired
	private ExpressApiDao dao;

	@Transactional(readOnly = false)
	public void insert(String code, String no, String data) {
		NoticeRequest noticeRequest = JSON.parseObject(data, NoticeRequest.class);
		// 默认签收状态为 在途
		String state = "0";
		if(null != noticeRequest && null != noticeRequest.getLastResult() && StringUtils.isNotEmpty(noticeRequest.getLastResult().getState())){
			state = noticeRequest.getLastResult().getState();
		}
		Date receivingDate = null;
		if(null != noticeRequest && null != noticeRequest.getLastResult() && null != noticeRequest.getLastResult().getData() && !noticeRequest.getLastResult().getData().isEmpty()){
			if(Result.STATE_RECEIVING.equals(noticeRequest.getLastResult().getState())){
				String time = noticeRequest.getLastResult().getData().get(0).getTime();
				if (StringUtils.isNotEmpty(time)){
					receivingDate = DateUtils.parseDate(time);
				}
			}
		}
		dao.insert(IdGen.uuid(), code, no, data, state, receivingDate);
	}

	public String query(String code, String no) {
		code = StringUtils.filterBlank(code);
		no = StringUtils.filterBlank(no);
		return dao.query(code, no);
	}
}
