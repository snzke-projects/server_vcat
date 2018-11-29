package com.vcat.module.ec.dto;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Created by Code.Ai on 16/4/7.
 * Description:
 */
public class ShopInfoDto implements Serializable {
    private String id;
    private String realName;
    private String farmName;
    private String QRCode;
    private String shopId;
    private String productId;
    private Integer isDefault;
    private Integer isActivate;
    private String wechatName;

    public String getWechatName() {
        return wechatName;
    }

    public void setWechatName(String wechatName) {
        this.wechatName = wechatName;
    }

    public ShopInfoDto() {
    }

    public ShopInfoDto(String shopId) {
        this.shopId = shopId;
    }

    public ShopInfoDto(String shopId, String productId) {
        this.shopId = shopId;
        this.productId = productId;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getQRCode() {
        if (QRCode == null) {
            return QRCode;
        }
        if(QRCode.contains("http")){
            String[] url = QRCode.split("/");
            return url[5] ;
        }else
            return QCloudUtils.createThumbDownloadUrl(QRCode, 400);
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

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public Integer getIsActivate() {
        return isActivate ;
    }

    public void setIsActivate(Integer isActivate) {
        this.isActivate = isActivate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.JSON_STYLE);
    }
}
