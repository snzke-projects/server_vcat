package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.push.PushService;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.excel.annotation.ExcelField;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户(买家)Entity
 */
public class Customer extends DataEntity<Customer> {
	private static final long serialVersionUID = 1L;
	private String phoneNumber;	// 手机号
	private Integer registered;	// 是否注册,用户类型 (0:微信注册,1:app注册,2:邀请注册,3:除微信外的注册)
	private String userName;	// 用户名
	private String password;	// 密码
	private String email;		// 邮箱
	private String avatarUrl;	// 头像地址
	private boolean deactivated;// 是否无效
	private List<Phone> phoneList;  // 电话集合
	private List<ThirdPartyLogin> thirdPartyLoginList;// 第三方登录方式
	private List<Address> addressList;      // 地址集合
	private List<Favorites> favoriteList;   // 收藏集合
	private Integer favoritesCount;
	private List<CustomerRole> roleList;    // 角色集合(区别买家或者是卖家)
	private Shop shop;			// 该客户的店铺
	private List<Order> orderList;  // 买家订单集合
	private String idCard;      // 身份证号
	private Integer deviceType; // 设备类型 ，3为android，4为ios
	private String pushToken;   // 消息推送的token
	private String orderCount;	// 订单数量
	private String refundTimes;	// 退款次数
	private BigDecimal purchasesAmount;	// 购买额
    private Boolean hasWithdrawal;  // 是否拥有提现记录
    private Boolean hasReturn;  // 是否包含退货
    private Boolean hasTeamMember;  // 是否拥有团队成员

    @ExcelField(title="注册来源", align=2, sort=30)
    public String getFrom(){
        if(null != registered && registered == 0){
            return "微信注册";
        }else if(null != registered && registered == 1){
            return "客户端注册";
        }else if(null != registered && registered == 2){
            return "网页接收邀请";
        }

        return "未知途径";
    }

    public Boolean getHasWithdrawal() {
        return hasWithdrawal;
    }

    public void setHasWithdrawal(Boolean hasWithdrawal) {
        this.hasWithdrawal = hasWithdrawal;
    }

    public Boolean getHasReturn() {
        return hasReturn;
    }

    public void setHasReturn(Boolean hasReturn) {
        this.hasReturn = hasReturn;
    }

    public Boolean getHasTeamMember() {
        return hasTeamMember;
    }

    public void setHasTeamMember(Boolean hasTeamMember) {
        this.hasTeamMember = hasTeamMember;
    }

    public boolean isAndroid(){
		return null != deviceType && PushService.ANDROID == deviceType;
	}
	public boolean isIOS(){
		return null != deviceType && PushService.IOS == deviceType;
	}

    @ExcelField(title="用户名", align=2, sort=10)
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAvatarUrl() {
		if(avatarUrl!=null&&avatarUrl.contains("http://")){
			return avatarUrl;
		}else
			return QCloudUtils.createThumbDownloadUrl(avatarUrl,ApiConstants.DEFAULT_AVA_THUMBSTYLE);
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public boolean isDeactivated() {
		return deactivated;
	}
	public void setDeactivated(boolean deactivated) {
		this.deactivated = deactivated;
	}
	public List<Phone> getPhoneList() {
		return phoneList;
	}
	public void setPhoneList(List<Phone> phoneList) {
		this.phoneList = phoneList;
	}
	public List<ThirdPartyLogin> getThirdPartyLoginList() {
		return thirdPartyLoginList;
	}
	public void setThirdPartyLoginList(List<ThirdPartyLogin> thirdPartyLoginList) {
		this.thirdPartyLoginList = thirdPartyLoginList;
	}
	public List<Address> getAddressList() {
		return addressList;
	}
	public void setAddressList(List<Address> addressList) {
		this.addressList = addressList;
	}
	public List<Favorites> getFavoriteList() {
		return favoriteList;
	}
	public void setFavoriteList(List<Favorites> favoriteList) {
		this.favoriteList = favoriteList;
	}
	public List<CustomerRole> getRoleList() {
		return roleList;
	}
	public void setRoleList(List<CustomerRole> roleList) {
		this.roleList = roleList;
	}
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	public List<Order> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}
    @ExcelField(title="手机号", align=2, sort=20)
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public Integer getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}
	public String getPushToken() {
		return pushToken;
	}
	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}
	public Integer getFavoritesCount() {
		return favoritesCount;
	}
	public void setFavoritesCount(Integer favoritesCount) {
		this.favoritesCount = favoritesCount;
	}

	public String getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(String orderCount) {
		this.orderCount = orderCount;
	}

	public String getRefundTimes() {
		return refundTimes;
	}

	public void setRefundTimes(String refundTimes) {
		this.refundTimes = refundTimes;
	}

	public BigDecimal getPurchasesAmount() {
		return purchasesAmount;
	}

	public void setPurchasesAmount(BigDecimal purchasesAmount) {
		this.purchasesAmount = purchasesAmount;
	}
	public Integer getRegistered() {
		return registered;
	}
	public void setRegistered(Integer registered) {
		this.registered = registered;
	}
}
