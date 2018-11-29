package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 店铺
 */
public class Shop extends DataEntity<Shop> {
	private static final long serialVersionUID = 1L;
	private String name;			// 店铺名称
	private Integer confirmReceipt;	// 自动确认收货时间(小时)(在确认收货之后开始计算)
	private Integer autoOff;		// 订单自动关闭时间
	private String color;			// 店铺背景颜色
	private String bgUrl;			// 店铺背景图片
	private Customer customer;		// 卖家
	private String parentId;        // 上级卖家（推荐人）
	private String parentName;      // 上级卖家名字
	private Integer exp = 0;			// 店铺当前经验值
	private String shopNum;         // 店铺编号
	private Level level;			// 店铺等级
	private List<ExpLog> expLogList;// 经验操作日志
	private List<Account> accounts;	// 提现账户
	private ShopFund fund;			// 店铺资金
	private List<ShopFundLog> fundLogList;	// 资金操作日志
	private List<Product> productList;	// 店铺上架商品列表
	private List<Order> orderList;	// 店铺所有订单集合
    private Integer isRecommend;    // 是否为店铺达人
    private ShopGuru guru;          // 店铺达人
	private InviteCode myInviteCode;	  // 我的邀请码
	private InviteCode usedInviteCode;	// 被邀请时使用的邀请码
    private String myInviteCodeId;  // 店铺的邀请码(邀请别人时使用)
    private String usedInviteCodeId;// 被邀请时使用的邀请码
	private Integer isAdvancedShop = 0; // 是否为高级店铺 //0:未升级 1:已升级 2:可升级
	private UpgradeRequest upgradeRequest;  // 升级申请

    public InviteCode getMyInviteCode() {
        return myInviteCode;
    }

    public void setMyInviteCode(InviteCode myInviteCode) {
        this.myInviteCode = myInviteCode;
    }

    public InviteCode getUsedInviteCode() {
        return usedInviteCode;
    }

    public void setUsedInviteCode(InviteCode usedInviteCode) {
        this.usedInviteCode = usedInviteCode;
    }

    public UpgradeRequest getUpgradeRequest() {
		return upgradeRequest;
	}

	public void setUpgradeRequest(UpgradeRequest upgradeRequest) {
		this.upgradeRequest = upgradeRequest;
	}

	public Integer getAdvancedShop() {
		return isAdvancedShop == null ? 0 : isAdvancedShop;
	}

	public void setAdvancedShop(Integer isAdvancedShop) {
		this.isAdvancedShop = isAdvancedShop;
	}

	public String getMyInviteCodeId() {
		return myInviteCodeId;
	}

	public void setMyInviteCodeId(String myInviteCodeId) {
		this.myInviteCodeId = myInviteCodeId;
	}

	public String getUsedInviteCodeId() {
		return usedInviteCodeId;
	}

	public void setUsedInviteCodeId(String usedInviteCodeId) {
		this.usedInviteCodeId = usedInviteCodeId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getConfirmReceipt() {
		return confirmReceipt;
	}
	public void setConfirmReceipt(Integer confirmReceipt) {
		this.confirmReceipt = confirmReceipt;
	}
	public Integer getAutoOff() {
		return autoOff;
	}
	public void setAutoOff(Integer autoOff) {
		this.autoOff = autoOff;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getBgUrl() {
		return bgUrl;
	}
	public void setBgUrl(String bgUrl) {
		this.bgUrl = bgUrl;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Integer getExp() {
		return exp;
	}
	public void setExp(Integer exp) {
		this.exp = exp;
	}
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level) {
		this.level = level;
	}

    public Integer getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(Integer isRecommend) {
        this.isRecommend = isRecommend;
    }

    public List<ExpLog> getExpLogList() {
		return expLogList;
	}
	public void setExpLogList(List<ExpLog> expLogList) {
		this.expLogList = expLogList;
	}
	public ShopFund getFund() {
		return fund;
	}
	public void setFund(ShopFund fund) {
		this.fund = fund;
	}
	public List<ShopFundLog> getFundLogList() {
		return fundLogList;
	}
	public void setFundLogList(List<ShopFundLog> fundLogList) {
		this.fundLogList = fundLogList;
	}
	public List<Product> getProductList() {
		return productList;
	}
	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}
	public List<Order> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}

	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public List<Account> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	public String getShopNum() {
		return shopNum;
	}
	public void setShopNum(String shopNum) {
		this.shopNum = shopNum;
	}

    public ShopGuru getGuru() {
        return guru;
    }

    public void setGuru(ShopGuru guru) {
        this.guru = guru;
    }

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.JSON_STYLE);
	}
}
