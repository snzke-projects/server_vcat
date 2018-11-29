package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

import java.math.BigDecimal;
import java.util.Date;

public class WithdrawalExcel extends DataEntity<WithdrawalExcel> {
	private String shopName;
    private BigDecimal amount;
    private Date date;
    private String type;
    private String accountNumber;
    private String accountName;
    private String accountBankName;
    private Integer status;
    private String statusLabel;

    @ExcelField(title="小店昵称", align=2, sort=10)
    public String getShopName() {
        return shopName;
    }

    @ExcelField(title="金额", align=1, sort=20)
    public BigDecimal getAmount() {
        return amount;
    }

    @ExcelField(title="申请时间", align=2, sort=30, format="yyyy-mm-dd hh:ss:mm")
    public Date getDate() {
        return date;
    }

    @ExcelField(title="账户类型", align=2, sort=40)
    public String getType() {
        return type;
    }

    @ExcelField(title="账户号码", align=2, sort=50)
    public String getAccountNumber() {
        return accountNumber;
    }

    @ExcelField(title="账户名称", align=2, sort=60)
    public String getAccountName() {
        return accountName;
    }

    @ExcelField(title="银行名称", align=2, sort=70)
    public String getAccountBankName() {
        return accountBankName;
    }

    public Integer getStatus() {
        return status;
    }

    @ExcelField(title="状态", align=2, sort=80)
    public String getStatusLabel() {
        if(Withdrawal.WITHDRAWAL_STATUS_UNTREATED.equals(status)){
            return "未处理";
        }else if(Withdrawal.WITHDRAWAL_STATUS_SUCCESS.equals(status)){
            return "提现完成";
        }else if(Withdrawal.WITHDRAWAL_STATUS_FAILURE.equals(status)){
            return "提现失败";
        }else{
            return "未知状态";
        }
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountBankName(String accountBankName) {
        this.accountBankName = accountBankName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }
}
