package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

/**
 * 活动
 */
public class Participation extends DataEntity<Participation> {
	private static final long serialVersionUID = 1L;
    private String title;
    private String name;
    private String realName;
    private String phone;
    private String qq;
    private String email;
    private String date;
    private String deliveryName;    // 收货人
    private String deliveryPhone;   // 收货电话
    private String detailAddress;   // 详细地址
    private Boolean hasReport;      // 是否参与活动问卷调查
    private String customerId;      // 参与活动人员ID
    private String questionnaireTitle;  //问卷调查标题

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ExcelField(title="用户名", align=2, sort=10)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ExcelField(title="真实姓名", align=2, sort=20)
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @ExcelField(title="手机号", align=2, sort=30)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ExcelField(title="QQ", align=2, sort=40)
    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    @ExcelField(title="邮箱", align=2, sort=50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ExcelField(title="参与时间", align=2, sort=60)
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @ExcelField(title="收货人名称", align=2, sort=70)
    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    @ExcelField(title="收货人电话", align=2, sort=80)
    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    @ExcelField(title="收货地址", align=1, sort=90)
    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public Boolean getHasReport() {
        return hasReport;
    }

    public void setHasReport(Boolean hasReport) {
        this.hasReport = hasReport;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getQuestionnaireTitle() {
        return questionnaireTitle;
    }

    public void setQuestionnaireTitle(String questionnaireTitle) {
        this.questionnaireTitle = questionnaireTitle;
    }
}
