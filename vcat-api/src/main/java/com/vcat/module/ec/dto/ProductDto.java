package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.utils.DateUtils;
import com.vcat.module.ec.entity.Copywrite;

public class ProductDto implements Serializable {
	private static final long serialVersionUID = 7572315945052097294L;
	private String shopId;
	private String id;
	private String groupBuyId;
	private String name;
	private Integer inventory;
	private Integer saledNum;
	private Integer shelves;
	private Integer collection;
	private BigDecimal price;
	private Boolean isSellerLoad ;//卖家是否下架(单词用错！unload!)
	private String mainUrl;
	private BigDecimal saleEarningFund;
	private BigDecimal bonusEaringFund;
	private BigDecimal shareEarningFund;
	private BigDecimal loadEarningFund;
	private Boolean isHot;
	private Boolean isReserve;
	private Boolean isRecommend;
	private Date reserveStartTime;
	private Date reserveEndTime;
	private String copywrite;
	private String copywriteTitle;
	private Date updateTime;
    private String logo;
	private BigDecimal couponValue;
	private BigDecimal externalPrice;//外部价格，用于对比价格显示
	private Integer freeShipping;    // 免邮标识(1：免邮|2：不免邮)
	private Boolean isReserveOver;   // 预售是否结束
	private Boolean isGroupBuyProduct; // 是否为团购商品
	private BigDecimal groupPrice;
	private Integer neededPeople;

	public Integer getNeededPeople() {
		return neededPeople;
	}

	public void setNeededPeople(Integer neededPeople) {
		this.neededPeople = neededPeople;
	}

	public BigDecimal getGroupPrice() {
		return groupPrice;
	}

	public void setGroupPrice(BigDecimal groupPrice) {
		this.groupPrice = groupPrice;
	}

	public String getGroupBuyId() {
		return groupBuyId;
	}

	public void setGroupBuyId(String groupBuyId) {
		this.groupBuyId = groupBuyId;
	}

	public Boolean getGroupBuyProduct() {
		return isGroupBuyProduct;
	}

	public void setGroupBuyProduct(Boolean groupBuyProduct) {
		isGroupBuyProduct = groupBuyProduct;
	}

	private List<CopywriteDto> copywriteList = new ArrayList<>();// 文案

	public List<CopywriteDto> getCopywriteList() {
		return copywriteList;
	}

	public void setCopywriteList(List<CopywriteDto> copywriteList) {
		this.copywriteList = copywriteList;
	}

	public BigDecimal getBonusEaringFund() {
		return bonusEaringFund;
	}
	public void setBonusEaringFund(BigDecimal bonusEaringFund) {
		this.bonusEaringFund = bonusEaringFund;
	}

	public Boolean getIsReserveOver() {
        return isReserveOver;
    }

    public void setIsReserveOver(Boolean isReserveOver) {
        this.isReserveOver = isReserveOver;
    }

    public Boolean getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(Boolean isRecommend) {
        this.isRecommend = isRecommend;
    }

    public Integer getFreeShipping() {
		return freeShipping;
	}

	public void setFreeShipping(Integer freeShipping) {
		this.freeShipping = freeShipping;
	}

	public BigDecimal getExternalPrice() {
		return externalPrice;
	}

	public void setExternalPrice(BigDecimal externalPrice) {
		this.externalPrice = externalPrice;
	}

	public ArrayNode getHotZoneImages() {
		ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
		if(hotZoneImages != null){
			String[] urls = hotZoneImages.split(",");
			for(String url : urls){
				arrayNode.add(QCloudUtils.createOriginalDownloadUrl(url));
			}
		}
		return arrayNode;
	}

	public void setHotZoneImages(String hotZoneImages) {
		this.hotZoneImages = hotZoneImages;
	}

	private String hotZoneImages;
	private String productImages;

	public ArrayNode getProductImages() {
		ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
		if(productImages != null){
			String[] urls = productImages.split(",");
			for(String url : urls){
				arrayNode.add(QCloudUtils.createOriginalDownloadUrl(url));
			}
		}
		return arrayNode;
	}

	public void setProductImages(String productImages) {
		this.productImages = productImages;
	}
	private Date takeStartTime;//拿样后开始上架时间
	private Date takeEndTime;//拿样后必须下架时间
	private String productType;
	
	public Date getReserveStartTime() {
		return reserveStartTime;
	}
	public void setReserveStartTime(Date reserveStartTime) {
		this.reserveStartTime = reserveStartTime;
	}
	public Date getReserveEndTime() {
		return reserveEndTime;
	}
	public void setReserveEndTime(Date reserveEndTime) {
		this.reserveEndTime = reserveEndTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getInventory() {
		return inventory;
	}
	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}
	public Integer getSaledNum() {
		return saledNum;
	}
	public void setSaledNum(Integer saledNum) {
		this.saledNum = saledNum;
	}
	public String getMainUrl() {
		return QCloudUtils.createThumbDownloadUrl(mainUrl, 400);
	}
	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}
	public BigDecimal getSaleEarningFund() {
		return saleEarningFund;
	}
	public void setSaleEarningFund(BigDecimal saleEarningFund) {
		this.saleEarningFund = saleEarningFund;
	}
	public BigDecimal getShareEarningFund() {
		return shareEarningFund;
	}
	public void setShareEarningFund(BigDecimal shareEarningFund) {
		this.shareEarningFund = shareEarningFund;
	}
	public BigDecimal getLoadEarningFund() {
		return loadEarningFund;
	}
	public void setLoadEarningFund(BigDecimal loadEarningFund) {
		this.loadEarningFund = loadEarningFund;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public Date getTakeStartTime() {
		return takeStartTime;
	}
	public void setTakeStartTime(Date takeStartTime) {
		this.takeStartTime = takeStartTime;
	}
	public Date getTakeEndTime() {
		return takeEndTime;
	}
	public void setTakeEndTime(Date takeEndTime) {
		this.takeEndTime = takeEndTime;
	}
	public Integer getCollection() {
		return collection;
	}
	public void setCollection(Integer collection) {
		this.collection = collection;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
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
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Boolean getIsSellerLoad() {
		return isSellerLoad;
	}
	public void setIsSellerLoad(Boolean isSellerLoad) {
		this.isSellerLoad = isSellerLoad;
	}
	public Boolean getIsHot() {
		return isHot;
	}
	public void setIsHot(Boolean isHot) {
		this.isHot = isHot;
	}
	public Boolean getIsReserve() {
		return isReserve;
	}
	public void setIsReserve(Boolean isReserve) {
		this.isReserve = isReserve;
	}
	public Integer getShelves() {
		return shelves;
	}
	public void setShelves(Integer shelves) {
		this.shelves = shelves;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCopywriteTitle() {
		return copywriteTitle;
	}

	public void setCopywriteTitle(String copywriteTitle) {
		this.copywriteTitle = copywriteTitle;
	}

    public String getLogo() {
        return QCloudUtils.createThumbDownloadUrl(logo, 400);
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDeadTime() {
		if (reserveEndTime != null)
			if(new Date().before(reserveEndTime)){
				return DateUtils.getDeadTime(reserveEndTime);
			}else{
				return "预售已结束";
			}

		return null;
	}
}
