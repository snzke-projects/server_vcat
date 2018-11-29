package com.vcat.module.ec.entity;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * 发货单
 */
public class Invoices extends DataEntity<Invoices> {
	private static final long serialVersionUID = 1L;
    private String orderNumber;
    private String productName;
    private Express express;
    private String shippingNo;
    private String deliveryName;
    private String deliveryPhone;
    private String deliveryAddress;

    public boolean isEmpty(){
        return StringUtils.isBlank(orderNumber)
                && StringUtils.isBlank(productName)
                && null == express
                && StringUtils.isBlank(shippingNo)
                && StringUtils.isBlank(deliveryName)
                && StringUtils.isBlank(deliveryPhone)
                && StringUtils.isBlank(deliveryAddress);
    }

    @ExcelField(title="订单号", align=2, sort=10, type = 2)
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @ExcelField(title="商品名称", align=2, sort=20, type = 2)
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @NotNull(message="物流公司为空或物流公司名称不正确")
    @ExcelField(title="物流公司", align=2, sort=30, type = 2)
    public Express getExpress() {
        return express;
    }

    public void setExpress(Express express) {
        this.express = express;
    }

    @NotBlank(message="物流单号不能为空")
    @ExcelField(title="物流单号", align=2, sort=40, type = 2)
    public String getShippingNo() {
        return shippingNo;
    }

    public void setShippingNo(String shippingNo) {
        this.shippingNo = shippingNo;
    }

    @NotBlank(message = "收件人不能为空")
    @ExcelField(title="收件人", align=2, sort=50, type = 2)
    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    @NotBlank(message="收件电话不能为空")
    @ExcelField(title="收件电话", align=2, sort=60, type = 2)
    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    @NotBlank(message="收件地址不能为空")
    @ExcelField(title="收件地址", align=2, sort=70, type = 2)
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
