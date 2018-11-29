package com.vcat.module.json.product;

import com.vcat.module.core.entity.ResponseEntity;
import com.vcat.module.ec.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class GetSellerProductDetailResponse extends ResponseEntity {
    private Product productInfo;                        //商品信息
    private String shopName;                            //店铺名
    //private Integer reviewCount;                        //评论个数
    //private Integer cartCount;                          //购物车个数
    //private List<Map<String, Object>> itemTitleList;    //商品标题列表
    //private Map<String, Object> itemList;               //商品规格
    //private String isCollect;                           //是否收藏
    //private String couponType;                          //普通,拿样,猫币
    //private BigDecimal freightPrice;                    //商品的邮费

    //public Integer getCartCount() {
    //    return cartCount;
    //}
    //
    //public void setCartCount(Integer cartCount) {
    //    this.cartCount = cartCount;
    //}
    //
    //public String getCouponType() {
    //    return couponType;
    //}
    //
    //public void setCouponType(String couponType) {
    //    this.couponType = couponType;
    //}
    //
    //public BigDecimal getFreightPrice() {
    //    return freightPrice;
    //}
    //
    //public void setFreightPrice(BigDecimal freightPrice) {
    //    this.freightPrice = freightPrice;
    //}
    //
    //public String getIsCollect() {
    //    return isCollect;
    //}
    //
    //public void setIsCollect(String isCollect) {
    //    this.isCollect = isCollect;
    //}
    //
    //public Map<String, Object> getItemList() {
    //    return itemList;
    //}
    //
    //public void setItemList(Map<String, Object> itemList) {
    //    this.itemList = itemList;
    //}
    //
    //public List<Map<String, Object>> getItemTitleList() {
    //    return itemTitleList;
    //}
    //
    //public void setItemTitleList(List<Map<String, Object>> itemTitleList) {
    //    this.itemTitleList = itemTitleList;
    //}
    //
    //
    //
    //public Integer getReviewCount() {
    //    return reviewCount;
    //}
    //
    //public void setReviewCount(Integer reviewCount) {
    //    this.reviewCount = reviewCount;
    //}

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public Product getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(Product productInfo) {
        this.productInfo = productInfo;
    }
}
