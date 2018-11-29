package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by Code.Ai on 16/4/11.
 * Description:
 */
public class Copywrite extends DataEntity<Copywrite> {
    private static final long serialVersionUID = -454773132927237761L;
    private Product product;            // 商品
    private String title;               // 文案标题
    private String content;             // 文案内容
    private Integer isActivate;         // 是否激活
    private Date activateDate;          // 激活时间
    private Date createDate;            // 创建时间

    public Date getActivateDate() {
        return activateDate;
    }

    public void setActivateDate(Date activateDate) {
        this.activateDate = activateDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }

    @Override
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(Integer isActivate) {
        this.isActivate = isActivate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
