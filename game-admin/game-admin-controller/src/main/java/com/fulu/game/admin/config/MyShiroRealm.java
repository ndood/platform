package com.fulu.game.admin.config;


import com.fulu.game.core.entity.SysPermission;
import com.fulu.game.core.entity.SysRole;
import com.fulu.game.core.entity.SysUser;
import com.fulu.game.core.service.SysRolePermissionService;
import com.fulu.game.core.service.SysUserRoleService;
import com.fulu.game.core.service.SysUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRolePermissionService sysRolePermissionService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        SysUser sysUser  = (SysUser)principals.getPrimaryPrincipal();
        List<SysRole> sysRoleList =   sysUserRoleService.findSysUserByRoleId(sysUser.getId());

        for(SysRole role:sysRoleList){
            authorizationInfo.addRole(role.getRole());
            List<SysPermission> sysPermissionList = sysRolePermissionService.findSysPermissionByRoleId(role.getId());
            for(SysPermission p:sysPermissionList){
                authorizationInfo.addStringPermission(p.getPermission());
            }
        }
        return authorizationInfo;
    }

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        System.out.println("MyShiroRealm.doGetAuthenticationInfo()");
        //获取用户的输入的账号.
        String username = (String)token.getPrincipal();
        System.out.println(token.getCredentials());
        //通过username从数据库中查找 User对象，如果找到，没找到.
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        SysUser sysUser = sysUserService.findByUsername(username);
        System.out.println("----->>userInfo="+sysUser);
        if(sysUser == null){
            return null;
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                sysUser, //用户名
                sysUser.getPassword(), //密码
                ByteSource.Util.bytes(sysUser.getSalt()),//salt=username+salt
                getName()  //realm name
        );
        return authenticationInfo;
    }

}