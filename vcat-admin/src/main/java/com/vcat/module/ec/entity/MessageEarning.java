package com.vcat.module.ec.entity;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 收入消息
 */
public class MessageEarning extends DataEntity<MessageEarning> {
    private static final long serialVersionUID = 1L;
    public static final String TYPE_LOAD = "1";         // 代理
    public static final String TYPE_SALE = "2";         // 销售
    public static final String TYPE_SHARE = "3";        // 分享
    public static final String TYPE_WITHDRAWAL = "4";   // 提现
    public static final String TYPE_BONUS = "5";        // 分红
    public static final String TYPE_INVITE = "6";       // 上家邀请劵
    public static final String TYPE_TEAM = "7";         // 下家邀请劵
    public static final String TYPE_BONUS_PARENT = "8"; // 二级分红
    public static final String TYPE_UPGRADE = "9";      // 伯乐奖励
    public static final String TYPE_TEAM_BONUS = "10";  // 团队分红
    private Shop shop;          // 消息所属小店
    private boolean isRead;     // 是否已读
    private Date readDate;      // 阅读时间
    private String consumer;    // 收入提供者（可为买家或V猫）
    private BigDecimal earning; // 收入金额
    private String productName; // 收入来源商品
    private Integer quantity;   // 收入来源商品销售数量
    private String orderNumber; // 订单号
    private String bonusLevel;  // 分红级别
    private String type;        // 收入类型 1 代理 2 销售 3 分享 4 提现 5 分红 6 上家邀请劵 7 下家邀请劵 8 二级分红 9 伯乐奖励
    private String shareName;   // 分享活动名称

    public MessageEarning(){}

    public MessageEarning(Shop shop, String consumer, BigDecimal earning, String productName, Integer quantity, String orderNumber, String type) {
        this.shop = shop;
        this.isRead = false;
        this.consumer = StringUtils.isBlank(consumer) ? "V猫" : consumer;
        this.earning = earning;
        this.productName = productName;
        this.quantity = quantity;
        this.orderNumber = orderNumber;
        this.type = type;
    }

    public static MessageEarning create(Shop shop, String consumer, BigDecimal earning, String productName, Integer quantity, String orderNumber, String type){
        return new MessageEarning(shop, consumer, earning, productName, quantity, orderNumber, type);
    }

    public String getMessageTitle(){
        if(TYPE_LOAD.equals(type)){
            return "喵，您有" + earning.toString() + "元代理奖励到账！";
        }else if(TYPE_SALE.equals(type)){
            return "喵，您有" + earning.toString() + "元销售收入到账！";
        }else if(TYPE_SHARE.equals(type)){
            return "喵，您有" + earning.toString() + "元分享奖励到账！";
        }else if(TYPE_WITHDRAWAL.equals(type)){
            return "喵，您有" + earning.toString() + "元提现申请已成功转账！";
        }else if(TYPE_BONUS.equals(type)){
            return "喵，您有" + earning.toString() + "元销售分红到账！";
        }else if(TYPE_BONUS_PARENT.equals(type)){
            return "喵，您有" + earning.toString() + "元销售分红到账！";
        }else if(TYPE_INVITE.equals(type)){
            return "喵，您有" + earning.toString() + "元猫币到账！";
        }else if(TYPE_TEAM.equals(type)){
       	 	return "喵，您有" + earning.toString() + "元猫币到账！";
        }else if(TYPE_UPGRADE.equals(type)){
            return "喵，您有" + earning.toString() + "元伯乐奖励到账！";
        }else if(TYPE_TEAM_BONUS.equals(type)){
            return "喵，您有" + earning.toString() + "元分红收入到账！";
        }else{
            return "喵，您有一笔收入到账！";
        }
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public BigDecimal getEarning() {
        return earning;
    }

    public void setEarning(BigDecimal earning) {
        this.earning = earning;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public String getShareName() {
		return shareName;
	}

	public void setShareName(String shareName) {
		this.shareName = shareName;
	}

	public String getBonusLevel() {
		return bonusLevel;
	}

	public void setBonusLevel(String bonusLevel) {
		this.bonusLevel = bonusLevel;
	}
}
