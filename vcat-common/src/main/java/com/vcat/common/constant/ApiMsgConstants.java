package com.vcat.common.constant;

/**
 * 关于api的返回错误信息
 *
 * @author cw
 */
public class ApiMsgConstants {


    public static final int SUCCESS_CODE                      = 200;
    //成功子码
    public static final int SUCCESS_CODE_SUB                  = 2000;
    //请求成功，登录用户不存在，单独处理
    public static final int SUCCESS_CODE_CUSTOMER_NOT_EXSIT   = 2001;
    //请求成功，登录用户存在，单独处理
    public static final int SUCCESS_CODE_CUSTOMER_EXSIT       = 2002;
    //请求成功,用户为买家第一次在 app 登录
    public static final int SUCCESS_CODE_CUSTOMER_FIRST_LOGIN = 2020;
    //请求成功,登录用户存在,但是未激活
    public static final int SUCCESS_CODE_CUSTOMER_NOT_ACTIVE  = 2010;
    //请求成功，提现参数不对，或者超过单词提现金额
    public static final int SUCCESS_CODE_WITHDRAW_ERROR       = 2003;

    //请求成功，提现，用户没有绑定银行卡
    public static final int SUCCESS_CODE_BANK_NOT_EXSIT = 2004;

    //请求成功，提现，用户已有提现正在申请中
    public static final int SUCCESS_CODE_WITHDRAW_EXSIT = 2005;

    //请求成功，提现，提现金额不足
    public static final int SUCCESS_CODE_WITHDRAW_NO = 2006;
    //请求成功，用户已被邀请
    //public static final int SUCCESS_CODE_CUSTOMER_INVITE = 2007;
    //全额抵扣已购买过，无法再次购买
    public static final int COUPON_PRODUCT_BUY_CODE  = 2008;

    public static final int GURU_ILLEGAL_CODE = 2009;

    public static final int FAILED_CODE = 400;

    public static final int NOTFIND_CODE = 404;

    public static final int TOKEN_ILLEGAL_CODE = 401;

    // 上家店铺错误
    public static final int NO_SHOP_NUM_CODE = 405;

    // 店铺推荐的商品大于了4个
    public static final int RECOMMEND_FULL_CODE = 405;


    public static final String NOT_OWNER_MSG = "权限不够";

    public static final String SUCCESS_MSG = "成功";

    public static final String FAILED_MSG = "失败";
    public static final String CANCEL_ORDER = "成功取消";
    public static final String GROUPBUY_LOCKED = "此拼团参团人数已达到上限,请稍后再试";
    public static final String GROUPBUY_JOINED = "您已经参加过此次团购";
    public static final String NOFARM_ERROR_MSG  = "庄园主信息不能为空！";
    public static final String REQUEST_ERROR_MSG = "参数错误";

    public static final String PAYMENT_ERROR_MSG = "支付参数错误";

    public static final String WITHDRAW_ERROR_MSG = "提现金额不正确";
    //用户格式错误码
    public static final String PHONENUM_ERROR_MSG = "手机号格式错误";

    public static final String PASSWORD_EMPTY_MSG = "密码为空";

    public static final String GROUP_MEMBER_EXCEEDED = "本群已满员，无法加入成员";

    public static final String IDENTIFYCODE_INCURRENT_MSG = "验证码不正确";

    public static final String CUSTOMER_EXSIT_MSG = "用户已存在";

    public static final String ALREADY_INVITED = "此手机号已被邀请,请注册!";

    public static final String CUSTOMER_NOTEXSIT_MSG = "用户不存在";

    public static final String USERNAME_EXSIT_MSG = "该用户名已被使用";

    public static final String PASSWORD_INCURRENT = "密码不正确";
    public static final String NO_SHOP_MSG        = "无店铺信息,请升级为店主";

    public static final String HTIRDLOGIN_TYPE_INCURRENT = "三方登录类型不对";

    public static final String PHONENUM_BINDED_MSG = "手机号已注册，请直接登录";

    public static final String REFUND_NUM_TOOMUCH = "退货数量不能超过购买数量";

    public static final String REFUNDED          = "已发起退款申请";
    public static final String PROMOTION_PRODUCT = "此商品为优惠商品,退款时需全额退款";
    public static final String NOTREFUNDED       = "此商品不能退款";

