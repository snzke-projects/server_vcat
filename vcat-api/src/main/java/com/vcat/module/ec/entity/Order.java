package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.common.utils.StatusColor;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单Entity
 */
public class Order extends DataEntity<Order> {
	private static final long serialVersionUID = 1L;
	// 待确认
	public static final String ORDER_STATUS_TO_BE_CONFIRMED = "0";
	// 已确认
	public static final String ORDER_STATUS_CONFIRMED = "1";
	// 已完成
	public static final String ORDER_STATUS_COMPLETED = "2";
	// 已取消
	public static final String ORDER_STATUS_CANCELLED = "3";
	// 未支付
	public static final String PAYMENT_STATUS_UNPAID = "0";
	// 已支付
	public static final String PAYMENT_STATUS_PAID = "1";
	// 待发货
	public static final String SHIPPING_STATUS_TO_BE_SHIPPED = "0";
	// 已发货
	public static final String SHIPPING_STATUS_SHIPPED = "1";
	// 已收货
	public static final String SHIPPING_STATUS_RECEIVING = "2";
    // 普通订单
    public static final int ORDER_TYPE_NORMAL = 1;
    // 拿样订单
    public static final int ORDER_TYPE_SAMPLE = 2;
    // 购物券部分抵扣订单
    public static final int ORDER_TYPE_FULL_DEDUCTION = 3;
    // 购物券全额抵扣订单
    public static final int ORDER_TYPE_PARTIAL_DEDUCTION = 4;
    // 活动邮费订单
    public static final String ORDER_TYPE_POSTAGE = "5";
	private String orderNumber;			// 订单号
	private BigDecimal totalPrice;		// 订单总金额
	private String note;				// 备注
	private Date addDate;				// 订单生成时间
	private Date confirmDate;			// 订单确认时间
	private String paymentStatus;		// 支付状态
	private String orderStatus;			// 订单状态
	private String shippingStatus;		// 发货状态
    private String reconciliationFreightStatus;// 邮费结算状态
    private String reconciliationStatus;// 结算状态
	private Customer buyer;				// 买家
	private Payment payment;			// 支付单
	private String shopName;			// 商铺
	private Address address;			// 收货地址
	private CustomerAddress customerAddress;
	private Shipping shipping;			// 物流信息
	private List<OrderLog> orderLogList;// 订单操作日志
	private List<OrderItem> itemList;	// 订单项集合
	private List<PaymentLog> paymentLogList;// 支付日志

	private String supplierName;		// 供货商名称
	private String productName;			// 商品名称概览
	//2015年6月17日添加
	private String orderType;//订单类型 1为普通 2为那样
	//2015年7月24日添加
	private Invoice invoice;//发票信息
	// 2015年7月30日 添加
	private BigDecimal totalCoupon;//订单使用卷的总额
	private BigDecimal freightPrice;//快递费用
	
	private String supplierId;//供应商id

    private boolean hasReturn = false;  // 是否包含退货信息
    private boolean isAccounting = false;   // 该订单是否已记账
	public String getProductNames(){
		StringBuffer name = new StringBuffer();
		try {
			if(null != itemList && !itemList.isEmpty()){
                for (OrderItem item : itemList){
					name.append("," + item.getProductItem().getProduct().getName());
                }
            }
			if(name.length() > 0){
				name.delete(0,1);
			}
		} catch (Exception e) {
			return "...";
		}
		return name.toString();
	}

    @ExcelField(title="订单号", align=2, sort=20)
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

    @ExcelField(title="订单金额", align=1, sort=80)
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelField(title="下单时间", align=2, sort=30, format = "yyyy-mm-dd hh:ss:mm")
	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

    @ExcelField(title="买家名称", align=2, sort=50, value = "buyer.userName")
	public Customer getBuyer() {
		return buyer;
	}

	public void setBuyer(Customer buyer) {
		this.buyer = buyer;
	}

    @ExcelField(title="买家手机号", align=2, sort=60)
    public String getPhoneNumber(){
        if(null != buyer){
            return buyer.getPhoneNumber();
        }
        return "无";
    }

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

    @ExcelField(title="支付状态", align=2, sort=90, dictType = "ec_payment_status")
	public String getPaymentStatus() {
		return paymentStatus;
	}
    public String getPaymentStatusLabel() {
        return DictUtils.getDictLabel(paymentStatus,"ec_payment_status","未知状态");
    }

