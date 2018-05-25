package com.fulu.game.admin.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisListenerConfig {


    @Autowired
    private MessageListener redisMessageListener;
    @Autowired
    private RedisTemplate redisTemplate;

    @Bean
    RedisMessageListenerContainer container(MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@1__:expired"));
        return container;
    }
    @Bean
    MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(redisMessageListener);
    }
}
