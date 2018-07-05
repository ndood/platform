package com.fulu.game.admin.shiro;

import com.fulu.game.common.domain.Password;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.EncryptUtil;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.util.ByteSource;
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
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher implements InitializingBean {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Override
    public void afterPropertiesSet() throws Exception {
    }


    /**
     * 验证密码是否匹配逻辑
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        return match(token, info);
    }

    /**
     * 自定义验证逻辑
     *
     * @param token
     * @param info
     * @return
     */
    private boolean match(AuthenticationToken token, AuthenticationInfo info) {
        String salt = "";
        if (info instanceof SaltedAuthenticationInfo) {
            ByteSource bs = ((SaltedAuthenticationInfo) info).getCredentialsSalt();
            try {
                salt = new String(bs.getBytes(), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Password passObj = EncryptUtil.PiecesEncode(new String((char[]) token.getCredentials()), salt);
        String tokenHashedCredentials = passObj.getPassword();
        String infoHashCredentials = (String) info.getCredentials();
        if (super.equals(tokenHashedCredentials, infoHashCredentials)) {
            //登录成功保存token和用户信息到redis
            Admin admin = (Admin) info.getPrincipals().getPrimaryPrincipal();
            Map<String, Object> adminMap = new HashMap<>();
            adminMap.put("id", admin.getId());
            adminMap.put("name", admin.getName());
            adminMap.put("status",admin.getStatus());
            String genToken = GenIdUtil.GetToken();
            redisOpenService.hset(RedisKeyEnum.ADMIN_TOKEN.generateKey(genToken), adminMap);
            log.info("登录成功生成token：{}", genToken);
            SubjectUtil.setToken(genToken);
            SubjectUtil.setCurrentUser(admin);
            return true;
        }
        return false;
    }


}
