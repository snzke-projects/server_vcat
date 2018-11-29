package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 文案配图
 */
public class CopywriteImage extends DataEntity<CopywriteImage> {
	private static final long serialVersionUID = 1L;
    private Copywrite copywrite;    // 文案
    private Shop shop;              // 所属店铺
	private String imageUrl;        // 图片地址
	private Integer displayOrder;   // 图片排序

    public Copywrite getCopywrite() {
        return copywrite;
    }

    public void setCopywrite(Copywrite copywrite) {
        this.copywrite = copywrite;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
