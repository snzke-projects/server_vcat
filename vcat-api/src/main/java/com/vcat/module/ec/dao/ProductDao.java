package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.GroupBuyProductDto;
import com.vcat.config.CacheConfig;
import com.vcat.module.ec.dto.ProductDto;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductItem;
import com.vcat.module.ec.entity.ProductPerformanceLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface ProductDao extends CrudDao<Product> {
    void deleteHotRecommend(Product product);
    void insertHotRecommend(Product product);
    void archived(@Param(value = "id")String id,@Param(value = "archived")Integer archived);
	//接口 买家获取商品个数
	int countSellerProductList(@Param("productType")String productType, @Param("shopId")String customerId, @Param("categoryId")String categoryIds);
	// 买家获取详细信息
	Product getSellerProductDetail(@Param("shopId") String shopId, @Param("productId") String productId, @Param("couponType")String couponType);
	//接口 买家获取商品列表
	List<ProductDto> getSellerProductList(Map<String, Object> map);

    /**
     * 根据ID数组获取商品集合
     * @param ids
     * @return
     */
    List<Product> findListByIds(String[] ids);

    /**
     * 自动上架
     * @param product
     * @return
     */
    Integer autoLoadByForecast(Product product);
    //更新收藏数量
	void updateCollectCount(@Param("productId")String productId, @Param("count")int count);
	//更新商品的销售额
	void updateSales(@Param("productId")String productId, @Param("sales")int quantity);
	//卖家获取拿样商品数量
	int countCategoryProductList(Map<String, Object> map);
	//卖家获取拿样商品列表
	List<ProductDto> getCategoryProductList(Map<String, Object> map);
	//获取商品的基本信息
	ProductDto getProduct(String productId);
    Boolean isVirtualProduct(@Param("productItemId")String productItemId);
	//接口 卖家获取商品个数
	int countShopProductList(Map<String, Object> map);
    int countShopProductList2(Map<String, Object> map);
	//接口 卖家获取商品列表
	List<ProductDto> getShopProductList(Map<String, Object> map);
    List<ProductDto> getShopProductList2(Map<String, Object> map);
	//接口  获取V猫商场商品详情
	Product getVcatProductDetail(@Param("productId")String productId, @Param("shopId")String shopId,@Param("couponType")String couponType);
	
	void updateShelves(String productId);

    /**
     * 下架小店商品
     * @param productId
     * @param shopId
     */
    void archivedShopProduct(@Param("productId")String productId, @Param("shopId")String shopId);

    /**
     * 查看该品牌与商品分类是否已存在
     * @param brandId
     * @param categoryId
     * @return
     */
    boolean hasBrandCategory(@Param(value="brandId")String brandId,@Param(value="categoryId")String categoryId);

    /**
     * 插入品牌与商品分类关系表
     * @param id
     * @param brandId
     * @param categoryId
     */
    void insertBrandCategory(@Param(value="id")String id,@Param(value="brandId")String brandId,@Param(value="categoryId")String categoryId);

    /**
     * 检查是否需要删除品牌与商品分类关系表
     * @param brandId
     * @param categoryId
     * @return
     */
    Boolean needDeleteBrandCategory(@Param(value="brandId")String brandId,@Param(value="categoryId")String categoryId);
    /**
     * 鍒犻櫎鍝佺墝涓庡晢鍝佸垎绫诲叧绯�
     * @param brandId
     * @param categoryId
     */
    void deleteBrandCategory(@Param(value="brandId")String brandId,@Param(value="categoryId")String categoryId);

	//int countCouponProductList(@Param(value="shopId")String customerId, @Param(value="couponType")String couponType);
	
    //List<ProductDto> getCouponProductList(@Param(value="shopId")String customerId, @Param(value="couponType")String couponType,
		//	@Param(value="sortType")String sortType, @Param(value="page")Pager page);
    //
    ////获取猫币专项列表修改后将全部折扣和部分折扣合并在一起
    //List<ProductDto> getCouponProductList2(@Param(value="shopId")String customerId, @Param(value="page")Pager page);
    /**
     * 批量保存商品排序
     * @param id 商品ID数组
     * @param displayOrder 商品排序数组
     */
    void updateOrder(@Param(value = "id")String id, @Param(value = "displayOrder")String displayOrder);

    /**
     * 恢复已删除的商品
     * @param product
     */
    void recover(Product product);

    /**
     * 查询该商品是否可上架
     * @param id 商品ID
     * @return
     */
    Boolean getCanBeArchived(String id);
    
	int countRecommendProductList();
	
	List<ProductDto> getRecommendProductList(@Param("page")Pager page);
	
	int countNewProductList();
	
	List<ProductDto> getNewProductList(@Param("page")Pager page);
	
	int countReserveProductList();
	
	List<ProductDto> getReserveProductList(@Param("page")Pager page);
    String getReserveImage();
    /**
     * 查询V猫庄园数量
     */
    int countLeroyProductList();
    /**
     * 查询V猫庄园商品
     */
    @Cacheable(value = CacheConfig.GET_LEROY_PRODUCT_LIST_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
    List<ProductDto> getLeroyProductList(@Param("page")Pager page);
    /**
     * 置顶商品
     * @param product
     */
    void stick(Product product);

    /**
     * 添加商品销售及上架业绩
     * @param entity
     */
    void addPerformance(ProductPerformanceLog entity);

    int countHotZoneProductList();
    List<ProductDto> getHotZoneProductList(@Param("page")Pager page, @Param("shopId")String shopId);

	boolean setShopProductRecommendByType(@Param("productId")String productId,@Param("shopId")String shopId,@Param("type")String type);
    int countRecommendShopProduct(@Param("shopId")String shopId);
    // 判断商品是否为预售商品
    Boolean isReserveProduct(@Param("productItemId")String productItemId);
    // 根据支付单号查询商品列表
    Map<String, Object> getProductsByPaymentId(@Param("paymentId")String paymentId);
    //根据商品ID查询是否为预售商品
    Boolean isReserveType(@Param("productId")String productId);
    // 判断商品是否为全部猫币抵扣还是部分猫币抵扣 部分抵扣为4 全部抵扣为3
    //Integer CouponUsable(@Param("parductId") String parductId);

    // 天天拼团(商城)
    int countGroupBuyProductList(Map<String, Object> requestMap);
    // 天天拼团(小店)
    int countSellerGroupBuyProductList(@Param("shopId")String shopId);
    List<GroupBuyProductDto> getGroupBuyProductList(Map<String, Object> requestMap);
    List<GroupBuyProductDto> getSellerGroupBuyProductList(Map<String, Object> requestMap);

    Product getGroupBuyProductDetail(@Param("shopId") String shopId, @Param("groupBuyId")String groupBuyId,
                                         @Param("type")Integer type);
}
