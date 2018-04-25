package com.fulu.game.common.enums;

import com.fulu.game.common.properties.Config;
import com.fulu.game.common.utils.ApplicationContextRegister;

/**
 * redis key 前缀
 */
public enum RedisKeyEnum {


    PRODUCT_ENABLE_KEY,  //商品状态
    USER_ORDER_RECEIVE_TIME_KEY; //用户接单设置状态

    public String generateKey(Integer id){
        return getEvnPrefix()+"-"+this.name()+"-"+id;
    }

    public String getKey(){
        return getEvnPrefix()+"-"+this.name();
    }

    /**
     * 获取环境定义的前缀
     * @return
     */
    public String getEvnPrefix(){
       Config config = ApplicationContextRegister.getBean(Config.class);
       return config.getEvn().getPrefix();
    }


}
