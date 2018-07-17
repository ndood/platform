package com.fulu.game.admin.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@Slf4j
public class RedisListenerConfig {


    @Autowired
    private MessageListener redisMessageListener;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${spring.redis.database}")
    private String redisDatabase;

    @Bean
    RedisMessageListenerContainer container(MessageListenerAdapter listenerAdapter) {
            log.info("注册rediskey监听:database:{}",redisDatabase);
            RedisMessageListenerContainer container = new RedisMessageListenerContainer();
            container.setConnectionFactory(redisTemplate.getConnectionFactory());
            container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@"+redisDatabase+"__:expired"));
            return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(redisMessageListener);
    }
}
