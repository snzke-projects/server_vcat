package com.alipay.entity;

import com.google.common.collect.Lists;
import com.vcat.common.utils.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 支付宝回调数据
 */
public class AlipayRefundResponseData implements Serializable{
    private Date notify_time;       // 回调时间
    private String notify_type;     // 回调类型
    private String notify_id;       // 回调ID
    private String sign_type;       // 签名类型
    private String sign;            // 签名
    private String batch_no;        // 退款批次号
    private String success_num;     // 退款成功数量
    private String result_details;  // 退款结果明细

    public List<RefundDetail> getResultList(){
        List<RefundDetail> list = Lists.newArrayList();
        if(StringUtils.isNotBlank(result_details)){
            String[] resultArray = result_details.split("#");
            for (int i = 0; i < resultArray.length; i++) {
                String result = resultArray[i];
                RefundDetail refundDetail = new RefundDetail();
                if(result.indexOf("$") > 0){   // 判断是否包含退手续费
                    String[] detailArray = result.split("\\$");
                    String[] refundDetailArray = detailArray[0].split("\\^");
                    String[] feeArray = detailArray[1].split("\\^");
                    pushRefundDetail(refundDetail,refundDetailArray);
                    pushRefundFeeDetail(refundDetail, feeArray);
                }else{
                    pushRefundDetail(refundDetail,result.split("\\^"));
                }
                list.add(refundDetail);
            }
        }
        return list;
    }

    /**
     * 注入支付退款回调退手续费信息
     * @param refundDetail
     * @param feeArray
     */
    private void pushRefundFeeDetail(RefundDetail refundDetail, String[] feeArray) {
        if(null != feeArray && feeArray.length == 4){
            refundDetail.setFeeAccountName(feeArray[0]);
            refundDetail.setFeeAccountId(feeArray[1]);
            refundDetail.setFee(new BigDecimal(feeArray[2]));
            refundDetail.setFeeResult(feeArray[3]);
        }
    }

    /**
     * 注入支付宝退款回调退款信息
     * @param refundDetail
     * @param refundDetailArray
     * @return
     */
    private RefundDetail pushRefundDetail(RefundDetail refundDetail,String[] refundDetailArray){
        if (null != refundDetailArray && refundDetailArray.length == 3) {
            refundDetail.setTransactionNo(refundDetailArray[0]);
            refundDetail.setAmount(new BigDecimal(refundDetailArray[1]));
            refundDetail.setResult(refundDetailArray[2]);
        }
        return refundDetail;
    }

    public Date getNotify_time() {
        return notify_time;
    }

    public void setNotify_time(Date notify_time) {
        this.notify_time = notify_time;
    }

    public String getNotify_type() {
        return notify_type;
    }

    public void setNotify_type(String notify_type) {
        this.notify_type = notify_type;
    }

    public String getNotify_id() {
        return notify_id;
    }

    public void setNotify_id(String notify_id) {
        this.notify_id = notify_id;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getSuccess_num() {
        return success_num;
    }

    public void setSuccess_num(String success_num) {
        this.success_num = success_num;
    }

    public String getResult_details() {
        return result_details;
    }

    public void setResult_details(String result_details) {
        this.result_details = result_details;
    }

    public class RefundDetail {
        private String transactionNo;       // 支付交易号
        private BigDecimal amount;          // 退款金额
        private String result;              // 退款结果
        private String feeAccountName;      // 退手续费账号
        private String feeAccountId;        // 退手续费账户ID
        private BigDecimal fee;             // 退手续费
        private String feeResult;           // 退手续费结果

        public String getTransactionNo() {
            return transactionNo;
        }

        public void setTransactionNo(String transactionNo) {
            this.transactionNo = transactionNo;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getFeeAccountName() {
            return feeAccountName;
        }

        public void setFeeAccountName(String feeAccountName) {
            this.feeAccountName = feeAccountName;
        }

        public String getFeeAccountId() {
            return feeAccountId;
        }

        public void setFeeAccountId(String feeAccountId) {
            this.feeAccountId = feeAccountId;
        }

        public BigDecimal getFee() {
            return fee;
        }

        public void setFee(BigDecimal fee) {
            this.fee = fee;
        }

        public String getFeeResult() {
            return feeResult;
        }

        public void setFeeResult(String feeResult) {
            this.feeResult = feeResult;
        }
    }
}
