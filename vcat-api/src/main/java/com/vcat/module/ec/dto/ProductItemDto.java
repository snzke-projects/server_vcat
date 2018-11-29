package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.entity.Refund;

public class ProductItemDto implements Serializable {
	private String productItemId;
	private String productId;
	private String shopId;//店铺id
	private String parentShopId;//上级店铺id
	private String parentParentShopId;//上上级店铺id
	private String buyerId;//买家id
	private String shopName;
	private String shopNum;
	private String levelUrl;
	private String orderItemId;
	private String productName;
	private String itemName;
	private BigDecimal itemPrice = BigDecimal.ZERO;
	private int quantity;
	private int promotionQuantity;
	private String mainUrl;
	private String reFundStatus;  //退款状态（0未退款 1退款中 2已退款 3退款失败
	private String returnStatus;  //退货状态（0未处理 1退货申请成功 2退货中 3退货完成 4退货失败）
	private Boolean returnActivate; // 退款申请是否关闭
	private Integer reQuantity;//退款数量
	private BigDecimal itemSaleEarning = BigDecimal.ZERO;//销售奖励
	private BigDecimal itemBonusEarning = BigDecimal.ZERO;//分红奖励
	private BigDecimal itemFirstBonusEarning = BigDecimal.ZERO;//分红奖励
	private BigDecimal itemSecondBonusEarning = BigDecimal.ZERO;//二级分红奖励
	private BigDecimal amount = BigDecimal.ZERO;//二级分红奖励
	private BigDecimal couponValue ;
	private String refundId;//退款id
	private String allreFundStatus;//退款状态类型
	private String checkRefundStatus;// 是否有退款记录 "1", "0"  显示为 查看退款
	private String type; //订单类型
	private String productType; //订单的拿样类型
	private String hasFreightPrice;//退款是否包含邮费
	private Boolean canRefund = true; // 能否退货
	private Boolean isGroupBuyProduct; //是否为团购商品项
	private Boolean archived; // 商品是否下架
	private String allStatus; // 根据其他状态综合为一种状态
	private String groupBuyStatus;
	private String groupBuyId;

	public String getGroupBuyId() {
		return groupBuyId;
	}

	public void setGroupBuyId(String groupBuyId) {
		this.groupBuyId = groupBuyId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getGroupBuyStatus() {
		return groupBuyStatus;
	}

	public void setGroupBuyStatus(String groupBuyStatus) {
		this.groupBuyStatus = groupBuyStatus;
	}

	public String getAllStatus() {
		return allStatus;
	}

	public void setAllStatus(String allStatus) {
		this.allStatus = allStatus;
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public Boolean getReturnActivate() {
		return returnActivate;
	}

	public void setReturnActivate(Boolean returnActivate) {
		this.returnActivate = returnActivate;
	}

	public Boolean getGroupBuyProduct() {
		return isGroupBuyProduct;
	}

	public void setGroupBuyProduct(Boolean groupBuyProduct) {
		isGroupBuyProduct = groupBuyProduct;
	}

	public int getPromotionQuantity() {
		return promotionQuantity;
	}

	public void setPromotionQuantity(int promotionQuantity) {
		this.promotionQuantity = promotionQuantity;
	}

	public Boolean getCanRefund() {
		return canRefund;
	}

	public void setCanRefund(Boolean canRefund) {
		this.canRefund = canRefund;
	}

	public BigDecimal getItemFirstBonusEarning() {
		return itemFirstBonusEarning;
	}

	public void setItemFirstBonusEarning(BigDecimal itemFirstBonusEarning) {
		this.itemFirstBonusEarning = itemFirstBonusEarning;
	}

	private Integer isReview;//商品项是否评价
	private Integer rating;//评分
	private String  reviewText;//评价内容
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public BigDecimal getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}
	public int getQuantity() {
		return quantity;
	}
	public int getFinalQuantity(){
        return quantity + promotionQuantity;
    }
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getMainUrl() {
		return QCloudUtils.createThumbDownloadUrl(mainUrl, ApiConstants.DEFAULT_PRODUCT_THUMBSTYLE);
	}
	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}

