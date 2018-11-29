package com.vcat.module.ec.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.module.core.entity.DataEntity;

public class Suggest extends DataEntity<Suggest> {
	private static final long serialVersionUID = 3469477526786954868L;
	private String contact;     //联系方式
	private String content;     //内容
	private Date createDate;    //创建时间
	private boolean processed;  //是否处理
	private Date processDate;   //处理时间
	private String processUser; //处理人
    public String getProcessLabel(){
        if(processed){
            return "已处理";
        }else {
            return "未处理";
        }
    }
    public String getProcessColor(){
        if(processed){
            return "green";
        }else {
            return "red";
        }
    }
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getProcessDate() {
		return processDate;
	}
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	public String getProcessUser() {
		return processUser;
	}
	public void setProcessUser(String processUser) {
		this.processUser = processUser;
	}
}
