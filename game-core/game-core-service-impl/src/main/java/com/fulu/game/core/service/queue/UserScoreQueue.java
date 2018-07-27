package com.fulu.game.core.service.queue;

import com.fulu.game.core.dao.UserScoreDetailsDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserScoreDetails;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 用户积分队列
 *
 * @author Gong ZeChun
 * @date 2018/7/17 12:00
 */
@Component
@Slf4j
public class UserScoreQueue implements Runnable {
    private BlockingQueue<UserScoreDetails> userScoreQueue = new LinkedBlockingDeque<>(50000);

    private AtomicBoolean run = new AtomicBoolean();

    private final UserScoreDetailsDao userScoreDetailsDao;
    private final UserService userService;

    @Autowired
    public UserScoreQueue(UserScoreDetailsDao userScoreDetailsDao, UserService userService) {
        this.userScoreDetailsDao = userScoreDetailsDao;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        run.set(true);
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("用户积分队列开启");
        thread.start();
    }


    @PreDestroy
    public void destroy() {
        run.set(false);
    }


    public void addUserScoreQueue(UserScoreDetails details) {
        userScoreQueue.add(details);
    }


    @Override
    public void run() {
        log.info("开始修改用户积分");
        while (run.get()) {
            try {
                UserScoreDetails details = userScoreQueue.poll();
                if (details == null) {
                    Thread.sleep(300L);
                    continue;
                }
                process(details);
            } catch (Exception e) {
                log.error("修改用户积分队列异常", e);
            }
        }
        log.info("修改用户积分结束");
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
            user.setUpdateTime(new Date());
            userService.update(user);

            details.setCreateTime(new Date());
            details.setDescription(details.getDescription() + "，对应的userId： " + details.getUserId());
            userScoreDetailsDao.create(details);
            log.info("修改用户积分，userId:{}，修改后用户总积分:{}", details.getUserId(), user.getUserScore());
        } catch (Exception e) {
            log.error("修改用户积分出错!", e);
        }
    }
}