	public String getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
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
	public String getShopNum() {
		return shopNum;
	}
	public void setShopNum(String shopNum) {
		this.shopNum = shopNum;
	}
	public String getLevelUrl() {
		return QCloudUtils.createOriginalDownloadUrl(levelUrl);
	}
	public void setLevelUrl(String levelUrl) {
		this.levelUrl = levelUrl;
	}
	public String getReFundStatus() {
		return reFundStatus;
	}
	public void setReFundStatus(String reFundStatus) {
		this.reFundStatus = reFundStatus;
	}
	public String getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}
	public Integer getReQuantity() {
		return reQuantity;
	}
	public void setReQuantity(Integer reQuantity) {
		this.reQuantity = reQuantity;
	}
	public BigDecimal getItemSaleEarning() {
		return itemSaleEarning;
	}
	public void setItemSaleEarning(BigDecimal itemSaleEarning) {
		this.itemSaleEarning = itemSaleEarning;
	}
	public String getRefundId() {
		return refundId;
	}
	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}
	public String getProductItemId() {
		return productItemId;
	}
	public void setProductItemId(String productItemId) {
		this.productItemId = productItemId;
	}

	// reFundStatus;  //退款状态（0未退款 1退款中 2已退款 3退款失败
	// returnStatus;  //退货状态（0未处理 1退货申请成功 2退货中 3退货完成 4退货失败）
	public String getAllreFundStatus() {
		if (((StringUtils.isBlank(reFundStatus) && StringUtils.isBlank(returnStatus) && returnActivate == null  // 无退款记录
				&& ("3".equals(type)  // 待发货
				|| "4".equals(type)   // 待收货
				|| "8".equals(type))  // 订单查询时间小于 xx时间+7天时 --> 8
				|| "9".equals(type))  // 庄园订单
				|| Refund.REFUND_STATUS_FAILURE.equals(reFundStatus)  // 退款失败 3
				|| Refund.RETURN_STATUS_FAILURE.equals(returnStatus)) // 退货失败 4
				&& (ApiConstants.NORMAL.equals(productType) // 普通订单
				|| ApiConstants.VCATLEROY.equals(productType) // 庄园订单
				|| ApiConstants.SUPER.equals(productType)
				|| ApiConstants.GROUPBUY_SELLER.equals(productType)
				|| ApiConstants.GROUPBUY_BUYER.equals(productType))
				&& (canRefund != null && canRefund)) {
			allreFundStatus = "1";// 可以申请退款,订单在待发货或者待收货时,或者订单在签收(确认收货)7天以内
		} else if ((Refund.REFUND_STATUS_NO_REFUND.equals(reFundStatus)  // 未退款 0
				|| Refund.REFUND_STATUS_REFUND.equals(reFundStatus))     // 退款中 1
				&& !Refund.RETURN_STATUS_FAILURE.equals(returnStatus)
				&& (returnActivate != null && returnActivate)) {
			allreFundStatus = "2";  // 退款中
		} else if (Refund.REFUND_STATUS_COMPLETED.equals(reFundStatus)  // 已退款
				&& Refund.RETURN_STATUS_COMPLETED.equals(returnStatus)
				&& returnActivate != null && returnActivate) { // 退货完成
			allreFundStatus = "3"; // 退款完成
		} else if(returnActivate != null && !returnActivate){ // 退款已关闭
			allreFundStatus = "4";
		}else
			allreFundStatus = "5"; // 未知异常
		return allreFundStatus;
	}

	public void setAllreFundStatus(String allreFundStatus) {
		this.allreFundStatus = allreFundStatus;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public BigDecimal getItemBonusEarning() {
		return itemBonusEarning;
	}
	public void setItemBonusEarning(BigDecimal itemBonusEarning) {
		this.itemBonusEarning = itemBonusEarning;
	}
	public String getParentShopId() {
		return parentShopId;
	}
	public void setParentShopId(String parentShopId) {
		this.parentShopId = parentShopId;
	}
	public String getCheckRefundStatus() {
		return checkRefundStatus;
	}
	public void setCheckRefundStatus(String checkRefundStatus) {
		this.checkRefundStatus = checkRefundStatus;
	}
	public BigDecimal getCouponValue() {
		return couponValue;
	}
	public void setCouponValue(BigDecimal couponValue) {
		this.couponValue = couponValue;
	}
	public Integer getIsReview() {
		return isReview;
	}
	public void setIsReview(Integer isReview) {
		this.isReview = isReview;
	}

	public String getReviewText() {
		return reviewText;
	}
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	public BigDecimal getItemSecondBonusEarning() {
		return itemSecondBonusEarning;
	}
	public void setItemSecondBonusEarning(BigDecimal itemSecondBonusEarning) {
		this.itemSecondBonusEarning = itemSecondBonusEarning;
	}
	public String getParentParentShopId() {
		return parentParentShopId;
	}
	public void setParentParentShopId(String parentParentShopId) {
		this.parentParentShopId = parentParentShopId;
	}
	public String getHasFreightPrice() {
		return hasFreightPrice;
	}
	public void setHasFreightPrice(String hasFreightPrice) {
		this.hasFreightPrice = hasFreightPrice;
	}

	@Override
	public String toString() {
		return "ProductItemDto{" +
				"productItemId='" + productItemId + '\'' +
				", productId='" + productId + '\'' +
				", shopId='" + shopId + '\'' +
				", parentShopId='" + parentShopId + '\'' +
				", parentParentShopId='" + parentParentShopId + '\'' +
				", buyerId='" + buyerId + '\'' +
				", shopName='" + shopName + '\'' +
				", shopNum='" + shopNum + '\'' +
				", levelUrl='" + levelUrl + '\'' +
				", orderItemId='" + orderItemId + '\'' +
				", productName='" + productName + '\'' +
				", itemName='" + itemName + '\'' +
				", itemPrice=" + itemPrice +
				", quantity=" + quantity +
				", promotionQuantity=" + promotionQuantity +
				", mainUrl='" + mainUrl + '\'' +
				", reFundStatus='" + reFundStatus + '\'' +
				", returnStatus='" + returnStatus + '\'' +
				", returnActivate=" + returnActivate +
				", reQuantity=" + reQuantity +
				", itemSaleEarning=" + itemSaleEarning +
				", itemBonusEarning=" + itemBonusEarning +
				", itemFirstBonusEarning=" + itemFirstBonusEarning +
				", itemSecondBonusEarning=" + itemSecondBonusEarning +
				", amount=" + amount +
				", couponValue=" + couponValue +
				", refundId='" + refundId + '\'' +
				", allreFundStatus='" + allreFundStatus + '\'' +
				", checkRefundStatus='" + checkRefundStatus + '\'' +
				", type='" + type + '\'' +
				", productType='" + productType + '\'' +
				", hasFreightPrice='" + hasFreightPrice + '\'' +
				", canRefund=" + canRefund +
				", isGroupBuyProduct=" + isGroupBuyProduct +
				", archived=" + archived +
				", allStatus='" + allStatus + '\'' +
				", groupBuyStatus='" + groupBuyStatus + '\'' +
				", groupBuyId='" + groupBuyId + '\'' +
				", isReview=" + isReview +
				", rating=" + rating +
				", reviewText='" + reviewText + '\'' +
				'}';
	}
}
