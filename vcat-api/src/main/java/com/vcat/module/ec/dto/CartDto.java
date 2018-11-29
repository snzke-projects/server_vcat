package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.util.List;

import com.vcat.module.ec.entity.Cart;

public class CartDto implements Serializable {

	private String shopId;
	private String shopName;
	private List<Cart> cartList;
	private String productType;
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public List<Cart> getCartList() {
		return cartList;
	}
	public void setCartList(List<Cart> cartList) {
		this.cartList = cartList;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
}
