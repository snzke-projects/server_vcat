package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

/**
 * 待发货订单
 */
public class ShipOrder extends DataEntity<ShipOrder> {
	private static final long serialVersionUID = 1L;
    private String distributionName;
    private String orderId;
    @ExcelField(title="订单号", align=2, sort=10)
    private String orderNumber;
    @ExcelField(title="包含退货", align=2, sort=20)
    private String hasReturn;
    @ExcelField(title="商品名称", align=2, sort=30)
    private String productName;
    @ExcelField(title="规格", align=2, sort=40)
    private String spec;
    @ExcelField(title="数量", align=2, sort=50)
    private String quantity;
    @ExcelField(title="寄件电话", align=2, sort=60)
    private String distributionPhone;
    @ExcelField(title="寄件省份", align=2, sort=70)
    private String distributionProvince;
    @ExcelField(title="寄件城市", align=2, sort=80)
    private String distributionCity;
    @ExcelField(title="寄件区县", align=2, sort=90)
    private String distributionDistrict;
    @ExcelField(title="寄件详细地址", align=2, sort=100)
    private String distributionAddress;
    @ExcelField(title="收件人", align=2, sort=110)
    private String deliveryName;
    @ExcelField(title="收件电话", align=2, sort=120)
    private String deliveryPhone;
    @ExcelField(title="收件省份", align=2, sort=130)
    private String deliveryProvince;
    @ExcelField(title="收件城市", align=2, sort=140)
    private String deliveryCity;
    @ExcelField(title="收件区县", align=2, sort=150)
    private String deliveryDistrict;
    @ExcelField(title="收件详细地址", align=2, sort=160)
    private String detailAddress;

    public String getDistributionName() {
        return distributionName;
    }

    public void setDistributionName(String distributionName) {
        this.distributionName = distributionName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getHasReturn() {
        return hasReturn;
    }

    public void setHasReturn(String hasReturn) {
        this.hasReturn = hasReturn;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDistributionPhone() {
        return distributionPhone;
    }

    public void setDistributionPhone(String distributionPhone) {
        this.distributionPhone = distributionPhone;
    }

    public String getDistributionProvince() {
        return distributionProvince;
    }

    public void setDistributionProvince(String distributionProvince) {
        this.distributionProvince = distributionProvince;
    }

    public String getDistributionCity() {
        return distributionCity;
    }

    public void setDistributionCity(String distributionCity) {
        this.distributionCity = distributionCity;
    }

    public String getDistributionDistrict() {
        return distributionDistrict;
    }

    public void setDistributionDistrict(String distributionDistrict) {
        this.distributionDistrict = distributionDistrict;
    }

    public String getDistributionAddress() {
        return distributionAddress;
    }

    public void setDistributionAddress(String distributionAddress) {
        this.distributionAddress = distributionAddress;
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

    public String getDeliveryProvince() {
        return deliveryProvince;
    }

    public void setDeliveryProvince(String deliveryProvince) {
        this.deliveryProvince = deliveryProvince;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public String getDeliveryDistrict() {
        return deliveryDistrict;
    }

    public void setDeliveryDistrict(String deliveryDistrict) {
        this.deliveryDistrict = deliveryDistrict;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }
}
