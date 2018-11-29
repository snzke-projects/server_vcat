package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.content.entity.Article;
import com.vcat.module.core.entity.DataEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * V猫新动态
 */
public class Message extends DataEntity<Message> {
    public static final int PERSONAL = 1;
    public static final int PUBLIC = 2;
    private static final long serialVersionUID = 1L;
    private Shop shop;          // 消息所属小店
    private boolean isRead;     // 是否已读
    private Date readDate;      // 阅读时间
    private String thumb;       // 缩略图
    private String intro;       // 简介
    private String title;       // 标题
    private Date publicDate;    // 发布时间
    private Article article;    // V猫新动态对应文章
    private Integer type;       // 消息类型(1:个人V猫新动态,2:公用V猫新动态)

    public String getThumbPath(){
        return QCloudUtils.createOriginalDownloadUrl(thumb);
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublicDate() {
        return publicDate;
    }

    public void setPublicDate(Date publicDate) {
        this.publicDate = publicDate;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Integer getType() {
        if(null == type){
            return PERSONAL;
        }
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
