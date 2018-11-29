package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;


public class ShopBgImage extends DataEntity<ShopBgImage>{
	private static final long serialVersionUID = 1L;
	private Shop shop;
	private BgImage bgImage;
	private int displayOrder;
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	public BgImage getBgImage() {
		return bgImage;
	}
	public void setBgImage(BgImage bgImage) {
		this.bgImage = bgImage;
	}
	public int getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

}