	public String getPaymentStatusColor() {
		if(PAYMENT_STATUS_UNPAID.equals(paymentStatus)){
			return StatusColor.TOMATO;
		}else if(PAYMENT_STATUS_PAID.equals(paymentStatus)){
			return StatusColor.GREEN;
		}else{
			return StatusColor.BLACK;
		}
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

    @ExcelField(title="订单状态", align=2, sort=100, dictType = "ec_order_status")
	public String getOrderStatus() {
		return orderStatus;
	}
    public String getOrderStatusLabel() {
        return DictUtils.getDictLabel(orderStatus,"ec_order_status","未知状态");
    }

	public String getOrderStatusColor() {
		if(ORDER_STATUS_TO_BE_CONFIRMED.equals(orderStatus)){
			return StatusColor.TOMATO;
		}else if(ORDER_STATUS_CONFIRMED.equals(orderStatus)){
			return StatusColor.CORNFLOWER_BLUE;
		}else if(ORDER_STATUS_COMPLETED.equals(orderStatus)){
			return StatusColor.GREEN;
		}else if(ORDER_STATUS_CANCELLED.equals(orderStatus)){
			return StatusColor.RED;
		}else{
			return StatusColor.BLACK;
		}
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getShippingStatusColor() {
		if(SHIPPING_STATUS_TO_BE_SHIPPED.equals(shippingStatus)){
			return StatusColor.TOMATO;
		}else if(SHIPPING_STATUS_SHIPPED.equals(shippingStatus)){
			return StatusColor.CORNFLOWER_BLUE;
		}else if(SHIPPING_STATUS_RECEIVING.equals(shippingStatus)){
			return StatusColor.GREEN;
		}else{
			return StatusColor.BLACK;
		}
	}

    @ExcelField(title="发货状态", align=2, sort=110, dictType = "ec_shipping_status")
	public String getShippingStatus() {
		return shippingStatus;
	}
    public String getShippingStatusLabel() {
        return DictUtils.getDictLabel(shippingStatus,"ec_shipping_status","未知状态");
    }

	public void setShippingStatus(String shippingStatus) {
		this.shippingStatus = shippingStatus;
	}

    public String getReconciliationFreightStatus() {
        return reconciliationFreightStatus;
    }
    public String getReconciliationFreightStatusLabel() {
        return "1".equals(reconciliationFreightStatus) ? "已结算" : "未结算";
    }

    public String getReconciliationFreightStatusColor() {
        return "1".equals(reconciliationFreightStatus) ? StatusColor.GREEN : StatusColor.RED;
    }

    public void setReconciliationFreightStatus(String reconciliationFreightStatus) {
        this.reconciliationFreightStatus = reconciliationFreightStatus;
    }

    public String getReconciliationStatus() {
        return reconciliationStatus;
    }
    public String getReconciliationStatusLabel() {
        return DictUtils.getDictLabel(reconciliationStatus,"ec_reconciliation_status","未知状态");
    }

    public String getReconciliationStatusColor() {
        if(ORDER_STATUS_TO_BE_CONFIRMED.equals(reconciliationStatus)){
            return StatusColor.RED;
        }else if(ORDER_STATUS_CONFIRMED.equals(reconciliationStatus)){
            return StatusColor.CORNFLOWER_BLUE;
        }else if(ORDER_STATUS_COMPLETED.equals(reconciliationStatus)){
            return StatusColor.GREEN;
        }else{
            return StatusColor.BLACK;
        }
    }

    public void setReconciliationStatus(String reconciliationStatus) {
        this.reconciliationStatus = reconciliationStatus;
    }

    @ExcelField(title="小店名称", align=2, sort=50)
	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Shipping getShipping() {
		return shipping;
	}

	public void setShipping(Shipping shipping) {
		this.shipping = shipping;
	}

	public List<OrderLog> getOrderLogList() {
		return orderLogList;
	}

	public void setOrderLogList(List<OrderLog> orderLogList) {
		this.orderLogList = orderLogList;
	}

	public List<OrderItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<OrderItem> itemList) {
		this.itemList = itemList;
	}

	public List<PaymentLog> getPaymentLogList() {
		return paymentLogList;
	}

	public void setPaymentLogList(List<PaymentLog> paymentLogList) {
		this.paymentLogList = paymentLogList;
	}

	public String getOrderStatusToBeConfirmed() {
		return ORDER_STATUS_TO_BE_CONFIRMED;
	}

	public String getOrderStatusConfirmed() {
		return ORDER_STATUS_CONFIRMED;
	}

	public String getOrderStatusCompleted() {
		return ORDER_STATUS_COMPLETED;
	}

	public String getOrderStatusCancelled() {
		return ORDER_STATUS_CANCELLED;
	}

	public String getPaymentStatusUnpaid() {
		return PAYMENT_STATUS_UNPAID;
	}

	public String getPaymentStatusPaid() {
		return PAYMENT_STATUS_PAID;
	}

	public String getShippingStatusToBeShipped() {
		return SHIPPING_STATUS_TO_BE_SHIPPED;
	}

	public String getShippingStatusShipped() {
		return SHIPPING_STATUS_SHIPPED;
	}

	public String getShippingStatusReceiving() {
		return SHIPPING_STATUS_RECEIVING;
	}

	public CustomerAddress getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(CustomerAddress customerAddress) {
		this.customerAddress = customerAddress;
	}

    @ExcelField(title="商品名称", align=2, sort=70)
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

    @ExcelField(title="供应商", align=2, sort=40)
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

    @ExcelField(title="订单类型", align=2, sort=10)
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public BigDecimal getTotalCoupon() {
		return totalCoupon;
	}

	public void setTotalCoupon(BigDecimal totalCoupon) {
		this.totalCoupon = totalCoupon;
	}

	public BigDecimal getFreightPrice() {
		return freightPrice;
	}

	public void setFreightPrice(BigDecimal freightPrice) {
		this.freightPrice = freightPrice;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

    public boolean getHasReturn() {
        return hasReturn;
    }

    public void setHasReturn(boolean hasReturn) {
        this.hasReturn = hasReturn;
    }

    public boolean getIsAccounting() {
        return isAccounting;
    }

    public void setAccounting(boolean accounting) {
        isAccounting = accounting;
    }
}
