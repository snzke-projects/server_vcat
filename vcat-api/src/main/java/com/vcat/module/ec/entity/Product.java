package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 商品Entity
 */
public class Product extends DataEntity<Product> {
	@Override
	public String toString() {
		return this.id;
	}

	private static final long serialVersionUID = 1L;
	public static final String RECOMMEND_HOT = "HOT";
	private Brand brand; 			    // 品牌
	private String name;				// 名称
	private String description;			// 图文详情HTML
	private ProductCategory category;	// 所属分类
	private Integer clicks;				// 点击量
	private Level needLevel;			// 上架所需等级
	private Date addDate;				// 添加时间
	private Integer displayOrder;		// 排序
	private Integer archived;			// 是否下架
	private LoadEarning loadEarning = new LoadEarning();	// 上架奖励
	//同一时段只有一个分享奖励  cw 
	private ShareEarning shareEarning = new ShareEarning();//此时段用户是否有分享奖励
	private int inventory;			// 库存
	private String price;				// 商品售价范围
	private String productImages;		// 商品图片地址
    private String productHotSaleImages;// 爆品图片地址
	private String farmWechatNo;        // 庄园服务微信号

	private BigDecimal couponValue;//可使用购物卷金额
	private BigDecimal freightPrice;//快递费

	private Integer isHot;				// 是否热销
    private Integer isHotSale;			// 是否爆品
    private Date hotSaleSetDate;        // 爆品设置时间
    private Integer isNew;				// 是否新品
	private String hotRecommendId;		// 热销推荐ID
	private ReserveRecommend reserveRecommend = new ReserveRecommend();	// 限时预购
	private SaleRecommend saleRecommend = new SaleRecommend();// 限时打折
	private Promotion promotion = new Promotion();// 优惠卷

	private List<ProductItem> itemList; 	// 规格集合
	private List<ProductImage> imageList;	// 图片集合
    private List<ProductHotSaleImage> hotSaleImageList;// 爆品图片集合
	private List<ShareEarning> shareEarningList;// 分享奖励集合
	private List<ProductProperty> propertyList;	// 商品属性集合

	private int productCollectCount;
	private int saledNum;               // 销量
	private int shelves;//上架次数
	private String isDownLoad;          // 是否下架
	
	private Integer isAutoLoad;			// 是否自动上架
	private Date autoLoadDate;			// 自动上架时间

	private String[] specNameArray;		// 规格属性名

	private String saleEarning;			// 销售奖励显示

    private String copywriteTitle;      // 推广文案标题
    private String copywrite;           // 推广文案

    private Integer retailUsable;       // 是否可在普通商品区使用
    private Integer couponAllUsable;    // 是否可用全部券购买
    private Integer couponPartUsable;   // 是否可用部分劵购买
    private String isSellerLoad;
    private String saleEarningFund;
	private String title;

    private Integer freeShipping;       // 免邮标识(1：免邮|0：不免邮)
    private ExpressTemplate expressTemplate;    // 邮费模板
    private Distribution distribution;  // 配送方
    private Topic topic;                // 所属专题
    private BigDecimal externalPrice;   // 外部参考价格
	private Boolean canRefund;			// 是否可退货
	private Boolean virtualProduct;	    // 是否为虚拟商品

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getVirtualProduct() {
		return virtualProduct;
	}

