package com.vcat.module.ec.entity;

import java.math.BigDecimal;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;

public class Brand extends DataEntity<Brand>{

	private static final long serialVersionUID = -4340257749061482593L;
	private String name;
	private String description;
	private String logoUrl;
    private BigDecimal freightCharge;   // 邮费
    private String intro;               // 品牌简介
    private Supplier supplier;          // 供应商


	public String getLogoUrl() {
		return logoUrl;
	}

	public String getLogoUrlPath() {
		return QCloudUtils.createOriginalDownloadUrl(logoUrl);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getFreightCharge() {
		return freightCharge;
	}

	public void setFreightCharge(BigDecimal freightCharge) {
		this.freightCharge = freightCharge;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
