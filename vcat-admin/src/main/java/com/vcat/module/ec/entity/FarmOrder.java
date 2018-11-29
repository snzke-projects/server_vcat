package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

/**
 * 庄园订单
 */
public class FarmOrder extends DataEntity<FarmOrder> {
	private static final long serialVersionUID = 1L;
    private String productIds;
    private String[] productIdArray;
    @ExcelField(title="店铺名称", align=2, sort=5)
    private String shopName;
    @ExcelField(title="店铺手机号", align=2, sort=6)
    private String shopPhone;   // 小店手机号
    @ExcelField(title="包含退货", align=2, sort=7)
    private String hasReturn;
    @ExcelField(title="发件人", align=2, sort=10)
    private String farmName;
    @ExcelField(title="订单号", align=2, sort=20)
    private String orderNumber;
    @ExcelField(title="商品名称", align=2, sort=30)
    private String productName;
    @ExcelField(title="规格名称", align=2, sort=40)
    private String spec;
    @ExcelField(title="支付时间", align=2, sort=50)
    private String paymentTime;
    @ExcelField(title="拼团成功时间", align=2, sort=55)
    private String groupBuySuccTime;
    @ExcelField(title="收件人姓名", align=2, sort=60)
    private String deliveryName;
    @ExcelField(title="收件电话", align=2, sort=70)
    private String deliveryPhone;
    @ExcelField(title="收件省份", align=2, sort=71)
    private String province;
    @ExcelField(title="收件城市", align=2, sort=72)
    private String city;
    @ExcelField(title="收件区县", align=2, sort=73)
    private String district;
    @ExcelField(title="收件地址", align=2, sort=74)
    private String address;
    @ExcelField(title="收件详细地址", align=2, sort=80)
    private String detailAddress;
    @ExcelField(title="购买数量", align=2, sort=90)
    private String quantity;
    @ExcelField(title="订单备注", align=2, sort=100)
    private String orderNote;
    @ExcelField(title="订单确认时间", align=2, sort=110)
    private String orderConfirmTime;

    public String getProductIds() {
        return productIds;
    }

    public void setProductIds(String productIds) {
        this.productIds = productIds;
    }

    public String[] getProductIdArray() {
        return productIdArray;
    }

    public void setProductIdArray(String[] productIdArray) {
        this.productIdArray = productIdArray;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public String getHasReturn() {
        return hasReturn;
    }

    public void setHasReturn(String hasReturn) {
        this.hasReturn = hasReturn;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getGroupBuySuccTime() {
        return groupBuySuccTime;
    }

    public void setGroupBuySuccTime(String groupBuySuccTime) {
        this.groupBuySuccTime = groupBuySuccTime;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public String getOrderConfirmTime() {
        return orderConfirmTime;
    }

    public void setOrderConfirmTime(String orderConfirmTime) {
        this.orderConfirmTime = orderConfirmTime;
    }
}
