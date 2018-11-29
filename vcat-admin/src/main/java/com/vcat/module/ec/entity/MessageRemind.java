package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

/**
 * 提醒消息
 */
public class MessageRemind extends DataEntity<MessageRemind> {
    private static final long serialVersionUID = 1L;
    private Shop shop;          // 消息所属小店
    private String title;       // 消息标题
    private String content;     // 消息内容
    private boolean isRead;     // 是否已读
    private Date readDate;      // 阅读时间

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }
}
