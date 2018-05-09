package com.fulu.game.play.shiro;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
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
        log.info("开始MyShiroRealm验证");
        PlayUserToken playUserToken = (PlayUserToken) token;
        String openId = playUserToken.getOpenId();
        User user = userService.findByOpenId(openId);
        if (user != null) {
            log.info("openId为 {} 的用户已存在", openId);
            return new SimpleAuthenticationInfo(user, user.getOpenId(), getName());
        } else {
            log.info("openId为 {} 的用户不存在，开始创建该用户", openId);
            UserVO userVO = new UserVO();
            userVO.setOpenId(openId);
            user = userService.save(userVO);
            log.info("openId为 {} 的用户创建成功，MyShiroRealm验证结束");
            return new SimpleAuthenticationInfo(user, user.getOpenId(), getName());
        }
    }

}