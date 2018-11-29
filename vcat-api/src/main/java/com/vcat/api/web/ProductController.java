package com.vcat.api.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vcat.api.service.*;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.config.Global;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.config.CacheConfig;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.CartDto;
import com.vcat.module.ec.dto.GroupBuyProductDto;
import com.vcat.module.ec.dto.ProductDto;
import com.vcat.module.ec.entity.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
/**
 * 关于商品相关，请求控制器
 * 
 * @author cw 2015年5月18日 18:58:49
 */
@RestController
@ApiVersion({1,2})
public class ProductController extends RestBaseController{

	@Autowired
	private ProductService productService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ShareEarningService shareEarningService;
	@Autowired
	private ShareEarningLogService shareEarningLogService;
	@Autowired
	private FavoritesService favoritesService;
	@Autowired
	private CartService cartService;
	@Autowired
	private ShopProductService shopProductService;
	@Autowired
	private RatingSummaryService ratingSummaryService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private FreightConfigService freightConfigService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private ServerConfigService cfgService;
	@Autowired
	private ProductCategoryService productCategoryService;
	@Autowired
	private CouponFundService couponFundService;
	@Autowired
	private ShopBgImageService shopBgImageService;
	@Autowired
	private FeaturedPageService featuredPageService;
    @Autowired
    private ShopService shopService;

