package com.fulu.game.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.asymmetric.RSA;
import sun.security.krb5.internal.crypto.RsaMd5CksumType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Constant {

    Integer DEF_PID = 0;  //默认的父类ID

    Double DEF_RECEIVING_ORDER_PRICE = 1.0;

    //手机验证码缓存时间
    Long VERIFYCODE_CACHE_TIME = 5 * 60L;

    Long TIME_MINUTES_FIFTEEN = 15 * 60L;

    Long TIME_MINUTES_FIFE = 5 * 60L;

    Long TIME_HOUR_TWO = 2 * 60 * 60L;

    Long TIME_HOUR_FIVE = 5 * 60 * 60L;


    Long TIME_MINUTES_FIVE = 5 * 60L;

    //手机验证码限定次数和限定时间
    Integer MOBILE_CODE_SEND_TIMES = 100; //todo 方便测试
    Long MOBILE_CACHE_TIME = 30 * 60L;

    long APP_EXPIRE_TIME = 30 * 24 * 60 * 60;


    String WEIXN_JUMP_URL = "https://t-open.wzpeilian.com/pc/weixin.html ";


    //默认评价分
    BigDecimal DEFAULT_SCORE_AVG = new BigDecimal(4);
    //默认余额
    BigDecimal DEFAULT_BALANCE = new BigDecimal(0.00);
    //渠道商默认加款最低金额
    BigDecimal DEFAULT_CHANNEL_BALANCE_MIN = new BigDecimal(1.00);
    //渠道商默认加款最高金额
    BigDecimal DEFAULT_CHANNEL_BALANCE_MAX = new BigDecimal(100000.00);


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
     * 60秒（毫秒值）
     */
    Long MILLI_SECOND_60 = 60 * 1000L;

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

    /**
     * 发放新用户优惠券成功
     */
    Integer SEND_COUPOU_SUCCESS = 1;

    /**
     * 陪玩师接单Map
     */
    Map<Integer, Object> serviceUserAcceptOrderMap = new ConcurrentHashMap<>();

    /**
     * 没有陪玩师接单
     */
    String SERVICE_USER_NOT_ACCEPT_ORDER = "0";
    /**
     * 陪玩师接单
     */
    String SERVICE_USER_ACCEPT_ORDER = "1";

    /**
     * 分期乐订单--已对账
     */
    Integer IS_RECON = 1;

    /**
     * 分期乐订单--未对账
     */
    Integer UN_RECON = 0;

    //app

    /**
     * 举报信息未处理（默认）
     */
    Integer UN_PROCESSED = 0;
    /**
     * 举报信息已处理
     */
    Integer IS_PROCESSED = 1;

    /**
     * 登录用户获取的虚拟币数量
     */
    Integer LOGIN_VIRTUAL_MONEY = 10;

    /**
     * 魅力值转化为可提现金额的比例
     */
    BigDecimal CHARM_TO_MONEY_RATE = new BigDecimal(0.07);

    Map<String, Object> RSA_KEY_MAP = new HashMap<>();

    Map<String, Object> RSA_MAP = new HashMap<>();
}
