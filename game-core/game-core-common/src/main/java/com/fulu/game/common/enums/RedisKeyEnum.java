package com.fulu.game.common.enums;

import com.fulu.game.common.properties.Config;
import com.fulu.game.common.utils.ApplicationContextRegister;

/**
 * redis key 前缀
 */
public enum RedisKeyEnum {

    PRODUCT_ENABLE_KEY,  //商品状态
    USER_ORDER_RECEIVE_TIME_KEY, //陪玩师接单设置状态
    USER_ORDER_ALREADY_SERVICE_KEY, //陪玩师是已经开始服务key
    ADMIN_TOKEN,//管理员token
    PLAY_TOKEN,//用户token
    WX_SESSION_KEY,//用户token
    SMS, //短信
    WX_TEMPLATE_MSG, //微信消息推送
    BITSET_PUSH_MSG_HITS; //微信推送点击去重

    public static final String SPLIT = "-";

    public String generateKey(Integer id) {
        return getEvnPrefix() + SPLIT + this.name() + SPLIT + id;
    }

    public String generateKey(String id) {
        return getEvnPrefix() + SPLIT + this.name() + SPLIT + id;
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
        Config config = ApplicationContextRegister.getBean("configProperties",Config.class);
        return config.getEvn().getPrefix();
    }


}
