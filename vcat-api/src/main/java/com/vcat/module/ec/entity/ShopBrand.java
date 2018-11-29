package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

public class ShopBrand extends DataEntity<ShopBrand>{

	private static final long serialVersionUID = -5419796352485110905L;
	private Brand brand;
	private Shop shop;

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}

}
