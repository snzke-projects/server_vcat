package com.vcat.module.ec.entity;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;

/**
 * 退款单日志
 */
public class RefundLog extends DataEntity<RefundLog> {
    private static final long serialVersionUID = 1L;
    private Refund refund;          // 所属退款单
    private String returnStatus;    // 退货状态
    private String refundStatus;    // 退款状态
    private String note;            // 备注
    private String statusNote;      // 状态说明
    private String orderItemId;

    public static RefundLog create(Refund refund){
        if(null == refund){
            return null;
        }
        RefundLog log = new RefundLog();
        log.preInsert();
        log.setRefund(refund);
        log.setStatusNote(createStatusNote(refund.getReturnStatus(),refund.getRefundStatus()));
        return log;
    }

    private static String createStatusNote(String returnStatus,String refundStatus){
        if(StringUtils.isEmpty(returnStatus) || StringUtils.isEmpty(refundStatus)){
            return "";
        }
        String note = "";
        if(Refund.RETURN_STATUS_CHECK_SUCC.equals(returnStatus) && Refund.REFUND_STATUS_NO_REFUND.equals(refundStatus)){
            return "退货审核成功，可以退货";
        }else if(Refund.RETURN_STATUS_COMPLETED.equals(returnStatus) && Refund.REFUND_STATUS_NO_REFUND.equals(refundStatus)){
            return "已成功收到退货物品，正在退款审核中...";
        }else if(Refund.RETURN_STATUS_FAILURE.equals(returnStatus) && Refund.REFUND_STATUS_NO_REFUND.equals(refundStatus)){
            return "退货失败";
        }else if(Refund.RETURN_STATUS_COMPLETED.equals(returnStatus) && Refund.REFUND_STATUS_REFUND.equals(refundStatus)){
            return "退款审核成功，正在退款...";
        }else if(Refund.RETURN_STATUS_COMPLETED.equals(returnStatus) && Refund.REFUND_STATUS_COMPLETED.equals(refundStatus)){
            return "退款完成，请及时查收您的付款账户";
        }else if(Refund.RETURN_STATUS_COMPLETED.equals(returnStatus) && Refund.REFUND_STATUS_FAILURE.equals(refundStatus)){
            return "退款失败";
        }
        return note;
    }

    public String getRefundStatusLabel(){
        if(Refund.REFUND_STATUS_NO_REFUND.equals(refundStatus)){
            return "未退款";
        }else if(Refund.REFUND_STATUS_REFUND.equals(refundStatus)){
            return "退款中";
        }else if(Refund.REFUND_STATUS_COMPLETED.equals(refundStatus)){
            return "退款完成";
        }else if(Refund.REFUND_STATUS_FAILURE.equals(refundStatus)){
            return "退款失败";
        }else{
            return "未知状态";
        }
    }

    public String getRefundStatusColor(){
        if("0".equals(refundStatus)){
            return "cornflowerblue";
        }else if("1".equals(refundStatus)){
            return "tomato";
        }else if("2".equals(refundStatus)) {
            return "green";
        }else if("3".equals(refundStatus)){
            return "red";
        }else{
            return "black";
        }
    }

    public String getReturnStatusLabel(){
        if(Refund.RETURN_STATUS_UNTREATED.equals(returnStatus)){
            return "未退货";
        }else if(Refund.RETURN_STATUS_CHECK_SUCC.equals(returnStatus)){
            return "退货审核成功";
        }else if(Refund.RETURN_STATUS_RETURNS_IN.equals(returnStatus)){
            return "退货中";
        }else if(Refund.RETURN_STATUS_COMPLETED.equals(returnStatus)){
            return "退货完成";
        }else if(Refund.RETURN_STATUS_FAILURE.equals(returnStatus)){
            return "退货失败";
        }else{
            return "未知状态";
        }
    }

    public String getReturnStatusColor(){
        if("0".equals(returnStatus)){
            return "cornflowerblue";
        }else if("1".equals(returnStatus)){
            return "tomato";
        }else if("2".equals(returnStatus)){
            return "tomato";
        }else if("3".equals(returnStatus)){
            return "green";
        }else if("4".equals(returnStatus)){
            return "red";
        }else{
            return "black";
        }
    }

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatusNote() {
        return statusNote;
    }

    public void setStatusNote(String statusNote) {
        this.statusNote = statusNote;
    }

    public String getRETURN_STATUS_UNTREATED() {
        return Refund.RETURN_STATUS_UNTREATED;
    }

    public String getRETURN_STATUS_RETURNS_IN() {
        return Refund.RETURN_STATUS_RETURNS_IN;
    }

    public String getRETURN_STATUS_COMPLETED() {
        return Refund.RETURN_STATUS_COMPLETED;
    }

    public String getRETURN_STATUS_FAILURE() {
        return Refund.RETURN_STATUS_FAILURE;
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

	public String getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}
}
