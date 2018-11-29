package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 收藏
 */
public class Favorites extends DataEntity<Favorites> {
	private static final long serialVersionUID = 1L;
	private Customer customer; //买家
	private Product product;	// 商品
	private Shop shop;			// 店铺
	private String favType;//收藏商品类型 1为普通 2为拿样
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public String getFavType() {
		return favType;
	}
	public void setFavType(String favType) {
		this.favType = favType;
	}
}
