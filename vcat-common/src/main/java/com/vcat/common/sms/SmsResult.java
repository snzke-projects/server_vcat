package com.vcat.common.sms;

import java.io.Serializable;

/**
 * 短信结果
 */
public class SmsResult implements Serializable{
	public final static String SUCC = "0"; // 结果码 成功
	private String code;	// 结果码 0：成功 2：失败
	private String msg;		// 消息
	private String detail;	// 详情

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "SmsResult{" +
				"code='" + code + '\'' +
				", msg='" + msg + '\'' +
				", detail='" + detail + '\'' +
				'}';
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}
