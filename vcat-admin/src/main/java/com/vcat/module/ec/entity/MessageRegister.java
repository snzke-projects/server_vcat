package com.vcat.module.ec.entity;

import com.vcat.common.utils.DateUtils;
import com.vcat.module.content.entity.Article;
import com.vcat.module.core.entity.DataEntity;

/**
 * 注册消息
 */
public class MessageRegister extends DataEntity<MessageRegister> {
    private static final long serialVersionUID = 1L;
    private Article article;    // 消息对应文章
    private Integer interval;   // 发送消息间隔
    private Integer isActivate; // 是否激活

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getIntervalShow() {
        return DateUtils.getIntervalLabel(interval);
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(Integer isActivate) {
        this.isActivate = isActivate;
    }
}
