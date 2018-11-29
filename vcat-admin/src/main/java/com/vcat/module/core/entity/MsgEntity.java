package com.vcat.module.core.entity;
/**
 * 接口返回状态实体
 * @author cw
 *2015年5月6日 19:20:56
 */
public class MsgEntity {
	private String msg;
	private int code;
	private int status;
	public String getMsg() {
		return msg;
	}
	public MsgEntity(){};
	public MsgEntity(String msg, int code) {
		super();
		this.msg = msg;
		this.code = code;
	}
	public MsgEntity(String msg, int code, int status) {
		super();
		this.msg = msg;
		this.code = code;
		this.setStatus(status);
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