	/**
	 * 卖家获取自己小店商品列表
	 * @param token
	 * @param param
	 * @return
	 */
//	@RequiresRoles("seller")
//	@RequestMapping("/api/getProducts")
//	public Object getProducts(
//			@RequestHeader(value = "token", defaultValue = "") String token,
//			@RequestParam(value = "params", defaultValue = "") String param){
//		// 简单验证,保证服务端不会出异常
//		JSONObject params = null;
//		try {
//			params = JSONObject.parseObject(param);
//		} catch (Exception e) {
//			logger.error("params 出错"+e.getMessage());
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		if (params == null) {
//			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
//					ApiMsgConstants.FAILED_CODE);
//		}
//		// 商品类型，all/hot
//		String productType = params.getString("productType");
//		if (!StringUtils.isEmpty(productType))
//			productType = productType.toUpperCase();
//		// 商品类别，不传表示达人下面的所有
//		String categoryId = params.getString("categoryId");
//		// 排序字段 ，sales/collection/time/saleEarning
//		String sortType = params.getString("sortType");
////		//商品品牌id，不传表示达人下面的所有
////		String supplierId = params.getString("supplierId");
////		// 是否需要拿样,默认全部，不筛选
////		String isNeedTake = params.getString("isNeedTake");
////		// 达人id，当获取达人下的所有商品时需要
////		String guruId = params.getString("guruId");
//		// 是否下架，0上架 1下架
//		String loadType = params.getString("loadType");
//		String customerId = StringUtils.getCustomerIdByToken(token);
//		int pageNo = params.getIntValue("pageNo");
////		List<String> categoryIds = new ArrayList<>();
////		// 当没有categoryId时，获取所有达人下的类别
////		if (StringUtils.isBlank(categoryId) && !StringUtils.isBlank(guruId)) {
////			GuruCategory gc = new GuruCategory();
////			Guru guru = new Guru();
////			guru.setId(guruId);
////			gc.setGuru(guru);
////			GuruCategory guruCategory = guruCategoryService.get(gc);
////			if (guruCategory != null && guruCategory.getChilds() != null
////					&& guruCategory.getChilds().size() != 0) {
////				for (ProductCategory catg : guruCategory.getChilds()) {
////					categoryIds.add(catg.getId());
////				}
////			}
////		}
////		// 当传categoryId时，即使获取当前类别下的所有商品
////		if (!StringUtils.isBlank(categoryId)) {
////			categoryIds.add(categoryId);
////		}
////		if (categoryIds.size() == 0) {
////			categoryIds.add(ApiConstants.RANK_TYPE_ALL);
////		}
////		List<String> supplierIds = new ArrayList<>();
////		if (StringUtils.isBlank(supplierId) && !StringUtils.isBlank(guruId)) {
////			//获取guru信息
////			List<Map<String,Object>> sup = null;
////			Guru guru = guruService.get(guruId);
////			if(guru!=null){
////				sup = brandService.getBrandList(customerId,guru.getId());
////			}
////			if(sup!=null&&sup.size()>0){
////				for(Map<?,?> supplier:sup){
////					//查询出来的数据如果有绑定，当做查询条件
////					if(ApiConstants.SUPER.equals(guru.getGuruType()+"")&&ApiConstants.YES.equals((String)supplier.get("isBind")))
////						supplierIds.add((String) supplier.get("id"));
////					if(ApiConstants.NORMAL.equals(guru.getGuruType()+"")){
////						supplierIds.add((String) supplier.get("id"));
////					}
////				}
////			}
////
////		}
////		if(!StringUtils.isBlank(supplierId)){
////			supplierIds.add(supplierId);
////		}
//		int count = productService.countShopProductList(productType,
//				customerId, loadType,categoryId);
//		// 组装分页信息
//		Pager page = new Pager();
//		page.setPageNo(pageNo);
//		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
//		page.setRowCount(count);
//		page.doPage();
//
//		List<ProductDto> list = productService.getShopProductList(
//				productType, customerId,loadType,
//				sortType, page,categoryId);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
//		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
//		map.put("page", page);
//		map.put("list", list);
//		return map;
//	}
	/**
	 * 获得卖家所有上架商品
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/getProductsSellerView")
	public Object getProductsSellerView(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param){
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错"+e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String loadType = "0";
		String customerId = StringUtils.getCustomerIdByToken(token);
		int pageNo = params.getIntValue("pageNo");
		int count = productService.countShopProductList(null,
				customerId, loadType,null);
		// 组装分页信息
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();
		String sortType = "default";
		List<ProductDto> list = productService.getShopProductList(
				null, customerId,loadType,
				sortType, page,null);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("page", page);
		map.put("list", list);
		return map;
	}



	/**
	 * 分享日志记录 用于分享得钱，已不使用
	 * @param token
	 * @param productId
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/shareProductLog")
	public Object ShareProductLog(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "productId", defaultValue = "") String productId,
			@RequestParam(value = "shareType", defaultValue = "") String shareType){
		if(StringUtils.isEmpty(productId)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String shopId = StringUtils.getCustomerIdByToken(token);
		Product product = new Product();
		product.setId(productId);
		ShareEarning shareEarning = new ShareEarning();
		shareEarning.setProduct(product);
		ShareEarning earning = shareEarningService.getAvaShareEarning(shareEarning);
		if(earning==null||earning.getAvailableShare()<earning.getSharedCount()){
				return new MsgEntity(ApiMsgConstants.SHARE_PRODUCT_LOG_FAILED,
						ApiMsgConstants.FAILED_CODE);
		}
		//组装分享日志
		ShareEarningLog shareLog = new ShareEarningLog();
	
		shareLog.setShopId(shopId);
		shareLog.setShareId(earning.getId());
		ShareEarningLog log = shareEarningLogService.get(shareLog);
		if(log!=null){
			return new MsgEntity(ApiMsgConstants.SHARE_PRODUCT_LOG_EXSIT,
					ApiMsgConstants.FAILED_CODE);
		}
		ThirdLoginType type = new ThirdLoginType();
		shareLog.preInsert();
		type.setCode(shareType);
		shareLog.setThirdLoginType(type);
		shareEarningLogService.saveLog(earning, shareLog, earning.getProduct().getName());
		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
				ApiMsgConstants.SUCCESS_CODE);
	}

	/**
	 * 增加商品到购物车
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/addCart")
	public Object addCart(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param){
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错"+e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String buyerId = StringUtils.getCustomerIdByToken(token);
		JSONArray cardList = params.getJSONArray("cartList");
		if(cardList==null||cardList.size()==0){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		List<Cart> carts = new ArrayList<Cart>(cardList.size());
		for(Object object:cardList){
			JSONObject json = (JSONObject) object;
			Cart item = JSON.toJavaObject(json, Cart.class);
			carts.add(item);
		}
		cartService.saveCarts(buyerId,carts);
		int cartCount = cartService.countByBuyerId(buyerId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("cartCount", cartCount);
		return map;
	}

	/**
	 * 更新购物车商品数量
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/updateCartList")
	public Object updateCartList(@RequestHeader(value = "token", defaultValue = "") String token,
								 @RequestParam(value = "params", defaultValue = "") String param){
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错"+e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		JSONArray list = params.getJSONArray("cartList");
		if(list==null||list.size()==0){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		cartService.updateQuantity(list);
		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
				ApiMsgConstants.SUCCESS_CODE);
	}

	/**
	 * 获取购物车信息
	 * @param token
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/getCartList")
	public Object getCartList(@RequestHeader(value = "token", defaultValue = "") String token){
		String buyerId = StringUtils.getCustomerIdByToken(token);
		List<CartDto> list = cartService.getCartList(buyerId);
		//获取我的猫币金额
		Shop shop = new Shop();
		BigDecimal couponFundPrice = BigDecimal.ZERO;
		shop.setId(buyerId);
		CouponFund couponFund = new CouponFund();
		couponFund.setShop(shop);
		CouponFund conFund = couponFundService.get(couponFund);
		if(conFund!=null){
			couponFundPrice = conFund.getAvailableFund();
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", list);
		if(couponFundPrice.compareTo(BigDecimal.ZERO)>0){
			map.put("shopCouponFund", couponFundPrice); //店铺的剩余猫币
		}
		return map;
	}

	/**
	 * 从购物车删除商品
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("buyer")
	@RequestMapping("/api/deleteCart")
	public Object deleteCart(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param){
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错"+e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String cartIds = params.getString("cartIds");
		if(StringUtils.isEmpty(cartIds)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		List<String> list = Arrays.asList(cartIds.split("\\,"));
		cartService.deleteCarts(list);
		return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
				ApiMsgConstants.SUCCESS_CODE);
	}

	/**
	 * 获取店铺上架了得商品的分类列表（筛选功能的分类）
	 * @param token
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getShopCategoryList")
	public Object getShopCategoryList(@RequestHeader(value = "token", defaultValue = "") String token){
		
		String customerId = StringUtils.getCustomerIdByToken(token);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		List<ProductCategory> list = productCategoryService.getListByCustomer(customerId);
		map.put("list", list);
		return map;
	}

	/**
	 * 获取商品分类列表，V猫供货市场使用
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getCategoryList")
	@Cacheable(value = CacheConfig.GET_CATEGORY_LIST_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public Object getCategoryList(@RequestParam(value = "parentCategoryId", defaultValue = "") String parentCategoryId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		List<Map<String, Object>> list = new ArrayList<>();
		// 如果获取主分类
		if (StringUtils.isBlank(parentCategoryId)) {
			Map<String, Object> c1 = new HashMap<>();
			c1.put("id", IdGen.uuid());
			c1.put("name", ApiConstants.RECOMMENT_NAME);
			list.add(c1);
			//Map<String, Object> c2 = new HashMap<>();
			//c2.put("id", IdGen.uuid());
			//c2.put("name", ApiConstants.LEROY_NAME);
			//list.add(c2);
			//Map<String, Object> c3 = new HashMap<>();
			//c3.put("id", IdGen.uuid());
			//c3.put("name", ApiConstants.REVERT_NAME);
			//list.add(c3);
			Map<String, Object> c4 = new HashMap<>();
			c4.put("id", IdGen.uuid());
			c4.put("name", ApiConstants.NEW_NAME);
			list.add(c4);
			//Map<String, Object> c5 = new HashMap<>();
			//c5.put("id", IdGen.uuid());
			//c5.put("name", ApiConstants.COUPON_NAME);
			//list.add(c5);
			List<Map<String, Object>> clist = productCategoryService.findCategoryList("0");
			if (clist != null && clist.size() > 0) {
				clist = productCategoryService.findCategoryList((String) clist
						.get(0).get("id"));
			}
			list.addAll(clist);
		} else {
			//获取子类别
			list = productCategoryService.findCategoryList(parentCategoryId);
			List<Brand> brandList = productCategoryService.findBrandList(parentCategoryId);
			map.put("brandList", brandList);
		}
		map.put("list", list);
		return map;
	}

	/**
	 * 获取v猫商场首页广告位配置信息
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getVcatShopConfig")
	public Object getVcatShopConfig(@RequestHeader(value = "token", defaultValue = "") String token){
		//Map<String, Object> map = new HashMap<String, Object>();
		//map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		//map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		////获取v猫商场轮播图
		//map.put("vcatList", shopBgImageService.getIndexImageList(ApiConstants.SUPER));
		////0元秒杀
		//List<FeaturedPage> page1 = featuredPageService.getByType(ApiConstants.COUPON_TYPE);
		//if(page1!=null&&page1.size()>0){
		//	map.put(ApiConstants.COUPON_TYPE, page1.get(0));
		//}
		////每日精选
		//List<FeaturedPage> page2 = featuredPageService.getByType(ApiConstants.SELECTION_TYPE);
		//if(page2!=null&&page2.size()>0){
		//	map.put(ApiConstants.SELECTION_TYPE, page2.get(0));
		//}
		////精品推荐
		//List<FeaturedPage> page3 = featuredPageService.getByType(ApiConstants.CATEGORY_TYPE);
		//if(page3!=null&&page3.size()>0){
		//	map.put(ApiConstants.CATEGORY_TYPE, page3);
		//}
		//return map;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		//获取v猫商场轮播图
		map.put("vcatList", shopBgImageService.getIndexImageList(ApiConstants.SUPER));
		// 天天拼团
		List<FeaturedPage> page1 = featuredPageService.getByType(ApiConstants.GROUP_BUY_TYPE);
		if (page1 != null && page1.size() > 0) {
			map.put(ApiConstants.GROUP_BUY_TYPE, page1.get(0));
		}
		// 每日精选
		List<FeaturedPage> page2 = featuredPageService.getByType(ApiConstants.SELECTION_TYPE);
		if (page2 != null && page2.size() > 0) {
			map.put(ApiConstants.SELECTION_TYPE, page2.get(0));
		}
		// V猫庄园
		List<FeaturedPage> page3 = featuredPageService.getByType(ApiConstants.LEROY_TYPE);
		if (page3 != null && page3.size() > 0) {
			map.put(ApiConstants.LEROY_TYPE, page3.get(0));
		}
		return map;
	}
	
	/**
	 * 买家通过shopId获取卖家对应的商品分类和二级分类（H5页面导航下的商品分类接口）
	 * @return
	 */
	@RequestMapping("/anon/getSellerCategoryList")
	public Object getSellerCategoryList(@RequestParam(value = "shopId", defaultValue = "") String shopId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		if(StringUtils.isBlank(shopId)){
			shopId = cfgService.findCfgValue("default_shop_id");
		}
		List<ProductCategory> list = productCategoryService.getListByShopId(shopId);
		map.put("list", list);
		return map;
	}

	/**
	 * 入库商品、或者取消入库，上架或者下架
     * 店主代理预售商品后,上架店铺,库存为0
	 * @param token
	 * @param productId
	 * @return
	 */
	@RequestMapping("/api/operationProduct")
	@CacheEvict(value = CacheConfig.GET_VCAT_PRODUCT_DETAIL_CACHE, key = "T(com.vcat.common.utils.StringUtils).getCustomerIdByToken(#token)+#productId", cacheManager = "apiCM")
	public Object operationProduct(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "productId", defaultValue = "") String productId,
			@RequestParam(value = "operationType", defaultValue = "") String operationType){
		if(StringUtils.isBlank(operationType)||StringUtils.isBlank(productId)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String customerId = StringUtils.getCustomerIdByToken(token);
		
		productService.operateProduct(customerId,productId,operationType);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		return map;
	}


    /**
     * 获取商品的详细信息,买家使用,从分享进入，并不需要登录
     * @param token
     * @return
     */
    @RequestMapping("/anon/getSellerProductDetail")
	@Cacheable(value = CacheConfig.GET_SELLER_PRODUCT_DETAIL_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
    public Object getSellerProductDetail(
            @RequestHeader(value = "token", defaultValue = "") String token,
            @RequestParam(value = "params",defaultValue="") String param) {
        // 简单验证,保证服务端不会出异常
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(param);
        } catch (Exception e) {
            logger.error("params 出错"+e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String shopId = params.getString("shopId");
        // 买家查看商品详情,类型都为 normal
        String couponType = "";
        if(StringUtils.isBlank(couponType)){
            couponType = ApiConstants.NORMAL;
        }
        String productId = params.getString("productId");
        if(StringUtils.isBlank(productId)||StringUtils.isBlank(shopId)){
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }

        // 判断用户是否收藏,默认没收藏
        String isCollect = ApiConstants.NO;
        //购物车数量
        int cartCount = 0;
        // 如果是微信用户，判断用户是否收藏
        String buyerId = StringUtils.getCustomerIdByToken(token);
        if (!StringUtils.isBlank(buyerId)) {
            Favorites fav = new Favorites();
            Customer buyer = new Customer();
            buyer.setId(buyerId);
            Shop shop = new Shop();
            shop.setId(shopId);
            Product product = new Product();
			GroupBuy groupBuy = new GroupBuy();
			fav.setGroupBuy(groupBuy);
            product.setId(productId);
            fav.setProduct(product);
            fav.setCustomer(buyer);
            fav.setShop(shop);
            fav.setFavType(ApiConstants.NORMAL);
            if (favoritesService.get(fav) != null) {
                isCollect = ApiConstants.YES;
            }
            cartCount = cartService.countByBuyerId(buyerId);
        }
        Customer customer  = customerService.get(shopId);
        Product product = productService.getSellerProductDetail(shopId, productId,couponType);
        if(product==null){
            return new MsgEntity(ApiMsgConstants.PRODUCT_NOT_FIND,
                    ApiMsgConstants.NOTFIND_CODE);
        }

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("id", product.getId());

        res.put("name", product.getName());
        res.put("productCollectCount", product.getProductCollectCount());
        res.put("saledNum", product.getSaledNum());
        res.put("description", product.getDescription());
        res.put("inventory", product.getInventory());
        res.put("price", product.getPrice());
        res.put("isHot", product.getIsHot() != 0);
        res.put("reviewCount", ratingSummaryService.countReviewList(productId));
		Date startTime = product.getReserveRecommend().getStartTime();
		Date endTime =  product.getReserveRecommend().getEndTime();
        if(startTime == null || endTime == null) {
            res.put("isReserve", false);
            res.put("isReserveOver", false);
            //如果是不是预售商品
            res.put("canRefund",product.getCanRefund());
        }
        else {
            res.put("canRefund",false);
            res.put("isReserve", true);
            if(endTime.after(new Date())){
                res.put("isReserveOver", false);
            }else res.put("isReserveOver", true);
        }
        res.put("reserveStartTime", startTime);
        res.put("reserveEndTime", endTime);
        res.put("supplier",product.getBrand());
        res.put("copywrite", product.getCopywrite());
        res.put("category", product.getCategory());
        //是否支持退货
        //是否为虚拟商品(V猫庄园商品)
        res.put("isVirtualProduct",product.getVirtualProduct());
        // 商品规格
        Map<String, Object> itemList = productService.getProductItemList(productId,ApiConstants.SUPER);
		// Code.Ai ( 买家查看商品详情,如果是预售商品,则显示小店中的库存)
        //Collection maps =itemList.values();
        //for(Object lmap : maps){
         //   Map<String,Object> ss = (HashMap<String,Object>)lmap;
		//	if(startTime != null || endTime != null){
         //       int inventory = product.getInventory();
         //       ss.put("inventory",inventory);
		//	}
        //}
        res.put("itemList", itemList);
        res.put("itemTitleList", productService.getProductItemTitleList(productId));
        res.put("imageList", product.getImageList());
        res.put("propertyList", product.getPropertyList());
        res.put("isCollect", isCollect);
        res.put("cartCount", cartCount);
        res.put("couponType", couponType);
        res.put("couponPrice", product.getCouponValue());
        res.put("freightPrice", freightConfigService.getFreightPrice(buyerId,productId));
        res.put("isDownLoad", product.getIsDownLoad());
		res.put("isSellerLoad", product.getIsSellerLoad());
        product.setReserveRecommend(product.getReserveRecommend().getId()==null?null:product.getReserveRecommend());
        Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("shopName", customer==null?"":customer.getUserName());
        map.put("productInfo", res);
        return map;
    }
	/**
	 * 卖家获取V猫商场商品列表
	 * @param token
	 * @param param
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getVcatShopProductList")
	@Cacheable(value = CacheConfig.GET_VCAT_SHOP_PRODUCT_LIST_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public Object getVcatShopProductList(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params",defaultValue="") String param) {
		// 简单验证,保证服务端不会出异常
		JSONObject params = null;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错"+e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String customerId = StringUtils.getCustomerIdByToken(token);
		// 商品类型，all/hot
		String productType = params.getString("productType");
		if (!StringUtils.isEmpty(productType))
			productType = productType.toUpperCase();
		// 商品类别，(recommend/coupon/new/reserve/商品类别id)
		String categoryId = params.getString("categoryId");
		if(StringUtils.isBlank(categoryId)){
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		//子分类id 筛选使用
		String childCategoryId = params.getString("childCategoryId");
		//品牌id 筛选使用
		String brandId = params.getString("brandId");
		
		// 排序字段 ，商品类别为recommend不传
		String sortType = params.getString("sortType");
		if(StringUtils.isBlank(sortType)){
			sortType = ApiConstants.DEFAULT;
		}
		// 猫币专场商品类型
		String couponType = ApiConstants.COUPON_TOTAL;
		int pageNo = params.getIntValue("pageNo");
		int count = 0;
		//推荐个数
		if(ApiConstants.RECOMMENT_CODE.equals(categoryId))
			//count = productService.countRecommendProductList();
			count = productService.countNewProductList();
		//猫币专场
		//else if(ApiConstants.COUPON_CODE.equals(categoryId))
		//	count = productService.countCouponProductList(customerId,couponType);
		//新品
		else if(ApiConstants.NEW_CODE.equals(categoryId))
			count = productService.countNewProductList();
		//预购
		else if(ApiConstants.REVERT_CODE.equals(categoryId))
			count = productService.countReserveProductList();
        //V猫庄园
        else if(ApiConstants.LEROY_CODE.equals(categoryId))
            count = productService.countLeroyProductList();
		// 天天拼团
		else if (ApiConstants.GROUP_BUY_TYPE.equals(categoryId))
			count = productService.countGroupBuyProductList(customerId,"app");
		else 
			count = productService.countCategoryProductList(productType, categoryId, childCategoryId, brandId);
		// 组装分页信息
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();
		List<ProductDto> list                 = new ArrayList<>();
		List<GroupBuyProductDto> groupBuyList = new ArrayList<>();
        Map<String, Object> map               = new HashMap<>();
        Shop shop                             = shopService.get(customerId);
		if(ApiConstants.RECOMMENT_CODE.equals(categoryId)) {
			//list = productService.getRecommendProductList(page);
			list = productService.getNewProductList(page);
            if(shop.getAdvancedShop() == 1){
                for(ProductDto productDto : list){
                    productDto.setSaleEarningFund(productDto.getSaleEarningFund().add(productDto.getBonusEaringFund()));
                }
            }
		}
		//else if(ApiConstants.COUPON_CODE.equals(categoryId))
		//	list = productService.getCouponProductList2(customerId,page);
		else if(ApiConstants.NEW_CODE.equals(categoryId)) {
			list = productService.getNewProductList(page);
            if(shop.getAdvancedShop() == 1){
                for(ProductDto productDto : list){
                    productDto.setSaleEarningFund(productDto.getSaleEarningFund().add(productDto.getBonusEaringFund()));
                }
            }
		}
        //预购 reserve
		else if(ApiConstants.REVERT_CODE.equals(categoryId)){
            list = productService.getReserveProductList(page);
            if(shop.getAdvancedShop() == 1){
                for(ProductDto productDto : list){
                    productDto.setSaleEarningFund(productDto.getSaleEarningFund().add(productDto.getBonusEaringFund()));
                }
            }
            //添加广告图
            map.put("bigImageUrl", QCloudUtils.createOriginalDownloadUrl(productService.getReserveImage()));
        }
        // V猫庄园
        else if(ApiConstants.LEROY_CODE.equals(categoryId))
            list = productService.getLeroyProductList(customerId,page);
		// 天天拼团
		else if(ApiConstants.GROUP_BUY_TYPE.equals(categoryId)){
			groupBuyList = productService.getGroupBuyProductList(customerId,page,"app");

		}
        else {
			list = productService.getCategoryProductList(productType, categoryId, childCategoryId, brandId, sortType, page);
            if(shop.getAdvancedShop() == 1){
                for(ProductDto productDto : list){
                    productDto.setSaleEarningFund(productDto.getSaleEarningFund().add(productDto.getBonusEaringFund()));
                }
            }
		}

		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("page", page);
		if(ApiConstants.GROUP_BUY_TYPE.equals(categoryId)){
			map.put("list",groupBuyList);
		}else
			map.put("list", list);
		map.put("cartCount", cartService.countByBuyerId(customerId));
		return map;
	}
    /**
     * 获取商品的详细信息,卖家从v猫商城,从app点击进入，
     * VIP 用户显示的价格为 零售价-销售分红-一级分红
     * 普通用户显示为 零售价-销售分红
     * @param token
     * @return
     */
    @RequiresRoles("buyer")
    @RequestMapping("/api/getVcatProductDetail")
	@Cacheable(value = CacheConfig.GET_VCAT_PRODUCT_DETAIL_CACHE,
			key = "T(com.vcat.common.utils.StringUtils).getCustomerIdByToken(#token) + T(com.vcat.common.utils.StringUtils).getValueFromJson(#param,'productId')",
			cacheManager = "apiCM")
    public Object getVcatProductDetail(@RequestHeader(value = "token", defaultValue = "") String token,
                                       @RequestParam(value = "params",defaultValue="") String param){
        // 简单验证,保证服务端不会出异常
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(param);
        } catch (Exception e) {
            logger.error("params 出错"+e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        //获取商品详情的类型，1为普通商品,2为拿样商品,3为猫币全额抵扣专场商品 4为购物卷部分抵扣专场商品,5为店主获取我的小店中的商品详情,6为V猫庄园商品(虚拟商品)
        String couponType = params.getString("type");
        if(StringUtils.isBlank(couponType)){
            couponType = ApiConstants.SUPER;
        }
        //if(!ApiConstants.SUPER.equals(couponType) && !couponType.equals(ApiConstants.SHOW_SHOP_INVENTORY) && !couponType.equals(ApiConstants.LEROY)){
			////获取猫币抵扣商品的详情
        //    return getCouponProductDetail(token,param);
        //}
        String productId = params.getString("productId");
        if (StringUtils.isBlank(productId)) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        // 判断用户是否收藏,默认没收藏
        String isCollect = ApiConstants.NO;
        // 购物车数量
        int cartCount = 0;
        // 如果是微信用户，判断用户是否收藏
        String buyerId = StringUtils.getCustomerIdByToken(token);
        //customerService.get(buyerId).getUserName();
        if (!StringUtils.isBlank(buyerId)) {
            Favorites fav = new Favorites();
            Customer buyer = new Customer();
            buyer.setId(buyerId);
            Shop shop = new Shop();
            Product product = new Product();
			GroupBuy groupBuy = new GroupBuy();
            product.setId(productId);
            fav.setProduct(product);
            fav.setCustomer(buyer);
            fav.setShop(shop);
			fav.setGroupBuy(groupBuy);
            fav.setFavType(couponType);
            if (favoritesService.get(fav) != null) {
                isCollect = ApiConstants.YES;
            }
            cartCount = cartService.countByBuyerId(buyerId);
        }
        Product product = productService.getVcatProductDetail(productId,buyerId,couponType);
        if (product == null) {
            return new MsgEntity(ApiMsgConstants.PRODUCT_NOT_FIND,
                    ApiMsgConstants.NOTFIND_CODE);
        }
        Map<String, Object> res = new HashMap<>();
		res.put("isVip", shopService.get(StringUtils.getCustomerIdByToken(token)).getAdvancedShop() == 1);
        res.put("id", product.getId());
        res.put("name", product.getName());
        res.put("productCollectCount", product.getProductCollectCount());
        res.put("saledNum", product.getSaledNum());
        res.put("description", product.getDescription());
        res.put("inventory", product.getInventory());
        res.put("price", product.getPrice());
        res.put("farmWechatNo", product.getFarmWechatNo() == null ? Global.getConfig("service.wechat") : product.getFarmWechatNo());
        res.put("isHot", product.getIsHot()+"");
        res.put("copywrite", product.getCopywrite());
        res.put("isSellerLoad", product.getIsSellerLoad());
		Date startTime = product.getReserveRecommend().getStartTime();
		Date endTime =  product.getReserveRecommend().getEndTime();
		if(startTime == null || endTime == null) {
            res.put("isReserve", false);
            res.put("isReserveOver", false);
            //如果不是预售商品
            res.put("canRefund",product.getCanRefund());
        }
		else {
            res.put("isReserve", true);
            res.put("canRefund",false);
            if(endTime.after(new Date())){
                res.put("isReserveOver", false);
            }else res.put("isReserveOver", true);
        }
        res.put("reserveStartTime", product.getReserveRecommend()
                .getStartTime());
        res.put("reserveEndTime", product.getReserveRecommend().getEndTime());

        res.put("supplier", product.getBrand());
        res.put("category", product.getCategory());

        // 商品规格
        // 根据买家修改价格
        Shop shop = shopService.get(buyerId);
        BigDecimal retailPrice ;
        BigDecimal saleEarning ;
        BigDecimal bonusEarning;

		//Code.Ai (如果是小店店主查看自己小店中的商品详情(不是点预览后查看详情),显示的预售商品库存为小店库存)
        Map<String, Object> itemList = productService.getProductItemList(productId,ApiConstants.SUPER);
        Collection maps =itemList.values();
        for(Object lmap : maps){
            Map<String, Object> ss = (HashMap<String, Object>) lmap;
            if(couponType.equals(ApiConstants.SUPER) || couponType.equals(ApiConstants.SHOW_SHOP_INVENTORY)) {
                retailPrice = (BigDecimal) ss.get("retailPrice");
				res.put("retailPrice", retailPrice);
                saleEarning = (BigDecimal) ss.get("saleEarning");
                bonusEarning = (BigDecimal) ss.get("bonusEarning");
                if (shop.getAdvancedShop() == 1) { //如果是VIP
                    ss.put("finalPrice", retailPrice.subtract(saleEarning).subtract(bonusEarning));
					res.put("finalPrice", retailPrice.subtract(saleEarning).subtract(bonusEarning));
                } else {
                    ss.put("finalPrice", retailPrice.subtract(saleEarning));
					res.put("finalPrice", retailPrice.subtract(saleEarning));
                }
            }
            int isShopProductDetail = params.getInteger("isShopProductDetail");
			// H5调用接口时传 type=5,isShopProductDetail=1
            // 说明是店主查看自己小店商品,如果是预售商品,则显示小店的库存
            //if(couponType.equals(ApiConstants.SHOW_SHOP_INVENTORY) &&
				//	isShopProductDetail == ApiConstants.SHOPERVIEW_CODE &&
				//	product.getReserveRecommend().getId() != null){
            //    int inventory = shopProductService.getInventory(buyerId,productId);
            //    ss.put("inventory",inventory);
            //}
		}


        //是否为虚拟商品(V猫庄园商品)
        res.put("isVirtualProduct",product.getVirtualProduct());
        //添加优惠规则说明
        if(product.getPromotion().getName() != null){
            res.put("promotion",product.getPromotion());
        }
        res.put("itemList", itemList);
        res.put("itemTitleList", productService.getProductItemTitleList(productId));
        res.put("imageList", product.getImageList());
        res.put("propertyList", product.getPropertyList());
        res.put("isCollect", isCollect);
        res.put("saleEarningFund", String.valueOf(product.getSaleEarningFund()));
        res.put("cartCount", cartCount);
        res.put("freightPrice", freightConfigService.getFreightPrice(buyerId,productId));
        res.put("reviewCount", ratingSummaryService.countReviewList(productId));
        res.put("isDownLoad", product.getIsDownLoad());
        res.put("onlineReword", product.getLoadEarning().getFund());
        res.put("onlineRewordBottomLine", product.getLoadEarning().getConvertFund());
        product.setReserveRecommend(product.getReserveRecommend().getId() == null ? null
                : product.getReserveRecommend());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("productInfo", res);
        map.put("shopName", customerService.get(buyerId).getUserName());
        return map;
    }
}