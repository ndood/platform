package com.fulu.game.h5.shiro;

import com.fulu.game.core.entity.ThirdpartyUser;
import com.fulu.game.core.service.ThirdpartyUserService;
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
    private ThirdpartyUserService thirdpartyUserService;


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
//        PlayUserToken playUserToken = (PlayUserToken) token;
//        String openId = playUserToken.getFqlOpenid();
//        ThirdpartyUser thirdpartyUser = thirdpartyUserService.findByFqlOpenid(openId);
//        if (thirdpartyUser != null) {
//            log.info("openId为{} 的用户已存在", openId);
//        } else {
//            //新创建的用户记录注册的ip
//            String ip = playUserToken.getHost();
//            thirdpartyUser = thirdpartyUserService.createFenqileUser(openId, ip);
//            log.info("创建openId为{}的用户", openId);
//        }
        return new SimpleAuthenticationInfo("","", getName());
    }
}