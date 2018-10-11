package com.fulu.game.common.enums;

import com.fulu.game.common.properties.Config;
import com.fulu.game.common.utils.ApplicationContextRegister;

/**
 * redis key 前缀
 */
public enum RedisKeyEnum {
    //商品状态
    PRODUCT_ENABLE_KEY,
    //陪玩师接单设置状态
    USER_ORDER_RECEIVE_TIME_KEY,
    //陪玩师是已经开始服务key
    USER_ORDER_ALREADY_SERVICE_KEY,
    //管理员token
    ADMIN_TOKEN,
    //用户token
    PLAY_TOKEN,
    //小程序的sessionKey
    WX_SESSION_KEY,
    //短信
    SMS,
    //微信消息推送
    WX_TEMPLATE_MSG,
    //微信推送点击去重
    BITSET_PUSH_MSG_HITS,
    //市场订单接单锁
    MARKET_ORDER_RECEIVE_LOCK,
    //市场订单
    MARKET_ORDER,
    //写操作的sessionKey
    WRITER_SESSION_KEY,
    //判断集市订单是否推送过
    MARKET_ORDER_IS_PUSH,
    //表单验证token
    GLOBAL_FORM_TOKEN,
    //用户在线状态
    USER_ONLINE_KEY,
    //订单状态倒计时
    TIME_INTERVAL_KEY,
    //是否已经自动排单用户
    AUTO_ASSIGN_ORDER_USER,
    //APP登录验证码1
    SMS_VERIFY_CODE,
    //APP登录验证码发送次数1
    SMS_VERIFY_CODE_TIMES,
    //陪玩师IM未读数据
    IM_COMPANY_UNREAD,
    //登录用户是否领取过登录奖励
    LOGIN_RECEIVE_VIRTUAL_MONEY,
    //关注用户
    ATTENTION_USERS,
    //被关注用户
    ATTENTIONED_USERS,
    // 管理后台授权
    ADMIN_AUTHED,
    //需要随机自动问好的用户
    AUTO_SAY_HELLO_USER_LIST,
    //用户未读订单
    USER_WAITING_READ_ORDER,
    //午夜场标记
    MIDNIGHT,
    //用户24小时内开启了代聊
    USER_AGENT_IM_OPEN,
    //未接单的订单ID
    ORDER_WAITING_SERVICE_ID,
    //历史访问次数
    HISTORY_BROWSE_COUNT,
    //历史被访问次数
    HISTORY_ACCESSED_COUNT,
    //访问次数
    ACCESS_COUNT,
    //发布动态数
    DYNAMIC_COUNT,
    //聊天室虚拟人数
    CHAT_ROOM_VIRTUAL_COUNT,
    //聊天室在线用户set
    CHAT_ROOM_ONLINE_USER,
    //在线用户信息
    CHAT_ROOM_ONLINE_USER_INFO,
    //聊天室麦位list
    CHAT_ROOM_MIC,
    //上麦列表
    CHAT_ROOM_MIC_UP_LIST;



    public static final String SPLIT = "-";

    public String generateKey(int id) {
        return getEvnPrefix() + SPLIT + this.name() + SPLIT + id;
    }

    public String generateKey(int parentId,int id) {
        return getEvnPrefix() + SPLIT + this.name() + SPLIT + parentId+SPLIT+id;
    }

    public String generateKey(String id) {
        return getEvnPrefix() + SPLIT + this.name() + SPLIT + id;
    }

    public String generateKey(String parentId,String id) {
        return getEvnPrefix() + SPLIT + this.name() + SPLIT + parentId+SPLIT+id;
    }


    public String generateKey(String parentId,Integer id) {
        return getEvnPrefix() + SPLIT + this.name() + SPLIT + parentId+SPLIT+id;
    }

    public String generateKey() {
        return getEvnPrefix() + SPLIT + this.name();
    }

    /**
     * 获取环境定义的前缀
     *
     * @return
     */
    private String getEvnPrefix() {
        Config config = ApplicationContextRegister.getBean("configProperties", Config.class);
        return config.getEvn().getPrefix();
    }


}
