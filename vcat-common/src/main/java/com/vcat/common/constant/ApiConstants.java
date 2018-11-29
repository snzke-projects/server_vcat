package com.vcat.common.constant;

import com.vcat.common.config.Global;

public class ApiConstants {
	
	public static final String PARAMS = "params";
	
	public static final String CODE = "code";
	
	public static final String MSG = "msg";

	public static final String STATUS = "status";

	public static final Integer LOGIN  = 1;
	public static final Integer LOGOUT = 2;
	public static final Integer SHOPERVIEW_CODE = 1;
	//类型为普通
	public final static String NORMAL = "1";
	//类型为拿样
	public final static String SUPER = "2";
	//类型为猫币全部抵扣
	public final static String COUPON_ALL = "3";
	//类型为猫币部分抵扣
	public final static String COUPON_PART = "4";
	//类型为猫币全部类型(0元拿样[全部折扣]+补贴拿样[部分折扣])
	public final static String COUPON_TOTAL = "5";
    //店主查看自己小店预售商品是显示小店库存
	public final static String SHOW_SHOP_INVENTORY = "5";
	//店主预购商品拿货
    public final static String RESERVE = "5";
	//订单类型为预售拿样
    public final static String SUPER_RESERVE = "6";
    //订单类型为V猫庄园
    public final static String VCATLEROY = "7";
	// 卖家开团
    public final static String GROUPBUY_SELLER = "8";
	// 买家开团/参团
    public final static String GROUPBUY_BUYER = "9";
    //类型为V猫庄园
	public final static String LEROY = "6";
    //类型为快递
    public final static String POSTAGE = "5";
	//类型为专题活动
	public final static String TOPIC = "6";
	//状态为true，成功,绑定
	public final static String YES = "1";
	//状态为false，失败,没有绑定
	public final static String NO = "0";
	//劵的类型为上家邀请奖励
	public final static String COUPON_TYPE_INVITER = "1";
	//劵的类型为下家邀请奖励
	public final static String COUPON_TYPE_INVITEE = "2";


	public static final int DEFAULT_TOKEN_TIMEOUT = 0;//默认token失效时间为永久。
	
	public static final int DEFAULT_IDCODE_TIMEOUT = 30*60;//默认验证玛失效时间为30分钟。
	
	public static final String SELLERTOKEN = "seller-token";//默认卖家token后缀
	
	public static final String BUYERTOKEN = "buyer-token";//默认买家token后缀
	
	public static final String DEFAULT_LEVEL = "1级";//默认初始等级名字
	
	public static final int DEFAULT_SHOP_NUM = 1000;//初始小店编号
	
	public static final String RANK_TYPE_ALL = "all";//收入排行类型为总排行
	
	public static final String RANK_TYPE_MONTH = "month";//收入排行类型为当月排行
	
	public static final String BILL_TYPE_SALE = "sale";//详单类型为销售
	
	public static final String BILL_TYPE_BONUS = "bonus";//详单类型为销售分红
	public static final String BILL_TYPE_FIRSTBONUS = "firstBonus";//详单类型为一级团队分红
	public static final String BILL_TYPE_SECONDBONUS = "secondBonus";//详单类型为二级团队分红

	public static final Object BILL_TYPE_COUPON = "coupon";//详单类型为购物卷

	public static final String COUPON_ALL_NAME = "猫币全额抵扣";

	public static final String COUPON_PART_NAME = "猫币部分抵扣";

	public static final String SUPER_RESERVE_NAME = "预售进货";

	public static final String SUPER_SHOP_NAME = "小店拿样";

	public static final String SUPER_NAME = "V猫商场";

	public static final int BG_IMAGE_SIZE = 1;

	public static final String ACTIVITY = "activity";

	public static final String COUPON_ALERT = "有钱啦有钱啦，快去血拼吧~";

	public static final Integer WX_REGISTER = 0; //微信注册
	
	public static final Integer SELLER_REGISTER = 1; //客户端注册

	public static final Integer INVITE_REGISTER = 2; //邀请注册

	public static final Integer ALL_REGISTER = -1;

	public static final Integer SELLER_TYPE = 1;

	public static final Integer BUYER_TYPE = 2;
	public static final Integer INVITE_TYPE = 3;

	public static final int DEFAULT_PAGE_SIZE = Integer.valueOf(Global.getConfig("page.pageSize"));//默认分页大小

	public static final int PAGE_SIZE = 15;//当数据比较多时，使用
	
	public static final String AREA_CODE = "100000";//默认父地区为中国
	//设备类型为android
	public static final int  DEVICE_TYPE_ANDROID= 3;
	//设备类型为ios
	public static final int DEVICE_TYPE_IOS = 4;
	
	//提现未处理
	public static final int WITHDRAW_STATUS_NOT_CONFIRMED = 0;
	//提现成功
	public static final int WITHDRAW_STATUS_SUCESS = 1;
	//提现失败
	public static final int WITHDRAW_STATUS_FAILED = 2;
	//单次提现最大额度
	public static final int PRO_MAX_WITHDRAW_FUND = 5000;
	//默认商品缩略图大小
	public static final String DEFAULT_PRODUCT_THUMBSTYLE = "150";
	//默认头像缩略图大小
	public static final String DEFAULT_AVA_THUMBSTYLE = "100";
	//默认背景图样式
	public static final String DEFAULT_BG_THUMBSTYLE = "m290";
	//背景类型为所有
	public static final String BG_TYPE_ALL = "all";
	//背景类型为我选中的
	public static final String BG_TYPE_BIND = "bind";
	//卖家角色名
	public static final String SELLER = "seller";
	//买家角色名
	public static final String BUYER = "buyer";
	//卖家角色名
	public static final int SELLER_FLAG = 1;
	//买家角色名
	public static final int BUYER_FLAG = 2;
	