    public static final String OPENID_NOT_FIND = "找不到微信openId";

    public static final String ACCOUNT_NOT_BIND = "您还未绑定收款账户";

    public static final String PARENT_SHOP_NOT_FIND = "邀请码信息有误，请重新输入";

    public static final String PARENT_SHOP_BIND = "您已经有邀请人";

    public static final String PARENT_NONE_TEAM = "您没有加入任何社群";

    public static final String COUPON_NOT_ENTHOU = "您的猫币余额不足";

    public static final String BG_IMAGE_ERROR = "您最多能选择1张背景图";

    public static final String SEAT_IS_FULL = "名额已满，无法报名";

    public static final String COUPON_PRODUCT_BUY = "本商品每人限购1件，不支持多次购买！";

    public static final String ANSWER_IS_SUBMITTED = "您已提交过该反馈报告";

    public static final String TOKEN_ILLEGAL = "用户未登录";

    public static final String IMAGE_NOT_EXSIT = "头像图片不存在";

    public static final String UPLOAD_IMAGE_FAILED = "上传头像失败";
    public static final String SHOPINFO_EXSIT_MSG  = "您的庄园主信息已存在，不可重复提交！";
    public static final String NOT_QRCODE_IMG      = "不是二维码,请重新上传";
    public static final String NO_QRCODE_IMG       = "图片不能为空";

    public static final String WITHDRAW_CASH_FAILED = "当前有一笔提现在申请中";

    public static final String IDCARD_ILLEGAL_MSG = "身份证号码不对";

    public static final String SHARE_PRODUCT_LOG_FAILED = "分享失败";

    public static final String SHARE_PRODUCT_LOG_EXSIT = "该商品只能分享一次";
    //只有一个地址设置为不默认报错
    public static final String ADDRESS_FAILED          = "请设置默认地址";

    public static final String QUANTIYT_NOT_ENOUGH = "商品库存不足";

    public static final String ADDRESS_NOT_FIND         = "请选择收货地址";
    //提现金额大于总可用收入
    public static final Object CASH_MORETHAN_FUND       = "您的提现金额不足";
    //只有在未审核时可以修改原因
    public static final String REFUND_NO_COMFIRE        = "未审核时才可以修改退款原因";
    //只有在未审核通过才可以添加退货物流
    public static final String REFUND_NOT_COMFIRE       = "审核通过后才可以添加退货物流";
    //只有退货单发起之前可以撤销退货
    public static final String RETURN_STATUS_APP_FAILED = "您已添加退货单，不能撤销退款";

    public static final String ACTIVITY_JOINED = "您已参加活动";

    public static final String ACTIVITY_TIME_OUT = "请在活动时间内参加活动";

    public static final String PRODUCT_NOT_FIND = "商品不存在";

    public static final String GET_SIGN_FAILED = "支付失败";

    public static final String REVIEW_ILLEGAL = "订单未完成，无法评价";

    public static final String REVIEW_IS_EXIST = "商品已评价";

    public static final String NOT_REACH_ACCOUNT  = "销售金额未达到升级标准";
    public static final String OK_REACH_ACCOUNT   = "销售金额达到升级标准";
    public static final String RECOMMEND_FULL_MSG = "店主最多推荐4个商品!";
    public static final String ISVIP              = "您已经是VIP用户";
    public static final String OVERLIMITCOUNT     = "商品数量已超出上限!";
    public static final String OVERTIME           = "团购已超时!";
    public static final String GROUPBUY_SUCCESS   = "拼团成功";
    public static final String GROUPBUY_FAILED    = "拼团失败";
    public static final String GROUPBUY_HASCHANCE = "还有机会";
    public static final String CAN_JOIN           = "参团页";

    public static final String AUTO_CANEL = "订单因超时自动取消，无法支付！";
    public static final String GROUP_BUY_AUTO_CANEL = "订单因超时自动取消，无法支付！";
    public static final String OUT_OF_STOCK = "商品已下架，无法支付！";
    public static final String MERGE_ERROR = "合并支付不能含有团购订单";

    public static final String NOT_ALL_REVIEW = "请对所有商品做出评价后再发表！";

}
