package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.util.Date;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.entity.Refund;
import com.vcat.module.ec.entity.RefundLog;


public class RefundLogDto implements Serializable {
    private String refundId;          // 所属退款单
    private String returnStatus;    // 退货状态
    private String refundStatus;    // 退款状态
 //   private String note;            // 备注
    private String statusNote;      // 状态说明
    private Date createDate;//操作时间
    private String AllStatus;
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
        if(Refund.RETURN_STATUS_COMPLETED.equals(returnStatus) && Refund.REFUND_STATUS_NO_REFUND.equals(refundStatus)){
            return "已成功收到退货物品，正在退款审核中...";
        }else if(Refund.RETURN_STATUS_FAILURE.equals(returnStatus) && Refund.REFUND_STATUS_NO_REFUND.equals(refundStatus)){
            return "退货审核失败";
        }else if(Refund.REFUND_STATUS_COMPLETED.equals(returnStatus) && Refund.REFUND_STATUS_REFUND.equals(refundStatus)){
            return "退款审核成功，正在退款...";
        }else if(Refund.REFUND_STATUS_COMPLETED.equals(returnStatus) && Refund.REFUND_STATUS_COMPLETED.equals(refundStatus)){
            return "退款完成，请及时查收您的付款账户";
        }else if(Refund.REFUND_STATUS_COMPLETED.equals(returnStatus) && Refund.REFUND_STATUS_FAILURE.equals(refundStatus)){
            return "退款失败";
        }
        return note;
    }
	public String getRefundId() {
		return refundId;
	}
	public void setRefundId(String refundId) {
		this.refundId = refundId;
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
	public String getStatusNote() {
		return statusNote;
	}
	public void setStatusNote(String statusNote) {
		this.statusNote = statusNote;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getAllStatus() {
		return AllStatus;
	}
	public void setAllStatus(String allStatus) {
		AllStatus = allStatus;
	}
}
