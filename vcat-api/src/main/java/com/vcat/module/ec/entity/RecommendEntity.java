package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 推荐对象
 */
public abstract class RecommendEntity extends DataEntity<RecommendEntity> {
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.JSON_STYLE);
	}

	private static final long serialVersionUID = 1L;
	private Product product;        // 商品
	private Date startTime;         // 折扣开始时间
	private Date endTime;           // 折扣结束时间
	private String typeCode;		// 类型编码
	private BigDecimal discount;    // 折扣率(不能大于1)

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public abstract String getTypeCode();

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
}
