package com.vcat.module.ec.entity;

import java.math.BigDecimal;

import com.vcat.module.core.entity.DataEntity;

public class Rank extends DataEntity<Rank> implements Comparable<Rank>{

	private static final long serialVersionUID = 2766155518394798173L;
	
	private int rank;
	private String name;
	private String shopId;
	private String level;
	private String totalFund;
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getTotalFund() {
		return totalFund;
	}
	public void setTotalFund(String totalFund) {
		this.totalFund = totalFund;
	}
	@Override
	public int compareTo(Rank o) {
		BigDecimal thisFund = new BigDecimal(this.totalFund);
		BigDecimal otherFund = new BigDecimal(o.totalFund); 
		if (thisFund.compareTo(otherFund) == 0) {
			return this.name.compareTo(o.name);
		}
		else
		return -thisFund.compareTo(otherFund);
	}
	
}
