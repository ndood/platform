package com.fulu.game.play.aop;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.UserScoreEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserScoreDetails;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.queue.UserScoreQueue;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: 切面--用户积分
 * @Author: Gong ZeChun
 * @Date: 2018/7/16 15:47
 */
@Aspect
@Component
public class UserScoreAspect {
    @Autowired
    private UserService userService;
    @Autowired
    private UserScoreQueue userScoreQueue;

    /**
     * 用户积分--切入点
     */
    @Pointcut("execution(* com.fulu.game.play.controller.*.*(..))")
    public void annotationPointCut() {
    }

    @After(value = "annotationPointCut() && @annotation(score)", argNames = "score")
    public void processScore(UserScore score) {
        UserScoreEnum userScoreEnum = score.type();
        if(userScoreEnum.getDescription().equals(Constant.USER_LOGIN)) {
            User user = userService.getCurrentUser();
            UserScoreDetails details = new UserScoreDetails();
            details.setUserId(user.getId());
            details.setScore(userScoreEnum.getScore());
            details.setDescription(userScoreEnum.getDescription());
            userScoreQueue.addUserScoreQueue(details);
        }
    }
}
