package com.fulu.game.common;

import java.math.BigDecimal;

public interface Constant {

    Integer DEF_PID = 0;  //默认的父类ID

    Double DEF_RECEIVING_ORDER_PRICE = 1.0;

    //手机验证码缓存时间
    Long VERIFYCODE_CACHE_TIME_DEV = 5 * 60L;
    Long VERIFYCODE_CACHE_TIME_DEP = 1 * 60L;

    Long TIME_MINUTES_FIFTEEN = 15 * 60L;

    Long TIME_MINUTES_FIFE = 5 * 60L;

    Long TIME_HOUR_TWO = 2*60 * 60L;

    Long TIME_HOUR_FIVE = 5*60 * 60L;


    Long TIME_MINUTES_FIVE= 5 * 60L;

    //手机验证码限定次数和限定时间
    Integer MOBILE_CODE_SEND_TIMES_DEV = 20;
    Integer MOBILE_CODE_SEND_TIMES_DEP = 3;
    Long MOBILE_CACHE_TIME_DEV = 30 * 60L;

    String WEIXN_JUMP_URL = "https://t-open.wzpeilian.com/pc/weixin.html ";


    //默认评价分
    BigDecimal DEFAULT_SCORE_AVG = new BigDecimal(4);
    //默认余额
    BigDecimal DEFAULT_BALANCE = new BigDecimal(0.00);
    //渠道商默认加款最低金额
    BigDecimal DEFAULT_CHANNEL_BALANCE_MIN = new BigDecimal(1.00);
    //渠道商默认加款最高金额
    BigDecimal DEFAULT_CHANNEL_BALANCE_MAX = new BigDecimal(100000.00);
    //默认认证通过需要的认可数
    Integer DEFAULT_APPROVE_COUNT = 5;

    String DEFAULT_SPLIT_SEPARATOR = ",";
    //默认名片宽度
    Integer TECH_CARD_WIDTH = 750;
    //默认名片高度
    Integer TECH_CARD_HEIGHT = 1467;
    Integer TECH_AUTH_HEIGHT = 1206;

    String DEFAULT_CITY = "未设置";
    String DEFAULT_CDK_CHANNEL_NAME = "盛天";

    /**
     * 24小时
     */
    Long ONE_DAY = 24 * 60 * 60L;

    /**
     * IM延迟回复--已计算对应用户积分
     */
    String IM_DELAY_CALCULATED = "0";

    /**
     * 提现申请的提醒文案
     */
    String CASH_DRAWS_NEXT_TUESDAY_TIPS = "提现金额将在下周二到达您的支付宝账户，请耐心等待";
    String CASH_DRAWS_NEXT_FRIDAY_TIPS = "提现金额将在下周五到达您的支付宝账户，请耐心等待";

    /**
     * 用户积分策略--start
     */

    /**
     * 登录
     */
    String USER_LOGIN = "用户登录";

    String IM_REPLY = "陪玩师回复IM消息";

    String ACCEPT_ORDER = "订单接单";

    String USER_COMMENT = "用户评价";

    String FULL_RESTITUTION = "全额赔偿给用户";

    String CONSULT = "协商";

    String NEGOTIATE = "仲裁";

    String IM_REPLY_DELAY_0_TO_5 = "0到5分钟回复IM";
    String IM_REPLY_DELAY_5_TO_60 = "5到60分钟回复IM";
    String IM_REPLY_DELAY_60_TO_120 = "60到120分钟回复IM";
    String IM_REPLY_DELAY_120_TO_360 = "120到360分钟回复IM";
    String IM_REPLY_DELAY_360_TO_1440 = "360到1440分钟回复IM";
    String IM_REPLY_DELAY_LONGGER_1440 = "1440分钟后回复IM";

    /**
     * 陪玩师接单
     */
    String ACCEPT_ORDER_DELAY_30_TO_60 = "30到60分钟内接单";
    String ACCEPT_ORDER_DELAY_60_TO_360 = "60到360分钟内接单";
    String ACCEPT_ORDER_DELAY_360_TO_1440 = "360到1440分钟内接单";
    String ACCEPT_ORDER_DELAY_LONGGER_1440 = "1440分钟后接单";

    /**
     * 用户评价
     */
    String USER_COMMENT_1_STAR = "用户评价1颗星";
    String USER_COMMENT_2_STAR = "用户评价2颗星";
    String USER_COMMENT_3_STAR = "用户评价3颗星";
    String USER_COMMENT_4_STAR = "用户评价4颗星";
    String USER_COMMENT_5_STAR = "用户评价5颗星";


    String USER_CANCEL_ORDER = "用户取消订单";
    String SERVICE_USER_CANCEL_ORDER = "陪玩师取消订单";

    String USER_FINISH_ORDER = "用户正常完成订单";
    String SERVICE_USER_FINISH_ORDER = "陪玩师正常完成订单";

    /**
     * 用户积分策略--end
     */

    /**
     * 手机号码长度为11
     */
    Integer MOBILE_NUMBER_LENGTH = 11;

    /**
     * 上分新用户-优惠券兑换码
     */
    String NEW_POINT_USER_COUPON_GROUP_REDEEM_CODE = "FlPwSfZx1882";

    /**
     * 用户头像最少展示个数
     */
    Integer MIN_USER_HEAD_COUNT = 5;

    /**
     * 用户头像最多展示个数
     */
    Integer MAX_USER_HEAD_COUNT = 10;

    //==============CJ活动start==================
    /**
     * 陪玩师在平台内展示
     */
    Integer PLATFORM_SHOW = 1;
    /**
     * 陪玩师不在平台内展示
     */
    Integer PLATFORM_NOT_SHOW = 0;

    /**
     * 已完成微信群分享
     */
    Integer WECHAT_GROUP_SHARE_FINISHED = 1;
    /**
     * 未完成微信群分享
     */
    Integer WECHAT_GROUP_SHARE_NOT_FINISHED = 0;

    /**
     * CJ活动首页：微信群最大分享个数限制
     */
    Integer WECHAT_GROUP_SHARE_MAXIMUM = 5;

    /**
     * CJ默认的来源id
     * (只能在查表无效时，才能使用此字段，使用之前确认线上环境的管理后台的注册来源中是否改动过)
     */
    Integer CJ_SOURCE_ID = 31;

    /**
     * CJ对应的优惠券兑换码
     */
    String CJ_COUPON_GROUP_REDEEM_CODE = "FlPw11ZyCj";

    /**
     * 发放优惠券成功
     */
    Integer SEND_COUPOU_SUCCESS = 1;

    /**
     * 发放优惠券失败(默认值)
     */
    Integer SEND_COUPOU_FAIL = 0;

    /**
     * 优惠券通道开启
     */
    Integer COUPON_AVAILABLE = 1;
    /**
     * 优惠券通道关闭
     */
    Integer COUPON_UNAVAILABLE = 0;

    //==============CJ活动end==================
}
