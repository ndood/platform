package com.fulu.game.play.shiro;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
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
//
//        SysUser sysUser  = (SysUser)principals.getPrimaryPrincipal();
//        List<SysRole> sysRoleList =   sysUserRoleService.findSysUserByRoleId(sysUser.getId());
//
//        for(SysRole role:sysRoleList){
//            authorizationInfo.addRole(role.getRole());
//            List<SysPermission> sysPermissionList = sysRolePermissionService.findSysPermissionByRoleId(role.getId());
//            for(SysPermission p:sysPermissionList){
//                authorizationInfo.addStringPermission(p.getPermission());
//            }
//        }
        return authorizationInfo;
    }

    /**
     * 验证openId是否存在
     * 执行时机：subject.login()方法
     * 参数token由login()方法传过来
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        log.info("MyShiroRealm.doGetAuthenticationInfo()");
        PlayUserToken playUserToken = (PlayUserToken) token;
        String sessionKey = playUserToken.getSessionKey();
        String openId = playUserToken.getOpenId();
        User user = userService.findByOpenId(openId);
        if (user != null) {
            return new SimpleAuthenticationInfo(user,user.getOpenId(),getName());
        }else{
            //没有该用户则创建一个
            UserVO userVO = new UserVO();
            userVO.setOpenId(openId);
            userVO.setSessionKey(sessionKey);
            user = userService.save(userVO);
            return new SimpleAuthenticationInfo(user,user.getOpenId(),getName());
        }
    }

}