	public static final String SHARE_TYPE_WX = "Wechat";

	public static final String PAY_TYPE_WX = "Wechat";

	public static final String PAY_TYPE_WX_MOBILE = "WechatMobile";
	
	public static final String PAY_TYPE_ALIPAY = "alipay";
	
	public static final String PAY_TYPE_TENPAY = "tenpay";
	
	public static final String PAY_TYPE_BANK = "bankpay";
	//vcat域名
	public static final String VCAT_DOMAIN = Global.getConfig("vcat.domain");
	//收藏类型为店铺
	public static final String COLLECT_TYPE_PRODUCT ="product";
	//收藏类型为商品
	public static final String COLLECT_TYPE_SHOP = "shop";
	//订单状态待确认
	public static final String ORDER_TYPE_RETURNING = "3";
	//订单不存在
	public static final Object ORDER_NOT_EXSIT = "订单不存在";
    // 邮费商品项ID字典键
    public static final String POSTAGE_PRODUCT_ITEM_ID_KEY = "ec_postage_product_item_id";
    // 活动费用支付过期时间
    public static final String ACTIVITY_FEES_PAID_TIME = "ec_activity_fees_paid_time";
    // 活动状态
    public static final String ACTIVITY_PARTICIPATION_STATE = "activityParticipationState";
    // 活动状态 - 未参加
    public static final int ACTIVITY_PARTICIPATION_STATE_NOT_PARTICIPATING = 0;
    // 活动状态 - 参加未支付
    public static final int ACTIVITY_PARTICIPATION_STATE_NOT_PAID = 1;
    // 活动状态 - 已参加
    public static final int ACTIVITY_PARTICIPATION_STATE_PARTICIPATED = 2;
    //v猫商场-推荐
	public static final String RECOMMENT_NAME = "推荐";
	
	public static final String RECOMMENT_CODE = "recommend";
    //v猫商场-猫币专享
	public static final String COUPON_NAME = "猫币专享";
	
	public static final String COUPON_CODE = "coupon";
    //v猫商场-新品
	public static final String NEW_NAME = "新品";
	
	public static final String NEW_CODE = "new";
    //V猫商场-庄园
	public static final String LEROY_NAME = "V猫庄园";
	public static final String LEROY_CODE = "leroy";

	//v猫商场-预售
	public static final String REVERT_NAME = "进货";

	public static final String REVERT_CODE= "reserve";
	//类别为0元秒杀
	public static final String COUPON_TYPE = "coupon";
	//类别为每日精选
	public static final String SELECTION_TYPE = "selection";
	//类别为精品类别
	public static final String CATEGORY_TYPE = "category";

	// 天天拼团
	public static final String GROUP_BUY_TYPE = "groupbuy";
	public static final String GROUP_BUY_NAME = "天天拼团";
	// V猫庄园
	public static final String LEROY_TYPE = "leroy";


	public static final String DEFAULT = "default";

	public static final int DEFAULT_LEN = 6;
	//添加推荐标识
	public static final String ADD = "add";
	//取消推荐标识
	public static final String CANCEL = "cancel";
	//当达到升级要求时,发送的默认 title
    public static final String UPGRADE_DEFAULT_TITLE = "喵主,您可以升级为白金小店了.";
    public static final String NEW_JOIN_MEMBER = "喵主，有新成员加入您的团队了！";
	//当达到升级要求时,发送的默认 content
    public static final String UPGRADE_DEFAULT_CONTENT = "您的小店销售额已达到升级标准,快去申请吧!";

	public static final String SORT_DISCOVERY = "1";
	public static final String SORT_LEROY = "1";
	public static final String SORT_MYSHOP = "2";
	public static final String SORT_SOURCE = "2";

	// 团购类型
	public static final int ZERO_LAUNCH = 0;
	public static final int SELLER_LAUNCH = 8;
	public static final int BUYER_LAUNCH = 9;

	// 团购锁定标识
	public static final int GROUP_BUY_LOCKED = 1;
	public static final int GROUP_BUY_UNLOCKED = 0;

	// 开团成功页面
	public static final int START_SUCCESS_PAGE = 1;
	// 参团成功页面
	public static final int JOIN_SUCCESS_PAGE = 2;
	// 拼团成功页面
	public static final int GROUPBUY_SUCCESS_PAGE = 3;
	// 参团页面
	public static final int JOIN_PAGE = 4;
	// 有机会页面
	public static final int HAVE_CHANCE_PAGE = 5;
	// 拼团失败页面
	public static final int GROUPBUY_FAIL_PAGE = 6;

	// 无步骤标识
	public static final int NO_STEP = 0;
	// 选择心仪商品
	public static final int CHOOSE_PRODUCT_STEP = 1;
	// 支付开团或参团
	public static final int WAIT_PAY_STEP = 2;
	// 等待好友参团
	public static final int WAIT_JOIN_STEP = 3;
	// 达到人数拼团成功
	public static final int SUCCESS_STEP = 4;
}
