package com.vcat.module.ec.dto;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.utils.StringUtils;

import java.io.Serializable;

/**
 *
 selfJoined 表示自己是否参加
 inProgress 表示活动是否进行中
 full 表示是否报名已满
 * Created by ylin on 2015/12/3.
 */
public class OfflineActivityDto implements Serializable {
    protected String id;
    protected String title;
    protected String imgUrl;
    protected String intro;
    protected String activateTime;
    protected boolean isFull;
    protected boolean isSelfJoined;
    protected boolean isInProgress;
    protected String templateUrl;

    public String getTemplateUrl() {
        return ApiConstants.VCAT_DOMAIN+
                (StringUtils.isNullEmpty(templateUrl) ? "/buyer/views/activities.html" : templateUrl);
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setIsFull(boolean isFull) {
        this.isFull = isFull;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return QCloudUtils.createOriginalDownloadUrl(imgUrl);
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getActivateTime() {
        return activateTime;
    }

    public void setActivateTime(String activateTime) {
        this.activateTime = activateTime;
    }

    public boolean isSelfJoined() {
        return isSelfJoined;
    }

    public void setIsSelfJoined(boolean isSelfJoined) {
        this.isSelfJoined = isSelfJoined;
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setIsInProgress(boolean isInProgress) {
        this.isInProgress = isInProgress;
    }
}
