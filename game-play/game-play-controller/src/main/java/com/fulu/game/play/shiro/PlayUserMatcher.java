package com.fulu.game.play.shiro;

import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
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
     * @param token
     * @param info
     * @return
     */


    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

        PlayUserToken userToken = (PlayUserToken)token;
        String paramOpenId = userToken.getOpenId();
        User user = (User) info.getPrincipals().getPrimaryPrincipal();
        String dBOpenId = user.getOpenId();
        //登录成功保存token和用户信息到redis
        if (paramOpenId.equals(dBOpenId)){
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id",user.getId());
            userMap.put("nickname",user.getNickname());
            userMap.put("openId", user.getOpenId());
            userMap.put("sessionKey", user.getSessionKey());
            String gToken = GenIdUtil.GetGUID();
            redisOpenService.hset(RedisKeyEnum.PLAY_TOKEN.generateKey(gToken), userMap);
            log.info("登录成功生成token：{}", gToken);
            SubjectUtil.setToken(gToken);
            log.info("当前token===========" + SubjectUtil.getToken());
            SubjectUtil.setCurrentUser(user);
            return true;
        }
        return false;
    }

}
