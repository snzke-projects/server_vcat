package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.common.utils.StatusColor;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.entity.User;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 退款单
 */
public class Refund extends DataEntity<Refund> {
    private static final long serialVersionUID = 1L;
    // 未退货
    public static final String RETURN_STATUS_UNTREATED = "0";
    // 退货审核成功
    public static final String RETURN_STATUS_CHECK_SUCC = "1";
    // 退货中
    public static final String RETURN_STATUS_RETURNS_IN = "2";
    // 退货完成
    public static final String RETURN_STATUS_COMPLETED = "3";
    // 退货失败
    public static final String RETURN_STATUS_FAILURE = "4";
    // 未退款
    public static final String REFUND_STATUS_NO_REFUND = "0";
    // 退款中
    public static final String REFUND_STATUS_REFUND = "1";
    // 已退款
    public static final String REFUND_STATUS_COMPLETED = "2";
    // 退款失败
    public static final String REFUND_STATUS_FAILURE = "3";
    private OrderItem orderItem;	// 所属订单项
    private Product product;        // 所属商品
    private Shop shop;              // 所属店铺
    private Customer customer;      // 所属买家
    private Express express;        // 退货快递所属物流公司
    private String shippingNumber;  // 退货运单号
    private String phone;           // 退款联系电话
    private Integer quantity;       // 退货数量
    private BigDecimal amount;      // 退款金额
    private String returnReason;    // 退款原因
    private String returnStatus;    // 退货状态
    private String refundStatus;    // 退款状态
    private Date applyTime;         // 退款申请时间
    private Date finishTime;        // 退款完成时间
    private String note;            // 备注
    private List<RefundLog> logList;// 状态操作日志说明
    private Payment payment;        // 该退款单对应的支付单
    private String hasFreightPrice; // 退款金额是否包含快递费
    private Integer isActivate;     // 是否撤销
    private RefundInterface refundInterface;    // 退款接口数据
    private Integer isReceipt;      // 是否收到货物 （0：未收到货 1：已收到货） 已发货退款单使用
    private User operBy;            // 操作人
    private Date operDate;          // 操作时间

    /**
     * 返回退款单是否已结束
     * @return
     */
    public boolean getIsFinish(){
        if((isActivate != null && isActivate == 0)
                || (RETURN_STATUS_FAILURE.equals(returnStatus) && REFUND_STATUS_NO_REFUND.equals(refundStatus))
                || (RETURN_STATUS_COMPLETED.equals(returnStatus) && REFUND_STATUS_FAILURE.equals(refundStatus))
                || (RETURN_STATUS_COMPLETED.equals(returnStatus) && REFUND_STATUS_COMPLETED.equals(refundStatus))){
            return true;
        }
        return false;
    }

    public boolean getCanVerify(){
        return RETURN_STATUS_COMPLETED.equals(returnStatus) && REFUND_STATUS_NO_REFUND.equals(refundStatus);
    }

    public boolean getCanConfirm(){
        return RETURN_STATUS_COMPLETED.equals(returnStatus) && REFUND_STATUS_REFUND.equals(refundStatus);
    }

    public boolean getCanExecution(){
        return RETURN_STATUS_COMPLETED.equals(returnStatus) && REFUND_STATUS_REFUND.equals(refundStatus);
    }

    /**
     * 该订单是否可点击确认收到退货按钮
     * @return
     */
    public boolean getCanConfirmReturn(){
        return REFUND_STATUS_NO_REFUND.equals(refundStatus)
                && RETURN_STATUS_RETURNS_IN.equals(returnStatus)
                && null != isActivate
                && isActivate == 1
                && null != isReceipt
                && isReceipt == 1;
    }

    public String getRefundInterfaceLabel(){
        if(null == refundInterface){
            return "未执行";
        }else if(!refundInterface.getRequestSuccess()){
            return "请求退款失败";
        }else if(refundInterface.getRequestSuccess() && null == refundInterface.getResult()){
            return "等待退款结果";
        }else if(refundInterface.getResult()){
            return "执行退款成功";
        }else if(!refundInterface.getResult()){
            return "执行退款失败";
        }else{
            return "未知状态";
        }
    }

    public String getRefundInterfaceColor(){
        if(null == refundInterface){
            return StatusColor.CORNFLOWER_BLUE;
        }else if(!refundInterface.getRequestSuccess()){
            return StatusColor.RED;
        }else if(refundInterface.getRequestSuccess() && null == refundInterface.getResult()){
            return StatusColor.TOMATO;
        }else if(refundInterface.getResult()){
            return StatusColor.GREEN;
        }else if(!refundInterface.getResult()){
            return StatusColor.RED;
        }else{
            return StatusColor.BLACK;
        }
    }

