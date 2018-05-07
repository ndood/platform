package com.fulu.game.play.shiro;

import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于验证密码是否匹配
 *
 * @author LiuPiao
 */
@Slf4j
public class PlayUserMatcher extends HashedCredentialsMatcher implements InitializingBean {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    /**
     * 自定义验证提交凭证和数据库（缓存）凭据信息是否一致,最后执行
     * 参数由doGetAuthenticationInfo方法传过来
     *
     * @param token
     * @param info
     * @return
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        log.info("开始PlayUserMatcher验证");
        PlayUserToken userToken = (PlayUserToken) token;
        String paramOpenId = userToken.getOpenId();
        User user = (User) info.getPrincipals().getPrimaryPrincipal();
        String dBOpenId = user.getOpenId();
        //登录成功保存token和用户信息到redis
        if (paramOpenId.equals(dBOpenId)) {
            Map<String, Object> userMap = new HashMap<>();
            userMap = BeanUtil.beanToMap(user);
            String gToken = GenIdUtil.GetGUID();
            redisOpenService.hset(RedisKeyEnum.PLAY_TOKEN.generateKey(gToken), userMap);
            log.info("生成新token {} 加入Redis缓存", gToken);
            SubjectUtil.setToken(gToken);
            log.info("setToken()执行，token {}", gToken);
            SubjectUtil.setCurrentUser(user);
            log.info("setCurrentUser()执行，token=={},PlayUserMatcher验证结束", SubjectUtil.getToken());
            return true;
        }
        return false;
    }

}
