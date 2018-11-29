package com.vcat.module.json.product;

import com.vcat.module.core.entity.RequestEntity;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotNull;


public class AddLeroyerInfoRequest extends RequestEntity {
    @NotNull
    private String               realName;            //庄园主真是姓名
    @NotNull
    private String               farmName;            //庄园名
    @NotNull
    private String               productId;           //庄园商品ID
    @NotNull
    private MultipartFile QRcode;              //二维码

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public MultipartFile getQRcode() {
        return QRcode;
    }

    public void setQRcode(MultipartFile QRcode) {
        this.QRcode = QRcode;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
