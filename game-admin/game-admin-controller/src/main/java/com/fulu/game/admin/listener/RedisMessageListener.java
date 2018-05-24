package com.fulu.game.admin.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageListener implements MessageListener {



    @Override
    public void onMessage(Message message, byte[] pattern) {
        //获取过期的key
        String expireKey = new String(message.getBody());
        System.out.println("终于失效了");
        System.out.println(expireKey);
    }



}
