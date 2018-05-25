package com.fulu.game.admin.listener;

import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class RedisMessageListener implements MessageListener {
    @Autowired
    private ProductService productService;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        //获取过期的key
        String expireKey = new String(message.getBody());
        log.info("监听redisKey到期:{}",expireKey);
        //判断用户是否下线
        String  userOrderReceiveTimeKey =  RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey();
        Pattern userOrderReceivePattern = Pattern.compile(userOrderReceiveTimeKey+RedisKeyEnum.SPLIT+"(\\d+)");
        Matcher userOrderReceiveMatcher =userOrderReceivePattern.matcher(expireKey);
        //用户下线
        if(userOrderReceiveMatcher.find()){
            Integer userId = Integer.valueOf(userOrderReceiveMatcher.group(1));
            productService.batchCreateUserProduct(userId);
        }

    }







}
