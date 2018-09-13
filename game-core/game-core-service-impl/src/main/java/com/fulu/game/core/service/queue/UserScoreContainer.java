package com.fulu.game.core.service.queue;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.core.dao.UserScoreDetailsDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserScoreDetails;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
@Component
public class UserScoreContainer extends RedisTaskContainer {

    private static final String USER_SCORE_QUEQUE = "user:score:queue";

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    private static int runTaskThreadNum = 1;

    //使用一个统一维护的线程池来管理隔离线程
    protected static ExecutorService es = Executors.newFixedThreadPool(runTaskThreadNum);

    @Autowired
    private UserScoreDetailsDao userScoreDetailsDao;
    @Autowired
    private UserService userService;

    private RedisConsumer redisConsumer;

    @PostConstruct
    private void init() {
        redisQueue = new RedisQueue<UserScoreDetails>(USER_SCORE_QUEQUE, redisOpenService);

        if (!configProperties.getQueue().isUserScore()) {
            log.info("无需开启用户积分队列线程");
            es.shutdown();
            return;
        }

        Consumer<UserScoreDetails> consumer = (data) -> {
            process(data);
        };

        //提交线程
        for (int i = 0; i < runTaskThreadNum; i++) {
            redisConsumer = new RedisConsumer<>(this, consumer);
            es.execute(redisConsumer);
        }
    }

    private void process(UserScoreDetails details) {
        try {
            if (details == null) {
                return;
            }
            Integer userScore = userService.findUserScoreByUpdate(details.getUserId());
            if (userScore == null) {
                userScore = 0;
            }
            log.info("修改用户积分，userId:{}，修改前用户总积分:{}", details.getUserId(), userScore);
            User user = new User();
            user.setId(details.getUserId());
            user.setUserScore(userScore + details.getScore());
            user.setUpdateTime(DateUtil.date());
            userService.update(user);

            details.setCreateTime(DateUtil.date());
            userScoreDetailsDao.create(details);
            log.info("修改用户积分，userId:{}，修改后用户总积分:{}", details.getUserId(), user.getUserScore());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("修改用户积分出错!", e);
        }
    }

    /**
     * 添加用户积分元素到队列
     */
    public void add(UserScoreDetails userScodeDetails) {
        redisQueue.pushFromHead(userScodeDetails);
    }

    @PreDestroy
    public void destroy() {
        log.info("用户积分队列销毁");
        if (es != null) {
            es.shutdown();
        }
        if (redisConsumer != null) {
            redisConsumer.shutdown();
        }
    }
}
