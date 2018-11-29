package com.vcat.module.ec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;

import java.util.Date;
import java.util.List;

/**
 * 文案
 */
public class Copywrite extends DataEntity<Copywrite> {
    private Product product;    // 所属商品
    private String title;       // 标题
    private String content;     // 内容
    private Boolean isActivate; // 是否激活
    private Date activateTime;  // 激活时间
    private List<CopywriteImage> imageList;     // 商品素材图片
    private List<CopywriteImage> shopImageList; // 店铺素材图片

    /**
     * 获取商品素材图片
     * @return
     */
    public String getProductImagesPath() {
        if(null == imageList || imageList.isEmpty()){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < imageList.size(); i++) {
            sb.append("|");
            sb.append(QCloudUtils.createOriginalDownloadUrl(imageList.get(i).getImageUrl()));
        }

        if(sb.length() > 0){
            sb.delete(0,1);
        }
        return sb.toString();
    }

    /**
     * 获取店铺素材图片
     * @return
     */
    public String getShopImagesPath() {
        if(null == shopImageList || shopImageList.isEmpty()){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < shopImageList.size(); i++) {
            sb.append("|");
            sb.append(QCloudUtils.createOriginalDownloadUrl(shopImageList.get(i).getImageUrl()));
            if(null != shopImageList.get(i).getShop()){
                sb.append("?shopId=" + shopImageList.get(i).getShop().getId());
                sb.append("&shopName=" + shopImageList.get(i).getShop().getName());
                sb.append("&phoneNumber=" + shopImageList.get(i).getShop().getCustomer().getPhoneNumber());
            }
        }

        if(sb.length() > 0){
            sb.delete(0,1);
        }
        return sb.toString();
    }


    public Copywrite() {
    }

    public Copywrite(String id) {
        super(id);
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(Boolean activate) {
        isActivate = activate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getActivateTime() {
        return activateTime;
    }

    public void setActivateTime(Date activateTime) {
        this.activateTime = activateTime;
    }

    public List<CopywriteImage> getShopImageList() {
        return shopImageList;
    }

    public void setShopImageList(List<CopywriteImage> shopImageList) {
        this.shopImageList = shopImageList;
    }

    public List<CopywriteImage> getImageList() {
        return imageList;
    }

    public void setImageList(List<CopywriteImage> imageList) {
        this.imageList = imageList;
    }
}
