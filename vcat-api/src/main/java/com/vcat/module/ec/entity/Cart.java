package com.vcat.module.ec.entity;

import java.math.BigDecimal;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.module.core.entity.DataEntity;

public class Cart extends DataEntity<Cart>{
	private static final long serialVersionUID = 2994891349914726782L;
	private String productItemId;
	private String customerId;
	private String shopId;
	private String shopName;
	private int quantity;
	private int inventory;
	private BigDecimal price;
	private String mainUrl;
	private String productName;
	private String productItemName;
	private BigDecimal couponValue;
	//购物车商品类型 1为普通 2为拿样 5为店主拿样
	private String productType;
	//购物车类型为0为正常状态 1为商品处于下架状态 2为商品库存不足状态
	private String cartStatus;
	private String isSellerLoad;//卖家是否下架 1表示下架 0表示上架
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getMainUrl() {
		return QCloudUtils.createThumbDownloadUrl(mainUrl,ApiConstants.DEFAULT_PRODUCT_THUMBSTYLE);
	}
	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}
	public String getProductItemId() {
		return productItemId;
	}
	public void setProductItemId(String productItemId) {
		this.productItemId = productItemId;
	}
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
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public int getInventory() {
		return inventory;
	}
	public void setInventory(int inventory) {
		this.inventory = inventory;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductItemName() {
		return productItemName;
	}
	public void setProductItemName(String productItemName) {
		this.productItemName = productItemName;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getCartStatus() {
		return cartStatus;
	}
	public void setCartStatus(String cartStatus) {
		this.cartStatus = cartStatus;
	}
	public String getIsSellerLoad() {
		return isSellerLoad;
	}
	public void setIsSellerLoad(String isSellerLoad) {
		this.isSellerLoad = isSellerLoad;
	}
	public BigDecimal getCouponValue() {
		return couponValue;
	}
	public void setCouponValue(BigDecimal couponValue) {
		this.couponValue = couponValue;
	}
	
}
