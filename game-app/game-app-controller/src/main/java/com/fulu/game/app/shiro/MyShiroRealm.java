package com.fulu.game.app.shiro;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        return authorizationInfo;
    }

    /**
     * 验证openId是否存在
     * 执行时机：subject.login()方法
     * 参数token由login()方法传过来
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        AppUserToken playUserToken = (AppUserToken) token;
        String mobile = playUserToken.getMobile();
        String verifyCode = playUserToken.getVerifyCode();
        User user = userService.findByMobile(mobile);
        if (user == null) {
            user = new User();
            user.setMobile(mobile);
        }
        return new SimpleAuthenticationInfo(user, verifyCode, getName());
    }
}