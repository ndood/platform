package com.fulu.game.play.aop;

import com.fulu.game.common.enums.ScoreEnum;
import com.fulu.game.core.service.UserService;
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
public class ScoreAspect {
    @Autowired
    private UserService userService;

    /**
     * 用户积分--切入点
     */
    @Pointcut("execution(* com.fulu.game.play.controller.*.*(..))")
    public void annotationPointCut() {
    }

    @After(value = "annotationPointCut() && @annotation(score)", argNames = "score")
    public void processScore(Score score) {
        ScoreEnum scoreEnum = score.type();
        if(scoreEnum.getScore() == 1) {
            System.out.println(scoreEnum.getDescription());
        }
        if(scoreEnum.getScore() == -1) {
            System.out.println(scoreEnum.getDescription());
        }
        System.out.println("todo");

        //todo 业务逻辑
    }
}
