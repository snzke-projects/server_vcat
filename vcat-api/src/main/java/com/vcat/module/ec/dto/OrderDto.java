package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vcat.module.ec.entity.Invoice;

public class OrderDto implements Serializable {

	private String id;
	private String orderNum;
	private Date createDate;
	private BigDecimal totalPrice;  //总价格
	private String deliveryNum;
	private String expressCode;//快递公司代码
	private String receiveUserName;
	private String customerAddressId;
	private String city;
	private String paymentId;
	private String buyerId;
	private String province;
	private String district;
	private String receiveDetailAddress;
	private String receivePhoneNum;
	private int totalProductNum; //商品总件数
	private int shippingStatus;//发货状态
	private int reFundcount; // 此订单中含有多少有退款记录的订单项(激活状态)
	private Invoice invoice;
	private String orderType;//订单的状态类型
	private String orderStatus;
	private String productType;//订单的拿样类型 1为普通 2为拿样
	private BigDecimal freightPrice = BigDecimal.ZERO;
	private String isFund;//订单是否确认结算卖家收入
	private String payWay;//支付方式
	private int groupBuyStatus; // 1:进行中 2:成功
	private String groupBuySponsorId; // 拼团Id
	private Date autoCancelDate; // 自动取消订单时间
	private Date autoReceiptDate; // 自动收货时间
	private Boolean isReviewed; // 是否评论
	private Boolean isExtended; // 是否延长过收货时间
	private String groupBuyId; // 是否延长过收货时间

	public String getCustomerAddressId() {
		return customerAddressId;
	}

	public void setCustomerAddressId(String customerAddressId) {
		this.customerAddressId = customerAddressId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getGroupBuyId() {
		return groupBuyId;
	}

	public void setGroupBuyId(String groupBuyId) {
		this.groupBuyId = groupBuyId;
	}

	public Boolean getExtended() {
		return isExtended;
	}

	public void setExtended(Boolean extended) {
		isExtended = extended;
	}

	public Boolean getReviewed() {
		return isReviewed;
	}

	public void setReviewed(Boolean reviewed) {
		isReviewed = reviewed;
	}

	public Date getAutoCancelDate() {
		return autoCancelDate;
	}

	public void setAutoCancelDate(Date autoCancelDate) {
		this.autoCancelDate = autoCancelDate;
	}

	public Date getAutoReceiptDate() {
		return autoReceiptDate;
	}

	public void setAutoReceiptDate(Date autoReceiptDate) {
		this.autoReceiptDate = autoReceiptDate;
	}

	public String getGroupBuySponsorId() {
		return groupBuySponsorId;
	}

	public void setGroupBuySponsorId(String groupBuySponsorId) {
		this.groupBuySponsorId = groupBuySponsorId;
	}

	public int getGroupBuyStatus() {
		return groupBuyStatus;
	}

	public void setGroupBuyStatus(int groupBuyStatus) {
		this.groupBuyStatus = groupBuyStatus;
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	private List<ProductItemDto> productItems = new ArrayList<ProductItemDto>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getDeliveryNum() {
		return deliveryNum;
	}
	public void setDeliveryNum(String deliveryNum) {
		this.deliveryNum = deliveryNum;
	}
	public String getReceiveUserName() {
		return receiveUserName;
	}
	public void setReceiveUserName(String receiveUserName) {
		this.receiveUserName = receiveUserName;
	}
	public String getReceiveDetailAddress() {
		return receiveDetailAddress;
	}
	public void setReceiveDetailAddress(String receiveDetailAddress) {
		this.receiveDetailAddress = receiveDetailAddress;
	}
	public String getReceivePhoneNum() {
		return receivePhoneNum;
	}
	public void setReceivePhoneNum(String receivePhoneNum) {
		this.receivePhoneNum = receivePhoneNum;
	}
	public int getTotalProductNum() {
		return totalProductNum;
	}
	public void setTotalProductNum(int totalProductNum) {
		this.totalProductNum = totalProductNum;
	}
	public List<ProductItemDto> getProductItems() {
		return productItems;
	}
	public void setProductItems(List<ProductItemDto> productItems) {
		this.productItems = productItems;
	}
	public int getShippingStatus() {
		return shippingStatus;
	}
	public void setShippingStatus(int shippingStatus) {
		this.shippingStatus = shippingStatus;
	}
	public String getExpressCode() {
		return expressCode;
	}
	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public int getReFundcount() {
		return reFundcount;
	}
	public void setReFundcount(int reFundcount) {
		this.reFundcount = reFundcount;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	public BigDecimal getFreightPrice() {
		return freightPrice;
	}
	public void setFreightPrice(BigDecimal freightPrice) {
		this.freightPrice = freightPrice;
	}
	public String getIsFund() {
		return isFund;
	}
	public void setIsFund(String isFund) {
		this.isFund = isFund;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	@Override
	public String toString() {
		return "OrderDto{" +
				"id='" + id + '\'' +
				", orderNum='" + orderNum + '\'' +
				", createDate=" + createDate +
				", totalPrice=" + totalPrice +
				", deliveryNum='" + deliveryNum + '\'' +
				", expressCode='" + expressCode + '\'' +
				", receiveUserName='" + receiveUserName + '\'' +
				", city='" + city + '\'' +
				", paymentId='" + paymentId + '\'' +
				", buyerId='" + buyerId + '\'' +
				", province='" + province + '\'' +
				", district='" + district + '\'' +
				", receiveDetailAddress='" + receiveDetailAddress + '\'' +
				", receivePhoneNum='" + receivePhoneNum + '\'' +
				", totalProductNum=" + totalProductNum +
				", shippingStatus=" + shippingStatus +
				", reFundcount=" + reFundcount +
				", invoice=" + invoice +
				", orderType='" + orderType + '\'' +
				", productType='" + productType + '\'' +
				", freightPrice=" + freightPrice +
				", isFund='" + isFund + '\'' +
				", payWay='" + payWay + '\'' +
				", groupBuyStatus=" + groupBuyStatus +
				", groupBuySponsorId='" + groupBuySponsorId + '\'' +
				", autoCancelDate=" + autoCancelDate +
				", autoReceiptDate=" + autoReceiptDate +
				", isReviewed=" + isReviewed +
				", isExtended=" + isExtended +
				", groupBuyId='" + groupBuyId + '\'' +
				", productItems=" + productItems +
				'}';
	}
}
