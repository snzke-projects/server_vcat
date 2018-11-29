package com.vcat.module.ec.entity;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品规格
 */
public class ProductItem extends DataEntity<ProductItem> {
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.JSON_STYLE);
	}

	private static final long serialVersionUID = 1L;
	private Product product;			// 所属商品
	private String itemSn;				// 规格货号
	private String name;				// 规格名称
	@DataChangeLogField(title = "进价")
	private BigDecimal purchasePrice;	// 进价
	@DataChangeLogField(title = "售价")
	private BigDecimal retailPrice;		// 售价
	@DataChangeLogField(title = "销售奖励")
	private BigDecimal saleEarning;		// 销售奖励
	@DataChangeLogField(title = "分红奖励")
	private BigDecimal bonusEarning;    // 分红奖励
	@DataChangeLogField(title = "一级团队分红")
	private BigDecimal firstBonusEarning;   // 一级团队分红
	@DataChangeLogField(title = "二级团队分红")
	private BigDecimal secondBonusEarning;  // 二级团队分红
	@DataChangeLogField(title = "库存")
	private Integer inventory;			// 库存
	@DataChangeLogField(title = "可用劵的金额")
	private BigDecimal couponValue;     // 可用劵的金额
	@DataChangeLogField(title = "全额抵用卷库存")
	private Integer couponAllInventory;	// 全额抵用卷库存
	@DataChangeLogField(title = "部分抵用卷库存")
	private Integer couponPartInventory;// 部分抵用卷库存
	private String isSellerLoad;        // 卖家是否下架
	@DataChangeLogField(title = "平台扣点")
	private BigDecimal point;           // 平台扣点
	@DataChangeLogField(title = "重量")
	private Integer weight;             // 重量

	private List<Spec> specList;		// 规格属性

	public Map<String,String> getSpec(){
		Map<String,String> spec = new HashMap<>();
		if(StringUtils.isEmpty(remarks)){
			return spec;
		}
		String [] values = remarks.split(",");
		for (int i = 0; i < values.length; i++) {
			String[] entry = values[i].split(":");
			spec.put(entry[0],entry[1]);
		}
		return spec;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getItemSn() {
		return itemSn;
	}
	public void setItemSn(String itemSn) {
		this.itemSn = itemSn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public BigDecimal getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(BigDecimal retailPrice) {
		this.retailPrice = retailPrice;
	}
	public BigDecimal getSaleEarning() {
		return saleEarning;
	}
	public void setSaleEarning(BigDecimal saleEarning) {
		this.saleEarning = saleEarning;
	}
	public Integer getInventory() {
		return inventory;
	}
	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}

	public List<Spec> getSpecList() {
		return specList;
	}

	public void setSpecList(List<Spec> specList) {
		this.specList = specList;
	}

	public String getIsSellerLoad() {
		return isSellerLoad;
	}

	public void setIsSellerLoad(String isSellerLoad) {
		this.isSellerLoad = isSellerLoad;
	}


	public BigDecimal getBonusEarning() {
		return bonusEarning;
	}

	public void setBonusEarning(BigDecimal bonusEarning) {
		this.bonusEarning = bonusEarning;
	}

	public Integer getCouponAllInventory() {
		return couponAllInventory;
	}

	public void setCouponAllInventory(Integer couponAllInventory) {
		this.couponAllInventory = couponAllInventory;
	}

	public Integer getCouponPartInventory() {
		return couponPartInventory;
	}

	public void setCouponPartInventory(Integer couponPartInventory) {
		this.couponPartInventory = couponPartInventory;
	}

	public BigDecimal getCouponValue() {
		return couponValue;
	}

	public void setCouponValue(BigDecimal couponValue) {
		this.couponValue = couponValue;
	}

	public BigDecimal getPoint() {
		return point;
	}

	public void setPoint(BigDecimal point) {
		this.point = point;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public BigDecimal getFirstBonusEarning() {
		return firstBonusEarning;
	}

	public void setFirstBonusEarning(BigDecimal firstBonusEarning) {
		this.firstBonusEarning = firstBonusEarning;
	}

	public BigDecimal getSecondBonusEarning() {
		return secondBonusEarning;
	}

	public void setSecondBonusEarning(BigDecimal secondBonusEarning) {
		this.secondBonusEarning = secondBonusEarning;
	}
}