	public void setVirtualProduct(Boolean virtualProduct) {
		this.virtualProduct = virtualProduct;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public Boolean getCanRefund() {
		return canRefund;
	}

	public void setCanRefund(Boolean canRefund) {
		this.canRefund = canRefund;
	}

	public String getFarmWechatNo() {
		return farmWechatNo;
	}

	public void setFarmWechatNo(String farmWechatNo) {
		this.farmWechatNo = farmWechatNo;
	}

	public String getProductImagesPath() {
		if(null == imageList || imageList.isEmpty()){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for(ProductImage productImage : imageList){
			sb.append("|");
			sb.append(QCloudUtils.createOriginalDownloadUrl(productImage.getUrl()));
		}
		if(sb.length() > 0){
			sb.delete(0,1);
		}
		return sb.toString();
	}

    public String getProductHotSaleImagesPath() {
        if(null == hotSaleImageList || hotSaleImageList.isEmpty()){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for(ProductHotSaleImage productHotSaleImage : hotSaleImageList){
            sb.append("|");
            sb.append(QCloudUtils.createOriginalDownloadUrl(productHotSaleImage.getUrl()));
        }
        if(sb.length() > 0){
            sb.delete(0,1);
        }
        return sb.toString();
    }

	public String getShow(){
		String show = "";
		if(null != category && StringUtils.isNotEmpty(category.getName())){
			show += category.getName() + " - ";
		}
		show += name;
		return show;
	}

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public Integer getClicks() {
		return clicks;
	}

	public void setClicks(Integer clicks) {
		this.clicks = clicks;
	}

	public Level getNeedLevel() {
		return needLevel;
	}

	public void setNeedLevel(Level needLevel) {
		this.needLevel = needLevel;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Integer getArchived() {
		if(null == archived){
			return 1;
		}
		return archived;
	}

	public void setArchived(Integer archived) {
		this.archived = archived;
	}

	public LoadEarning getLoadEarning() {
		return loadEarning;
	}

	public void setLoadEarning(LoadEarning loadEarning) {
		this.loadEarning = loadEarning;
	}


	public String getPrice() {
        return StringUtils.merge(price);
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getProductImages() {
		return productImages;
	}

	public void setProductImages(String productImages) {
		this.productImages = productImages;
	}

    public String getProductHotSaleImages() {
        return productHotSaleImages;
    }

    public void setProductHotSaleImages(String productHotSaleImages) {
        this.productHotSaleImages = productHotSaleImages;
    }

    public Integer getIsHot() {
		if(null == isHot){
			return 0;
		}
		return isHot;
	}

	public void setIsHot(Integer isHot) {
		this.isHot = isHot;
	}

    public Integer getIsHotSale() {
        if(null == isHotSale){
            return 0;
        }
        return isHotSale;
    }

    public void setIsHotSale(Integer isHotSale) {
        this.isHotSale = isHotSale;
    }

    public Date getHotSaleSetDate() {
        if(getIsHotSale() == 0){
            return null;
        }
        return null == hotSaleSetDate ? new Date() : hotSaleSetDate;
    }

    public void setHotSaleSetDate(Date hotSaleSetDate) {
        this.hotSaleSetDate = hotSaleSetDate;
    }

    public Integer getIsNew() {
        if(null == isNew){
            return 0;
        }
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public String getHotRecommendId() {
		return hotRecommendId;
	}

	public void setHotRecommendId(String hotRecommendId) {
		this.hotRecommendId = hotRecommendId;
	}

	public ReserveRecommend getReserveRecommend() {
		return reserveRecommend;
	}

	public void setReserveRecommend(ReserveRecommend reserveRecommend) {
		this.reserveRecommend = reserveRecommend;
	}

	public SaleRecommend getSaleRecommend() {
		return saleRecommend;
	}

	public void setSaleRecommend(SaleRecommend saleRecommend) {
		this.saleRecommend = saleRecommend;
	}

	public List<ProductItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<ProductItem> itemList) {
		this.itemList = itemList;
	}

	public List<ProductImage> getImageList() {
		return imageList;
	}

	public void setImageList(List<ProductImage> imageList) {
		this.imageList = imageList;
	}

    public List<ProductHotSaleImage> getHotSaleImageList() {
        return hotSaleImageList;
    }

    public void setHotSaleImageList(List<ProductHotSaleImage> hotSaleImageList) {
        this.hotSaleImageList = hotSaleImageList;
    }

    public List<ShareEarning> getShareEarningList() {
		return shareEarningList;
	}

	public void setShareEarningList(List<ShareEarning> shareEarningList) {
		this.shareEarningList = shareEarningList;
	}

	public List<ProductProperty> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<ProductProperty> propertyList) {
		this.propertyList = propertyList;
	}

	public ShareEarning getShareEarning() {
		return shareEarning;
	}

	public void setShareEarning(ShareEarning shareEarning) {
		this.shareEarning = shareEarning;
	}
	public int getProductCollectCount() {
		return productCollectCount;
	}
	public void setProductCollectCount(int productCollectCount) {
		this.productCollectCount = productCollectCount;
	}
	public int getSaledNum() {
		return saledNum;
	}
	public void setSaledNum(int saledNum) {
		this.saledNum = saledNum;
	}

	public Integer getIsAutoLoad() {
		if(null == isAutoLoad){
			return 0;
		}
		return isAutoLoad;
	}

	public void setIsAutoLoad(Integer isAutoLoad) {
		this.isAutoLoad = isAutoLoad;
	}

	public Date getAutoLoadDate() {
		return autoLoadDate;
	}

	public void setAutoLoadDate(Date autoLoadDate) {
		this.autoLoadDate = autoLoadDate;
	}
	public String getIsDownLoad() {
		return isDownLoad;
	}
	public void setIsDownLoad(String isDownLoad) {
		this.isDownLoad = isDownLoad;
	}

	public String[] getSpecNameArray() {
		return specNameArray;
	}

	public void setSpecNameArray(String[] specNameArray) {
		this.specNameArray = specNameArray;
	}

	public String getSaleEarning() {
        return StringUtils.merge(saleEarning);
	}

	public void setSaleEarning(String saleEarning) {
		this.saleEarning = saleEarning;
	}

	public BigDecimal getFreightPrice() {
		return freightPrice;
	}
	public void setFreightPrice(BigDecimal freightPrice) {
		this.freightPrice = freightPrice;
	}

    public Integer getRetailUsable() {
        if(null == retailUsable){
            return 1;
        }
		return retailUsable;
	}
	public void setRetailUsable(Integer retailUsable) {
		this.retailUsable = retailUsable;
	}
	public Integer getCouponAllUsable() {
        if(null == couponAllUsable){
            return 0;
        }
		return couponAllUsable;
	}
	public void setCouponAllUsable(Integer couponAllUsable) {
		this.couponAllUsable = couponAllUsable;
	}
	public Integer getCouponPartUsable() {
        if(null == couponPartUsable){
            return 0;
        }
		return couponPartUsable;
	}
	public void setCouponPartUsable(Integer couponPartUsable) {
		this.couponPartUsable = couponPartUsable;
	}

    public String getCopywrite() {
        return copywrite;
    }

    public void setCopywrite(String copywrite) {
        this.copywrite = copywrite;
    }
	public BigDecimal getCouponValue() {
		return couponValue;
	}
	public void setCouponValue(BigDecimal couponValue) {
		this.couponValue = couponValue;
	}
	public int getShelves() {
		return shelves;
	}
	public void setShelves(int shelves) {
		this.shelves = shelves;
	}
	public int getInventory() {
		return inventory;
	}
	public void setInventory(int inventory) {
		this.inventory = inventory;
	}
	public String getIsSellerLoad() {
		return isSellerLoad;
	}
	public void setIsSellerLoad(String isSellerLoad) {
		this.isSellerLoad = isSellerLoad;
	}
	public String getSaleEarningFund() {
		return saleEarningFund;
	}
	public void setSaleEarningFund(String saleEarningFund) {
		this.saleEarningFund = saleEarningFund;
	}

    public Integer getFreeShipping() {
        if(null == freeShipping){
            return 0;
        }
        return freeShipping;
    }

    public void setFreeShipping(Integer freeShipping) {
        this.freeShipping = freeShipping;
    }

    public ExpressTemplate getExpressTemplate() {
        return expressTemplate;
    }

    public void setExpressTemplate(ExpressTemplate expressTemplate) {
        this.expressTemplate = expressTemplate;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public BigDecimal getExternalPrice() {
        return externalPrice;
    }

    public void setExternalPrice(BigDecimal externalPrice) {
        this.externalPrice = externalPrice;
    }

    public String getCopywriteTitle() {
        return copywriteTitle;
    }

    public void setCopywriteTitle(String copywriteTitle) {
        this.copywriteTitle = copywriteTitle;
    }
}