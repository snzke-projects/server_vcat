package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * 订阅
 */
public class Remind extends DataEntity<Remind> {
    private static final long serialVersionUID = 1L;
    public static final String DICT_LIMIT_TIME_TYPE = "ec_push_share_time_limit";
    public static final String REMIND_TYPE_SHARE = "1";
    private String customerId;  // 订阅者ID
    private String type;        // 订阅类型
    private String relationId;  // 关联ID
    private String pushId;      // 信鸽推送返回ID
    private Date pushDate;      // 推送时间
    private String isActivate;  // 是否激活
    private String title;       // 推送标题
    private String content;     // 推送内容

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public Date getPushDate() {
        return pushDate;
    }

    public void setPushDate(Date pushDate) {
        this.pushDate = pushDate;
    }

    public String getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(String isActivate) {
        this.isActivate = isActivate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public static String getRemindTypeShare() {
        return REMIND_TYPE_SHARE;
    }
}
