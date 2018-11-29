package com.vcat.module.json.product;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.module.core.entity.ResponseEntity;
import com.vcat.module.ec.entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Code.Ai on 16/5/10.
 * Description:
 */
public class GetGroupBuyProductDetailResponse extends ResponseEntity {
    private Map<String, Object> productInfo = new HashMap<>();
    public Map<String, Object> getProductInfo() {
        return productInfo;
    }
    public void setProductInfo(Shop shop,
                               Shop customerShop,
                               Product product,             // 团购商品基本信息
                               String isCollect,            // 收藏数量
                               int reviewCount,             // 评论数量
                               BigDecimal freightPrice,     // 运费
                               List<Map<String, Object>> itemTitleList, // 商品规格标题
                               Map<String, Object> itemInfos,            // 规格详情
                               int cartCount,               // 购物车数量
                               int type,                    // 卖家查看/买家查看
                               int joinedCount,             // 需要参团的人数 - 1
                               int groupBuyStatus,
                               String groupBuySponsorId) {  // 买家参团查看商品详情

        // 重构价格 卖家根据是否为VIP区分价格;买家价格为原来的价格
        Map groupBuyItemInfo = (Map) itemInfos.get("groupBuyItemInfo");
        Map normalItemInfo   = (Map) itemInfos.get("normalItemInfo");
        BigDecimal earning = BigDecimal.ZERO;
        if(type == ApiConstants.SELLER_LAUNCH && shop.getAdvancedShop() == 1) { // 卖家查看且卖家是VIP
            this.productInfo.put("earning", groupBuyItemInfo.get("earning"));
            this.productInfo.put("type", 8);
            this.productInfo.put("isVip",true);
        }else if(type == ApiConstants.SELLER_LAUNCH && shop.getAdvancedShop() != 1){
            this.productInfo.put("type", 8);
            this.productInfo.put("earning", groupBuyItemInfo.get("earning"));
            this.productInfo.put("isVip",false);
        }else if(type == ApiConstants.BUYER_LAUNCH){        // 买家开团/参团显示的团购价位原来的团购价
            this.productInfo.put("type", 9);
            this.productInfo.put("shopName", customerShop.getName());               // 店铺名
            this.productInfo.put("groupBuySponsorId", groupBuySponsorId);
            this.productInfo.put("earning", earning);
        }

        this.productInfo.put("groupBuyStatus", groupBuyStatus);
        // 团购价
        this.productInfo.put("groupBuyItemInfo", itemInfos.get("groupBuyItemInfo") );
        // 普通价
        this.productInfo.put("normalItemInfo",  itemInfos.get("normalItemInfo") );
        // 获取团购规格信息 添加到根节点
        this.productInfo.put("id", product.getId());    // 团购商品ID
        //this.productInfo.put("copywrite", product.getCopywrite());
        this.productInfo.put("description", product.getDescription()); // 代理说明/品牌故事
        this.productInfo.put("canRefund", product.getCanRefund());     // 能否退款
        //this.productInfo.put("inventory", itemInfo.get("inventory"));
        this.productInfo.put("productCollectCount", product.getProductCollectCount());  // 收藏数量
        this.productInfo.put("reviewCount", reviewCount);               // 评论数量
        this.productInfo.put("title",product.getTitle());
        this.productInfo.put("vcatGroupBuyPrice", groupBuyItemInfo.get("vcatGroupBuyPrice"));   // 原来拼团价
        this.productInfo.put("shopGroupBuyPrice", groupBuyItemInfo.get("shopGroupBuyPrice"));   // 店主拼团价(也显示原来的价格)
        this.productInfo.put("singlePrice", normalItemInfo.get("vcatNormalPrice"));             // 原始单人价
        this.productInfo.put("headCount", groupBuyItemInfo.get("headCount"));       // 几人团
        this.productInfo.put("limitCount", groupBuyItemInfo.get("limitCount"));
        this.productInfo.put("joinedCount", joinedCount);
        this.productInfo.put("category", product.getCategory());
        this.productInfo.put("isDownLoad", product.getIsDownLoad());
        this.productInfo.put("isCollect", isCollect);
        this.productInfo.put("saledNum", product.getSaledNum());
        this.productInfo.put("freightPrice", freightPrice);
        this.productInfo.put("supplier", product.getBrand());
        this.productInfo.put("isSellerLoad", product.getIsSellerLoad());
        this.productInfo.put("itemTitleList", itemTitleList);
        this.productInfo.put("imageList", product.getImageList());
        this.productInfo.put("propertyList", product.getPropertyList());
        this.productInfo.put("cartCount", cartCount);
        this.productInfo.put("name", product.getName());
    }
}
