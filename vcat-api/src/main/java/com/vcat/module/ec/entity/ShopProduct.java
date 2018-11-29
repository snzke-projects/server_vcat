package com.vcat.module.ec.entity;

import java.util.Date;

import com.vcat.module.core.entity.DataEntity;

/**
 * 店铺产品
 */
public class ShopProduct extends DataEntity<ShopProduct> {
	private static final long serialVersionUID = 1L;
	private Shop shop;
	private Product product;
	private Date startDate;				//拿样后开始上架时间
	private Date endDate;				//拿样后必须下架时间
	private Integer archived;			// 是否下架
	private Integer inventory;			//预售商品库存

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Integer getArchived() {
		return archived;
	}
	public void setArchived(Integer archived) {
		this.archived = archived;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
