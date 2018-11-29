package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

public class ShopInfo extends DataEntity<ShopInfo> {
	private static final long serialVersionUID = 7309007870868246266L;
    @ExcelField(title="小店名称", align=2, sort=10,value = "shop.name")
    private Shop shop;          // 农场所属店铺
    @ExcelField(title="小店手机号", align=2, sort=15)
    private String shopPhone;   // 小店手机号
    @ExcelField(title="庄园", align=2, sort=20,value = "product.name")
    private Product product;    // 农场商品（例：车厘子园）
    @ExcelField(title="庄园名称", align=2, sort=30)
    private String farmName;    // 庄园名称
    @ExcelField(title="真实名称", align=2, sort=40)
    private String realName;    // 真实姓名
    private String qrCodeUrl;   // 二维码图片链接
    private Boolean isDefault;  // 是否为默认
    @ExcelField(title="基地卡片库存", align=2, sort=43)
    private Integer baseCardInventory;    // 基地卡片库存
    @ExcelField(title="公司卡片库存", align=2, sort=46)
    private Integer companyCardInventory;   // 公司卡片库存
    @ExcelField(title="备注", align=2, sort=50)
    private String note;        // 备注

    @ExcelField(title="微信二维码", align=2, sort=50
//            , imageType=ExcelField.WEB_IMAGE, imageHeight=50
    )
    public String getQrCodeUrlPath(){
        if(StringUtils.isBlank(qrCodeUrl)){
            return "";
        }
        return QCloudUtils.createOriginalDownloadUrl(qrCodeUrl);
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }


    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Integer getBaseCardInventory() {
        return baseCardInventory;
    }

    public void setBaseCardInventory(Integer baseCardInventory) {
        this.baseCardInventory = baseCardInventory;
    }

    public Integer getCompanyCardInventory() {
        return companyCardInventory;
    }

    public void setCompanyCardInventory(Integer companyCardInventory) {
        this.companyCardInventory = companyCardInventory;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
