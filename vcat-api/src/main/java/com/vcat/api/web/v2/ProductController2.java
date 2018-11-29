package com.vcat.api.web.v2;

import com.vcat.api.service.*;
import com.vcat.api.web.validation.ValidateParams;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.*;
import com.vcat.module.ec.entity.*;
import com.vcat.module.json.product.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.security.krb5.internal.APOptions;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

@RestController
@ApiVersion(2)
public class ProductController2 extends RestBaseController {
    @Autowired
    private ProductService       productService;
    @Autowired
    private ShopService          shopService;
    @Autowired
    private ServerConfigService  cfgService;
    @Autowired
    private CustomerService      customerService;
    @Autowired
    private CopywriteService     copywriteService;
    @Autowired
    private FreightConfigService freightConfigService;
    @Autowired
    private RatingSummaryService ratingSummaryService;
    @Autowired
    private FavoritesService     favoritesService;
    @Autowired
    private CartService          cartService;
    @Autowired
    private GroupBuySponsorService groupBuySponsorService;
    @Autowired
    private GroupBuyService groupBuyService;
    @Autowired
    private GroupBuyCustomerService groupBuyCustomerService;
    @Autowired
    private ShopProductService shopProductService;

    /**
     * 店主推荐商品
     *
     * @param token
     * @param params
     * @return
     */
    @ValidateParams
    @RequiresRoles("seller")
    @RequestMapping(value = "/api/setRecommend", method = RequestMethod.POST)
    @ResponseBody
    public Object setRecommend(
            @RequestHeader(value = "token", defaultValue = "") String token,
            @Valid @RequestBody SetRecommendRequest params) {
        String shopId = StringUtils.getCustomerIdByToken(token);
        return productService.setShopProductRecommend(params.getProductId(), shopId, params.getType());
    }

    /**
     * 2016.04.11
     * Sprint6 (素材库)
     */
    /****************************************素材****************************************/
    /**
     * 店主设置默认素材
     *
     * @param token
     * @param params
     * @return
     */
    @ValidateParams
    @RequiresRoles("seller")
    @RequestMapping(value = "/api/setDefaultCopywrite", method = RequestMethod.POST)
    @ResponseBody
    public Object setDefaultCopywrite(
            @RequestHeader(value = "token", defaultValue = "") String token,
            @Valid @RequestBody SetDefaultCopywriteRequest params) {
        String shopId = StringUtils.getCustomerIdByToken(token);
        return productService.setShopProductDefaultCopywrite(
                params.getProductId(),
                params.getCopywriteId(),
                shopId,
                params.getType());
    }


    /**
     * 店主获取素材列表
     *
     * @param token
     * @param params
     * @return
     */
    @ValidateParams
    @RequiresRoles("seller")
    @RequestMapping(value = "/api/getCopywriteDataBaseList", method = RequestMethod.POST)
    @ResponseBody
    public Object getCopywriteList(
            @RequestHeader(value = "token", defaultValue = "") String token,
            @Valid @RequestBody GetCopywriteRequest params) {
        String shopId    = StringUtils.getCustomerIdByToken(token);
        String productId = params.getProductId();
        int    pageNo    = Integer.parseInt(params.getPageNo());
        int    count     = copywriteService.countGetCopywriteList(productId);
        // 组装分页信息
        Pager page = new Pager();
        page.setPageNo(pageNo);
        page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
        page.setRowCount(count);
        page.doPage();
        List<CopywriteDto>   list     = copywriteService.getCopywrites(shopId, productId, page, ApiConstants.SORT_SOURCE);
        GetCopywriteResponse response = new GetCopywriteResponse();
        response.setList(list);
        response.setPage(page);
        return response;
    }

