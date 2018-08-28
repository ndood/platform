package com.fulu.game.app.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.UserStatusEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 用于验证密码是否匹配
 *
 * @author LiuPiao
 */
@Slf4j
public class AppUserMatcher extends HashedCredentialsMatcher implements InitializingBean {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Autowired
    private UserService userService;


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
        AppUserToken userToken = (AppUserToken) token;
        User user = (User) info.getPrincipals().getPrimaryPrincipal();
        String redisVerifyCode = redisOpenService.get(RedisKeyEnum.SMS_VERIFY_CODE.generateKey(user.getMobile()));
        if (userToken.getVerifyCode().equals(redisVerifyCode)) {
            if (user.getId() == null) {
                user = userService.createNewUser(user.getMobile(), userToken.getHost());
            }
            if (UserStatusEnum.BANNED.getType().equals(user.getStatus())) {
                throw new UserException(UserException.ExceptionCode.USER_BANNED_EXCEPTION);
            }
            //匹配完毕更新新的登录时间和IP
            Map<String, Object> userMap = BeanUtil.beanToMap(user);
            String gToken = GenIdUtil.GetToken();
            redisOpenService.hset(RedisKeyEnum.PLAY_TOKEN.generateKey(gToken), userMap, Constant.APP_EXPIRE_TIME);
            SubjectUtil.setToken(gToken);
            SubjectUtil.setCurrentUser(user);
            log.info("生成新token=={},shiro验证结束", SubjectUtil.getToken());
            return true;
        }
        return false;
    }


}
