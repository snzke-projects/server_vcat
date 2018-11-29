package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

public class CouponShop extends DataEntity<CouponShop>{
	private static final long serialVersionUID = -6381551032479302980L;
	private Coupon coupon; //劵
	private Shop shop;//店铺
	public Coupon getCoupon() {
		return coupon;
	}
	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
}