    /**
     * 卖家获取自己小店商品列表
     *
     * @param token
     * @param params
     * @return
     */
    @ValidateParams
    @RequiresRoles("seller")
    @RequestMapping(value = "/api/getMyShopProducts", method = RequestMethod.POST)
    @ResponseBody
    public Object getMyShopProducts(
            @RequestHeader(value = "token", defaultValue = "") String token,
            @Valid @RequestBody GetMyShopProductsRequest params) {
        String customerId = StringUtils.getCustomerIdByToken(token);
        // 商品类型，all/hot/recommand
        String productType = params.getProductType();
        if (!StringUtils.isEmpty(productType))
            productType = productType.toUpperCase();
        // 排序字段 ，sales/collection/time/saleEarning/new
        String sortType = params.getSortType();
        // 是否下架，0上架 1下架
        String loadType = params.getLoadType();
        // 筛选分类
        String categoryId = params.getCategoryId();
        String condition = params.getCondition();
        int    pageNo     = Integer.parseInt(params.getPageNo());
        int count = productService.countShopProductList2(productType,
                customerId, loadType, condition);
        // 组装分页信息
        Pager page = new Pager();
        page.setPageNo(pageNo);
        page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
        page.setRowCount(count);
        page.doPage();
        List<ProductDto> list = productService.getShopProductList2(
                productType, customerId, loadType,
                sortType, page, categoryId, condition);
        Shop shop = shopService.get(customerId);

        for (ProductDto productDto : list) {
            if (shop.getAdvancedShop() == 1) {
                if (productDto.getGroupBuyProduct()) {
                    productDto.setSaleEarningFund((productDto.getSaleEarningFund().add(productDto.getBonusEaringFund())).multiply(new
                            BigDecimal(productDto.getNeededPeople() - 1)));
                } else
                    productDto.setSaleEarningFund(productDto.getSaleEarningFund().add(productDto.getBonusEaringFund()));
            } else {
                if (productDto.getGroupBuyProduct()) {
                    productDto.setSaleEarningFund((productDto.getSaleEarningFund()).multiply(new BigDecimal(productDto.getNeededPeople() -1 )));
                } else
                    productDto.setSaleEarningFund(productDto.getSaleEarningFund());
            }
        }
        GetMyShopProductsResponse response = new GetMyShopProductsResponse();
        response.setList(list);
        response.setPage(page);
        return response;
    }

    /****************************************素材****************************************/

    /**
     * 2016.05.09
     * Sprint7 (团购)
     */
    /****************************************团购****************************************/
    /**
     * 店主在商城中查看团购商品详情 用户点击团购列表跳转时调用此接口
     * @param token
     * @param params type, groupBuyId
     * @return
     */
    @ValidateParams
    @RequiresRoles("seller")
    @ResponseBody
    @RequestMapping(value = "/api/getVcatGroupBuyProductDetail", method = RequestMethod.POST)
    public Object getVcatGroupBuyProductDetail(
            @RequestHeader(value = "token", defaultValue = "") String token,
            @Valid @RequestBody GetGroupBuyProductDetailRequest params) {
        return getGroupBuyProductDetail(token,params);
    }

    /**
     * 买家查看团购商品详情,买家使用,从分享(预览)进入，并不需要登录
     * @param
     * @param params
     * @return
     */
    @ValidateParams
    @ResponseBody
    @RequestMapping(value = "/anon/getSellerGroupBuyProductDetail", method = RequestMethod.POST)
    public Object getSellerGroupBuyProductDetail(
            @RequestHeader(value = "token", defaultValue = "") String token,
            @Valid @RequestBody GetGroupBuyProductDetailRequest params) {
        return getGroupBuyProductDetail(token,params);
    }


