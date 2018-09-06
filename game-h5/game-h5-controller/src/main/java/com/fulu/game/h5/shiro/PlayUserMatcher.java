package com.fulu.game.h5.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.UserStatusEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.ThirdpartyUser;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.ThirdpartyUserService;
import com.fulu.game.core.service.UserService;
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
    @Autowired
    private UserService userService;
    @Autowired
    private ThirdpartyUserService thirdpartyUserService;

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
        PlayUserToken userToken = (PlayUserToken) token;
        User user = null;
        switch (userToken.getPlatform()) {
            case FENQILE:
                String openId = userToken.getFqlOpenid();
                ThirdpartyUser thirdpartyUser = thirdpartyUserService.findByFqlOpenid(openId);
                if (thirdpartyUser != null) {
                    log.info("openId为{} 的用户已存在", openId);
                } else {
                    //新创建的用户记录注册的ip
                    String ip = userToken.getHost();
                    thirdpartyUser = thirdpartyUserService.createFenqileUser(openId, ip);
                    log.info("创建openId为{}的用户", openId);
                    user = userService.findById(thirdpartyUser.getUserId());
                }
                break;
            case MP:
                String mobile = userToken.getMobile();
                String mpOpenId = userToken.getMpOpenId();
                String unionId = userToken.getUnionId();
                user = userService.findByOpenId(mpOpenId, PlatformEcoEnum.MP);
                if(user==null){
                    user = userService.findByUnionId(unionId);
                }
                if(user ==null){
                    user = userService.findByMobile(mobile);
                }
                if(user==null){
                    user = userService.createNewUser(mobile,mpOpenId,unionId,userToken.getHost());
                }
                break;

        }
        if(user==null){
            return false;
        }
        if (UserStatusEnum.BANNED.getType().equals(user.getStatus())) {
            throw new UserException(UserException.ExceptionCode.USER_BANNED_EXCEPTION);
        }
        //用户信息写redis
        Map<String, Object> userMap = BeanUtil.beanToMap(user);
        String gToken = GenIdUtil.GetToken();
        redisOpenService.hset(RedisKeyEnum.PLAY_TOKEN.generateKey(gToken), userMap);
        SubjectUtil.setToken(gToken);
        SubjectUtil.setCurrentUser(user);
        log.info("生成新token=={},shiro验证结束", SubjectUtil.getToken());
        return true;
    }

}
