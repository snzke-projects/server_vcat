package com.vcat.module.ec.entity;

import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

/**
 * 发票
 * @author cw
 *
 */
public class Invoice extends DataEntity<Invoice> {
	private static final long serialVersionUID = 1L;
	private String title;//发票抬头
	private Date orderTime;//下单时间
	private String type;//发票类型  明细 电脑耗材 等等
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
