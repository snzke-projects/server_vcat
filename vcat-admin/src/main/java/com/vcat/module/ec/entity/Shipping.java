package com.vcat.module.ec.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

/**
 * 发货单
 */
public class Shipping extends DataEntity<Shipping> {
	private static final long serialVersionUID = 1L;
	private String number;			// 物流单号
	private Date shippingDate;		// 发货日期
	private BigDecimal freightCharge;// 运费
	private Order order;			// 所属订单
	private Express express;		// 快递公司
	private String note;			// 备注
	private String operName;		// 操作人
	private Date operDate;			// 操作时间
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public Express getExpress() {
		return express;
	}
	public void setExpress(Express express) {
		this.express = express;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Date getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}
	public BigDecimal getFreightCharge() {
		return freightCharge;
	}
	public void setFreightCharge(BigDecimal freightCharge) {
		this.freightCharge = freightCharge;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public Date getOperDate() {
		return operDate;
	}

	public void setOperDate(Date operDate) {
		this.operDate = operDate;
	}
}