    private Object getGroupBuyProductDetail(String token, GetGroupBuyProductDetailRequest params) {
        // 获取团购商品信息
        int     type    = params.getType();
        String  groupBuyId = "";
        String groupBuySponsorId = "";
        GroupBuyDto groupBuyDto = new GroupBuyDto();
        groupBuyId = params.getGroupBuyId();
        GroupBuySponsorDto groupBuySponsorDto = new GroupBuySponsorDto();
        groupBuySponsorId = params.getGroupBuySponsorId();
        if (!StringUtils.isBlank(groupBuyId)) {
            groupBuyDto.setId(groupBuyId);
            groupBuyDto = groupBuyService.getGroupBuy(groupBuyDto);
        } else if (!StringUtils.isBlank(groupBuySponsorId)) {
            groupBuySponsorDto.setId(groupBuySponsorId);
            groupBuySponsorDto = groupBuySponsorService.getGroupBuySponsor(groupBuySponsorDto);
            groupBuyDto = groupBuySponsorDto.getGroupBuyDto();
            groupBuyId = groupBuyDto.getId();
        } else {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
        }
        String productId = groupBuyDto.getProductId();
        // 判断用户是否收藏,默认没收藏
        String isCollect = ApiConstants.NO;
        // 购物车数量
        int cartCount = 0;
        // 如果是微信用户，判断用户是否收藏
        String buyerId = StringUtils.getCustomerIdByToken(token);

        if (!StringUtils.isBlank(buyerId)) {
            Favorites fav   = new Favorites();
            Customer  buyer = new Customer();
            buyer.setId(buyerId);
            fav.setCustomer(buyer);
            Shop shop = new Shop();
            fav.setShop(shop);
            Product product = new Product();
            product.setId(productId);
            fav.setProduct(product);
            GroupBuy groupBuy = new GroupBuy();
            groupBuy.setId(groupBuyId);
            fav.setGroupBuy(groupBuy);
            fav.setFavType(type+"");
            if (favoritesService.get(fav) != null) {
                isCollect = ApiConstants.YES;
            }
            cartCount = cartService.countByBuyerId(buyerId);
        }
        //endregion
        String shopId = params.getShopId();
        Product product;
        if(type != ApiConstants.SELLER_LAUNCH && !StringUtils.isBlank(shopId)){
            product = productService.getGroupBuyProductDetail(shopId,groupBuyId, type);
        }else
            product = productService.getGroupBuyProductDetail(buyerId,groupBuyId, type);
        if (product == null) {
            return new MsgEntity(ApiMsgConstants.PRODUCT_NOT_FIND,
                    ApiMsgConstants.NOTFIND_CODE);
        }
        GetGroupBuyProductDetailResponse result        = new GetGroupBuyProductDetailResponse();
        // 评论数量
        int                              reviewCount   = ratingSummaryService.countReviewList(productId);
        // 运费模板
        BigDecimal                       freightPrice  = freightConfigService.getFreightPrice(buyerId, productId);
        // 规格标题列表
        List<Map<String, Object>>        itemTitleList = productService.getProductItemTitleList(productId);
        // 规格详情
        List<Map<String, Object>>        itemInfo      = productService.getGroupBuyProductItemInfo(groupBuyId);
        Shop                             shop          = shopService.get(buyerId);
        Shop                             customerShop  = null;

        // 店主算一个人头
        int    joinedCount = 0;
        int groupBuyStatus = 0;
        int productItemType = 0;
        if (type == ApiConstants.SELLER_LAUNCH) { // 卖家开团查看详情
            // 店主获取团购商品详情
            joinedCount = 1;
            groupBuyStatus = 1;
            productItemType = ApiConstants.SELLER_LAUNCH;
        } else {
            customerShop = shopService.get(shopId);
            if (StringUtils.isBlank(shopId) || customerShop == null) {
                return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                        ApiMsgConstants.NOTFIND_CODE);
            }
            // 买家开团查看详情
            if(!StringUtils.isBlank(groupBuyId)){
                joinedCount = 1;
                groupBuyStatus = 1;
                productItemType = ApiConstants.BUYER_LAUNCH;
            }
            // 买家参团查看详情
            else if(StringUtils.isBlank(groupBuyId) && !StringUtils.isBlank(groupBuySponsorId)){
                joinedCount = groupBuySponsorDto.getJoinedCount();
                groupBuyStatus = 2;
                productItemType = ApiConstants.BUYER_LAUNCH;
            }else{
                return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
            }
        }

