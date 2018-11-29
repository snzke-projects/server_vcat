package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;

public class FundDetailBillDto implements Serializable{


	private static final long serialVersionUID = 1L;
	private String fundTypeName;
	private Date fundDate;
	private BigDecimal fund;
	private String buyerAvatarUrl;
	private String buyerName;
	private Integer bonusLevel;//分红级别
	
	public Date getFundDate() {
		return fundDate;
	}
	public void setFundDate(Date fundDate) {
		this.fundDate = fundDate;
	}
	public BigDecimal getFund() {
		return fund;
	}
	public void setFund(BigDecimal fund) {
		this.fund = fund;
	}
	public String getBuyerAvatarUrl() {
		if(buyerAvatarUrl!=null&&buyerAvatarUrl.contains("http://")){
			return buyerAvatarUrl;
		}else
			return QCloudUtils.createThumbDownloadUrl(buyerAvatarUrl,ApiConstants.DEFAULT_AVA_THUMBSTYLE);
	}
	public void setBuyerAvatarUrl(String buyerAvatarUrl) {
		this.buyerAvatarUrl = buyerAvatarUrl;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getFundTypeName() {
		return fundTypeName;
	}
	public void setFundTypeName(String fundTypeName) {
		this.fundTypeName = fundTypeName;
	}
	public Integer getBonusLevel() {
		return bonusLevel;
	}
	public void setBonusLevel(Integer bonusLevel) {
		this.bonusLevel = bonusLevel;
	}	
}
