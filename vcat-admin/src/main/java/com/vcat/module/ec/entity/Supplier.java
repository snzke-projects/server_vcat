package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;

/**
 * 供货商Entity
 */
public class Supplier extends DataEntity<Supplier> {
	private static final long serialVersionUID = 1L;
	private String name;		// 名称
	private String logoUrl;		// logo图片地址
	private String siteUrl;		// 网址
	private String phone; 		// 座机
	private String contact;		// 联系人姓名
	private String mobilePhone;	// 移动电话
	private String address;		// 地址
    private String note;        // 备注
    private String description; // 分销说明

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public String getLogoUrlPath() {
		return QCloudUtils.createOriginalDownloadUrl(logoUrl);
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

    public String getOrderStatusToBeConfirmed() {
		return Order.ORDER_STATUS_TO_BE_CONFIRMED;
	}

	public String getOrderStatusConfirmed() {
		return Order.ORDER_STATUS_CONFIRMED;
	}

	public String getOrderStatusCompleted() {
		return Order.ORDER_STATUS_COMPLETED;
	}

	public String getOrderStatusCancelled() {
		return Order.ORDER_STATUS_CANCELLED;
	}

	public String getPaymentStatusUnpaid() {
		return Order.PAYMENT_STATUS_UNPAID;
	}

	public String getPaymentStatusPaid() {
		return Order.PAYMENT_STATUS_PAID;
	}

	public String getShippingStatusToBeShipped() {
		return Order.SHIPPING_STATUS_TO_BE_SHIPPED;
	}

	public String getShippingStatusShipped() {
		return Order.SHIPPING_STATUS_SHIPPED;
	}

	public String getShippingStatusReceiving() {
		return Order.SHIPPING_STATUS_RECEIVING;
	}

	public String getREFUND_STATUS_NO_REFUND() {
		return Refund.REFUND_STATUS_NO_REFUND;
	}

	public String getREFUND_STATUS_REFUND() {
		return Refund.REFUND_STATUS_REFUND;
	}

	public String getREFUND_STATUS_COMPLETED() {
		return Refund.REFUND_STATUS_COMPLETED;
	}

	public String getREFUND_STATUS_FAILURE() {
		return Refund.REFUND_STATUS_FAILURE;
	}

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
