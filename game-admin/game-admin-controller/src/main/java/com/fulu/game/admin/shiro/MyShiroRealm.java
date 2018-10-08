package com.fulu.game.admin.shiro;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.AdminStatus;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.SysRole;
import com.fulu.game.core.entity.SysRouter;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.RoleRouterService;
import com.fulu.game.core.service.SysRoleService;
import com.fulu.game.core.service.SysRouterService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private AdminService adminService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRouterService sysRouterService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // TODO 权限控制由前端控制，后端暂不做控制，当需要后端控制时，再打开注释
//        Admin admin  = (Admin)principals.getPrimaryPrincipal();
//        Set<String> permissionList = new HashSet<>();
//        String key = RedisKeyEnum.ADMIN_AUTHED.generateKey(admin.getId());
//        if(redisOpenService.hasKey(key)){
//            permissionList = JSONObject.parseObject(redisOpenService.get(key),Set.class);
//            for(String permission: permissionList){
//                authorizationInfo.addStringPermission(permission);
//            }
//        } else {
//            if(admin != null && admin.getUsername() != null &&
//                    !Constant.ADMIN_USERNAME.equals(admin.getUsername())){
//                List<SysRole> sysRoleList = sysRoleService.findByAdminId(admin.getId());
//                if(CollectionUtil.isNotEmpty(sysRoleList)){
//                    for(SysRole role: sysRoleList){
//                        authorizationInfo.addRole(role.getName());
//                        List<SysRouter> routerList = sysRouterService.findByRoleId(role.getId());
//                        if(CollectionUtil.isNotEmpty(routerList)){
//                            for(SysRouter router: routerList){
//                                authorizationInfo.addStringPermission(router.getPath());
//                                permissionList.add(router.getPath());
//                            }
//                        }
//                    }
//                }
//                //此处可以不用设置，因为拦截器里面不会去验证超管权限
//            } else if(admin != null && admin.getUsername() != null &&
//                    Constant.ADMIN_USERNAME.equals(admin.getUsername())){
//                List<SysRouter> routerList = sysRouterService.findAll();
//                if(CollectionUtil.isNotEmpty(routerList)){
//                    for(SysRouter router: routerList){
//                        permissionList.add(router.getPath());
//                    }
//                }
//            }
//        }
//        redisOpenService.set(key, JSONObject.toJSONString(permissionList));
//        log.info("权限配置列表 {}",JSONObject.toJSONString(permissionList));
        return authorizationInfo;
    }

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        log.info("Realm验证开始");
        //获取用户的输入的账号.
        String username = (String) token.getPrincipal();
        //通过username从数据库中查找 User对象，如果找到，没找到.
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        Admin admin = adminService.findByUsername(username);
        log.info("adminInfo=" + admin);
        if (admin == null) {
            throw new UnknownAccountException();
        }
        if (AdminStatus.DISABLE.getType().equals(admin.getStatus())) {
            throw new LockedAccountException();
        }
        String password = admin.getPassword();
        String salt = admin.getSalt();

        admin.setPassword(null);
        admin.setSalt(null);
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                admin, //用户对象
                password, //密码
                ByteSource.Util.bytes(salt),//salt=username+salt
                getName()  //realm name
        );
        return authenticationInfo;
    }
}