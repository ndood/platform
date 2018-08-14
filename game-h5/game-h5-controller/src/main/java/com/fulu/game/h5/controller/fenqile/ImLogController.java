package com.fulu.game.h5.controller.fenqile;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * IM日志Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/14 11:12
 */
@RestController
@RequestMapping(value = "/api/v1/imlog")
@Slf4j
public class ImLogController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    /**
     * 日志收集（后台记录前端的错误日志）
     *
     * @param content 日志内容
     * @return 封装结果集
     */
    @PostMapping(value = "/collect")
    public Result log(String content) {
        log.error("日志收集:{}", content);
        return Result.success();
    }


    /**
     * 用户上线下线的日志通知
     *
     * @param active  是否在线
     * @param version 版本号
     * @return 封装结果集
     */
    @PostMapping(value = "/online")
    public Result userOnline(@RequestParam Boolean active, String version) {
        User user = userService.getCurrentUser();
        if (active) {
            log.info("userId:{}用户上线了!;version:{}", user.getId(), version);
            redisOpenService.set(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()), user.getType() + "");
        } else {
            log.info("userId:{}用户下线了!version:{}", user.getId(), version);
            redisOpenService.delete(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()));
        }
        return Result.success();
    }
}
