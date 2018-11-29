package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemDto implements Serializable{
	private static final long serialVersionUID = 2147294399145241946L;
	private String id;
	private String topicId;
	private String productItemId;
	private String orderId;
	private String shopId;
	private String orderItemType;
	private int quantity;
	private int promotionQuantity;
	private BigDecimal itemPrice;
	private BigDecimal singleCoupon;
	private BigDecimal saleEarning;		// 当前订单项的销售奖励
	private BigDecimal bonusEarning;	// 当前订单项的分红奖励
	private boolean refundStatus;
	private String cartId;
	//商品的类型 1为普通 2为拿样
	private String productType;
	//商品的推荐类型 HOT RESERVE SALE
	private String productRecommendType;
	private String recommendId;

	public boolean isCanRefund() {
		return canRefund;
	}

	public void setCanRefund(boolean canRefund) {
		this.canRefund = canRefund;
	}

	private boolean canRefund;

	public int getPromotionQuantity() {
		return promotionQuantity;
	}

	public void setPromotionQuantity(int promotionQuantity) {
		this.promotionQuantity = promotionQuantity;
	}

	public String getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}

	public String getProductRecommendType() {
		return productRecommendType;
	}

	public void setProductRecommendType(String productRecommendType) {
		this.productRecommendType = productRecommendType;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getProductItemId() {
		return productItemId;
	}
	public void setProductItemId(String productItemId) {
		this.productItemId = productItemId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}
	public boolean isRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(boolean refundStatus) {
		this.refundStatus = refundStatus;
	}
	public String getCartId() {
		return cartId;
	}
	public void setCartId(String cartId) {
		this.cartId = cartId;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getOrderItemType() {
		return orderItemType;
	}
	public void setOrderItemType(String orderItemType) {
		this.orderItemType = orderItemType;
	}
	public BigDecimal getSingleCoupon() {
		return singleCoupon;
	}
	public void setSingleCoupon(BigDecimal singleCoupon) {
		this.singleCoupon = singleCoupon;
	}

	public BigDecimal getBonusEarning() {
		return bonusEarning;
	}

	public void setBonusEarning(BigDecimal bonusEarning) {
		this.bonusEarning = bonusEarning;
	}

	public BigDecimal getSaleEarning() {
		return saleEarning;
	}

	public void setSaleEarning(BigDecimal saleEarning) {
		this.saleEarning = saleEarning;
	}
}
