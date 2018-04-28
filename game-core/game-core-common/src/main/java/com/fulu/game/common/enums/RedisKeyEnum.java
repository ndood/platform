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
    TOKEN; //用户token

    private static final String SPLIT = "-";

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
        Config config = ApplicationContextRegister.getBean(Config.class);
        return config.getEvn().getPrefix();
    }


}
