package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;


public class ShopInfo extends DataEntity<ShopInfo> {
    private static final long serialVersionUID = 5592897807601287927L;
    private String  realName;
    private String  farmName;
    private Shop    shop;
    private Product product;
    private String  QRCode;
    private Integer isDefault;
    private Integer isActivate;
    private String wechatName;

    public String getWechatName() {
        return wechatName;
    }

    public void setWechatName(String wechatName) {
        this.wechatName = wechatName;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Boolean getIsActivate() {
        return isActivate == 1;
    }

    public void setIsActivate(Integer isActivate) {
        this.isActivate = isActivate;
    }
}