    public String getRefundStatusLabel(){
        if(REFUND_STATUS_NO_REFUND.equals(refundStatus)){
            return "待审核";
        }else if(REFUND_STATUS_REFUND.equals(refundStatus)){
            return "退款中";
        }else if(REFUND_STATUS_COMPLETED.equals(refundStatus)){
            return "退款完成";
        }else if(REFUND_STATUS_FAILURE.equals(refundStatus)){
            return "退款失败";
        }else{
            return "未知状态";
        }
    }

    public String getRefundStatusColor(){
        if(REFUND_STATUS_NO_REFUND.equals(refundStatus)){
            return StatusColor.CORNFLOWER_BLUE;
        }else if(REFUND_STATUS_REFUND.equals(refundStatus)){
            return StatusColor.TOMATO;
        }else if(REFUND_STATUS_COMPLETED.equals(refundStatus)) {
            return StatusColor.GREEN;
        }else if(REFUND_STATUS_FAILURE.equals(refundStatus)){
            return StatusColor.RED;
        }else{
            return StatusColor.BLACK;
        }
    }

    public String getReturnStatusLabel(){
        if(RETURN_STATUS_UNTREATED.equals(returnStatus)){
            return "待审核";
        }else if(RETURN_STATUS_CHECK_SUCC.equals(returnStatus)){
            return "退货审核成功";
        }else if(RETURN_STATUS_RETURNS_IN.equals(returnStatus)){
            return "退货中";
        }else if(RETURN_STATUS_COMPLETED.equals(returnStatus)){
            return "退货完成";
        }else if(RETURN_STATUS_FAILURE.equals(returnStatus)){
            return "退货失败";
        }else{
            return "未知状态";
        }
    }

    public String getReturnStatusColor(){
        if(RETURN_STATUS_UNTREATED.equals(returnStatus)){
            return StatusColor.CORNFLOWER_BLUE;
        }else if(RETURN_STATUS_CHECK_SUCC.equals(returnStatus)){
            return StatusColor.GREEN;
        }else if(RETURN_STATUS_RETURNS_IN.equals(returnStatus)){
            return StatusColor.TOMATO;
        }else if(RETURN_STATUS_COMPLETED.equals(returnStatus)){
            return StatusColor.GREEN;
        }else if(RETURN_STATUS_FAILURE.equals(returnStatus)){
            return StatusColor.RED;
        }else{
            return StatusColor.BLACK;
        }
    }

    public Express getExpress() {
        return express;
    }

    public void setExpress(Express express) {
        this.express = express;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getShippingNumber() {
        return shippingNumber;
    }

    public void setShippingNumber(String shippingNumber) {
        this.shippingNumber = shippingNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<RefundLog> getLogList() {
        return logList;
    }

    public void setLogList(List<RefundLog> logList) {
        this.logList = logList;
    }

    public String getRETURN_STATUS_UNTREATED() {
        return RETURN_STATUS_UNTREATED;
    }

    public String getRETURN_STATUS_RETURNS_IN() {
        return RETURN_STATUS_RETURNS_IN;
    }

    public String getRETURN_STATUS_COMPLETED() {
        return RETURN_STATUS_COMPLETED;
    }

    public String getRETURN_STATUS_FAILURE() {
        return RETURN_STATUS_FAILURE;
    }

    public String getREFUND_STATUS_NO_REFUND() {
        return REFUND_STATUS_NO_REFUND;
    }

    public String getREFUND_STATUS_REFUND() {
        return REFUND_STATUS_REFUND;
    }

    public String getREFUND_STATUS_COMPLETED() {
        return REFUND_STATUS_COMPLETED;
    }

    public String getREFUND_STATUS_FAILURE() {
        return REFUND_STATUS_FAILURE;
    }

	public Integer getQuantity() {
		return quantity;
	}

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

	public String getHasFreightPrice() {
		return hasFreightPrice;
	}

	public void setHasFreightPrice(String hasFreightPrice) {
		this.hasFreightPrice = hasFreightPrice;
	}

    public Integer getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(Integer isActivate) {
        this.isActivate = isActivate;
    }

    public RefundInterface getRefundInterface() {
        return refundInterface;
    }

    public void setRefundInterface(RefundInterface refundInterface) {
        this.refundInterface = refundInterface;
    }

    public Integer getIsReceipt() {
        return isReceipt;
    }

    public void setIsReceipt(Integer isReceipt) {
        this.isReceipt = isReceipt;
    }

    public User getOperBy() {
        return operBy;
    }

    public void setOperBy(User operBy) {
        this.operBy = operBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }
}
