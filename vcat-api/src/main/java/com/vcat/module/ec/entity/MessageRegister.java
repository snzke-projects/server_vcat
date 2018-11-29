package com.vcat.module.ec.entity;

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
        String yearShow = "";
        int year = 525600;
        int temp = interval;
        if(temp >= year){  // 年
            yearShow += (temp / year) + "年";
            temp = temp % year;
        }

        String dayShow = "";
        int day = 1440;
        if(temp >= day){ // 天
            dayShow += (temp / day) + "天";
            temp = temp % day;
        }

        String hourShow = "";
        int hour = 60;
        if(temp >= hour){      // 小时
            hourShow += (temp / hour) + "小时";
            temp = temp % hour;
        }

        String minShow = "";
        if(temp > 0){
            minShow += temp + "分钟";
        }

        return yearShow + dayShow + hourShow + minShow;
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