        Map<String, Object>       items         = productService.refactorPrice(itemInfo,type,shop);
        result.setProductInfo(
                shop,
                customerShop,
                product,
                isCollect,
                reviewCount,
                freightPrice,
                itemTitleList,
                items,
                cartCount,
                productItemType,
                joinedCount,
                groupBuyStatus,
                groupBuySponsorId);
        return result;
    }

    /**
     * 用户单独购买团购商品时 加入购物车 走普通购买流程
     * 用户开团/参团 (点击立即开团/参团时调用)
     * 检查是否限购
     * 检查通过后进入确认订单页面
     * @param token
     * @param params
     * @return
     * 如果是普通开团和参团 检查通过后调用获取地址接口(0元开团不调用),再走下单流程
     * 如果是参团,检查通过后调用支付接口,支付成功后跳转到参团成功页面(H5控制)
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value={"seller","buyer"},logical = Logical.OR)
    @RequestMapping(value = "/api/checkGroupBuyProduct", method = RequestMethod.POST)
    public Object checkGroupBuyProduct(
            @RequestHeader(value = "token", defaultValue = "") String token,
            @Valid @RequestBody CheckGroupBuyProductRequest params) {
        String customerId = StringUtils.getCustomerIdByToken(token);
        Customer customer = customerService.get(customerId);
        // 零元开团=0,卖家开团=8,买家开团/参团=9
        int type            = params.getType();
        String groupBuyId   = params.getGroupBuyId();
        String groupBuySponsorId = params.getGroupBuySponsorId();
        String shopId = params.getShopId();
        // 购买个数
        int    productCount = params.getProductCount();
        // 限购个数
        int limitCount = 0;
        // 获取团购商品信息
        GroupBuyDto groupBuyDto = new GroupBuyDto();
        groupBuyDto.setId(groupBuyId);
        groupBuyDto = groupBuyService.getGroupBuy(groupBuyDto);

        GroupBuySponsorDto groupBuySponsorDto = new GroupBuySponsorDto();
        groupBuySponsorDto.setId(groupBuySponsorId);
        groupBuySponsorDto = groupBuySponsorService.getGroupBuySponsor(groupBuySponsorDto);
        Date endTime                        = null;
        Date now                            = new Date();
        CheckGroupBuyProductResponse result = new CheckGroupBuyProductResponse();
        // 零元购买的用户必须有店铺
        Map<String,Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        if(type == ApiConstants.ZERO_LAUNCH && customer.getRoleList().size() == 2){
            if (groupBuyDto == null) {
                return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
            }
            String newGroupBuySponsorId = groupBuyService.addGroupBuyInfo(groupBuyDto,customerId,null,ApiConstants.ZERO_LAUNCH); // 0元开团
            map.put("groupBuySponsorId", newGroupBuySponsorId);
            map.put("type", 0);
            return map;
        }
        // 卖家开团  检查团购商品是否超时
        else if (type == ApiConstants.SELLER_LAUNCH) {
            if (groupBuyDto == null) {
                return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
            }
            if(groupBuyDto.getArchived() == 1){
                return new MsgEntity(ApiMsgConstants.OUT_OF_STOCK, ApiMsgConstants.FAILED_CODE);
            }
            endTime   = groupBuyDto.getEndDate();
            if(productCount <= 0){
                return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
            }
            if(now.getTime() >= endTime.getTime()){
                return new MsgEntity(ApiMsgConstants.OVERTIME, ApiMsgConstants.FAILED_CODE);
            }
            limitCount = groupBuyDto.getLimitCount() == null ? 0 : groupBuyDto.getLimitCount();
            if (limitCount != 0 && (productCount > limitCount)) {
                return new MsgEntity(ApiMsgConstants.OVERLIMITCOUNT, ApiMsgConstants.FAILED_CODE);
            }
            map.put("type",ApiConstants.SELLER_LAUNCH);
            map.put("groupBuyId",groupBuyId);
        }
        // 买家开团/参团
        else if(type == ApiConstants.BUYER_LAUNCH){
            if(StringUtils.isBlank(shopId) || shopService.get(shopId) == null){
                return new MsgEntity("该商品已下架!", ApiMsgConstants.FAILED_CODE);
            }
            if(groupBuyDto != null){
                if(groupBuyDto.getArchived() == 1){
                    return new MsgEntity(ApiMsgConstants.OUT_OF_STOCK, ApiMsgConstants.FAILED_CODE);
                }
                endTime = groupBuyDto.getEndDate();
                limitCount = groupBuyDto.getLimitCount() == null ? 0 : groupBuyDto.getLimitCount();
                map.put("groupBuyId",groupBuyId);
            }else if(groupBuySponsorDto != null){ // 如果是买家参团
                // 根据groupBuySponsorId判断此拼团商品是否已下架
                Shop shop = new Shop();
                Product product = new Product();
                product.setId(groupBuySponsorDto.getGroupBuyDto().getProductId());
                if(groupBuySponsorDto.getType() == 0 || groupBuySponsorDto.getType() == 1){ // 卖家开团
                    shop.setId(groupBuySponsorDto.getSponsorId());
                } else if(groupBuySponsorDto.getType() == 2){ // 买家开团
                    shop.setId(groupBuyCustomerService.getShopIdByGroupBuySponsorId(groupBuySponsorDto.getId()));
                }
                ShopProduct shopProduct = new ShopProduct();
                shopProduct.setShop(shop);
                shopProduct.setProduct(product);
                shopProduct = shopProductService.get(shopProduct);
                if(shopProduct == null || shopProduct.getArchived() == 1){ // 如果是卖家开团
                    return new MsgEntity("该商品已被店主下架!", ApiMsgConstants.FAILED_CODE);
                }
                if(groupBuySponsorDto.getGroupBuyDto().getArchived() == 1){
                    return new MsgEntity(ApiMsgConstants.OUT_OF_STOCK, ApiMsgConstants.FAILED_CODE);
                }
                // 拼团是否结束
                if(Objects.equals(groupBuySponsorDto.getStatus(), GroupBuySponsor.START_FAIL) || Objects.equals(groupBuySponsorDto
                        .getStatus(), GroupBuySponsor.FAIL)){
                    return new MsgEntity(ApiMsgConstants.OVERTIME, ApiMsgConstants.FAILED_CODE);
                }
                // 检查此拼团是否锁定
                if(groupBuySponsorDto.getLocked()){
                    return new MsgEntity(ApiMsgConstants.GROUPBUY_LOCKED, ApiMsgConstants.FAILED_CODE);
                }
                // 是否参加过此次拼团
                Map<String,Object> groupBuySponsorStatus = groupBuySponsorService.groupBuystatusByCustomer(customerId, groupBuySponsorId);
                if(groupBuySponsorStatus.get("isJoined") != null){  // 参加过此次团购
                    return new MsgEntity(ApiMsgConstants.GROUPBUY_JOINED, ApiMsgConstants.FAILED_CODE);
                }
                endTime   = groupBuySponsorDto.getEndDate();
                limitCount = groupBuySponsorDto.getGroupBuyDto().getLimitCount() == null ? 0 : groupBuySponsorDto.getGroupBuyDto().getLimitCount();
                map.put("groupBuySponsorId",groupBuySponsorId);
            }else{
                return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
            }
            if(productCount <= 0){
                return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
            }
            if(now.getTime() >= endTime.getTime()){
                return new MsgEntity(ApiMsgConstants.OVERTIME, ApiMsgConstants.FAILED_CODE);
            }

            if (limitCount != 0 && (productCount > limitCount)) {
                return new MsgEntity(ApiMsgConstants.OVERLIMITCOUNT, ApiMsgConstants.FAILED_CODE);
            }
            map.put("type",ApiConstants.BUYER_LAUNCH);
        }
        else {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
        }
        return map;
    }

    /**
     * 支付后和点击分享链接时获取团购状态
     * 支付后:8.卖家开团; 9.买家开团; 9.买家参团
     * 分享链接: 用户点击分享的链接时传参  type = 4
     * @param
     * @param params
     * @return
     * groupBuyStatus 1.开团成功; 2.参团成功; 3.拼团成功; 4.参团页; 5.还有机会; 6.拼团失败
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value={"seller","buyer"} ,logical = Logical.OR)
    @RequestMapping(value = "/anon/getGroupBuyStatusInfo", method = RequestMethod.POST)
    public Object getGroupBuyStatusInfo(@RequestHeader(value = "token", defaultValue = "") String token,
            @Valid @RequestBody GetGroupBuyStatusInfoRequest params) {
        String customerId = StringUtils.getCustomerIdByToken(token);
        //int    type              = params.getType();
        String groupBuySponsorId = params.getGroupBuySponsorId();
        GroupBuySponsorDto groupBuySponsorDto = new GroupBuySponsorDto();
        groupBuySponsorDto.setId(groupBuySponsorId);
        groupBuySponsorDto = groupBuySponsorService.getGroupBuySponsor(groupBuySponsorDto);
        // 参团者信息
        List<Map<String, Object>> joinedCustomers = groupBuyCustomerService.getJoinedCustomers(groupBuySponsorId);
        // 团购商品信息
        List<Map<String, Object>> itemInfo = productService.getGroupBuyProductItemInfo(groupBuySponsorDto.getGroupBuyDto().getId());
        itemInfo.get(0).put("singlePrice",itemInfo.get(1).get("singlePrice"));
        GetGroupBuyStatusInfoResponse result = new GetGroupBuyStatusInfoResponse();
        int headCount = groupBuySponsorDto.getGroupBuyDto().getNeededPeople();
        int joinedCount = joinedCustomers.size();
        int inventory    = groupBuySponsorDto.getGroupBuyDto().getInventory();      // 团购商品库存
        result.setHeadCount(headCount); // 拼团人数
        result.setJoinedCount(joinedCount);                                         // 参团人数
        result.setGroupBuySponsorId(groupBuySponsorId);                             // 拼团Id
        result.setEndDate(groupBuySponsorDto.getEndDate());                         // 拼团结束时间
        result.setItemInfo(itemInfo.get(0));                                               // 商品信息
        result.setJoinedCustomers(joinedCustomers);                                 // 参团人信息
        Integer lc = groupBuySponsorDto.getGroupBuyDto().getLimitCount();
        if (lc == null) {
            lc = 0;
        }
        result.setLimitCount(lc);
        //result.setSponsorId(groupBuySponsorDto.getSponsorId()); // 发起人Id
        // 根据状态判断跳转哪个页面
        return groupBuySponsorService.gotoPageByStatus(result,customerId,groupBuySponsorDto,params.getFrom());
    }

    /**
     * 获取预览小店中的商品列表(h5)
     * @param params
     * @return
     */
    @ValidateParams
    @ResponseBody
    @RequestMapping(value = "/anon/getSellerProductList", method = RequestMethod.POST)
    public Object getSellerProductList(
            @Valid @RequestBody GetSellerProductListRequest params){
        String shopId = params.getShopId();
        // 商品类别，不传表示达人下面的所有
        String categoryId = params.getCategoryId();
        String productType = params.getProductType();
        if(StringUtils.isBlank(shopId)){
            shopId = cfgService.findCfgValue("default_shop_id");
        }
        int pageNo  = params.getPageNo();
        //如果有参数为空，设置默认值
        if(productType==null||pageNo==0){
            productType = ApiConstants.BG_TYPE_ALL;
            pageNo=1;
        }
        //转换为大写，SALE HOT RESERVE
        if(!StringUtils.isEmpty(productType))
            productType = productType.toUpperCase();
        if(StringUtils.isBlank(shopId)){
            shopId = cfgService.findCfgValue("default_shop_id");
        }
        Pager page = new Pager();
        page.setPageNo(pageNo);
        int count = productService.countSellerProductList(productType,shopId,categoryId);
        page.setRowCount(count);
        page.doPage();
        List<ProductDto> list = productService.getSellerProductList(shopId,productType,page,categoryId);
        Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("page", page);
        map.put("productList", list);
        return map;
    }
    /****************************************团购****************************************/
}



