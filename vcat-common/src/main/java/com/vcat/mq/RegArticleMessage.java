package com.vcat.mq;

import java.io.Serializable;

/**
 * Created by ylin on 2016/5/20.
 */
public class RegArticleMessage implements Serializable {
    private String articleId;
    private String customerId;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "RegArticleMessage{" +
                "articleId='" + articleId + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}
