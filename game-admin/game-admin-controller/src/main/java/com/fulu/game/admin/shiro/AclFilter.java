package com.fulu.game.admin.shiro;

import cn.hutool.core.collection.CollectionUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.AdminStatus;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.SysRole;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.SysRoleService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Slf4j
public class AclFilter extends AccessControlFilter {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private SysRoleService sysRoleService;

    @Override
    protected boolean isAccessAllowed(ServletRequest request,
                                      ServletResponse response, Object mappedValue) throws Exception {
        // TODO 权限控制由前端控制，后端暂不做控制，当需要后端控制时，再打开注释
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String action = httpRequest.getRequestURI().replace(httpRequest.getContextPath(), "");
//        try {
//            Subject subject = SecurityUtils.getSubject();
//            Admin admin = (Admin) subject.getPrincipal();
//            // 非超级用户，需要走权限验证
//            if(admin != null && !Constant.ADMIN_USERNAME.equals(admin.getUsername())){
//                subject.checkPermission(action);
//            }
//        } catch (UnauthorizedException exception){
//            log.info("用户无此访问权限");
//            throw new UserException(UserException.ExceptionCode.NO_PERMISSION);
//        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request,
                                     ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            return true;
        }
        log.info("uri:{}",httpRequest.getRequestURI());
        String token = httpRequest.getHeader("token");
        Map<String, Object> map = redisOpenService.hget(RedisKeyEnum.ADMIN_TOKEN.generateKey(token));
        String returnMsg = null;
        // 没有登录授权 且没有记住我
        if (MapUtils.isEmpty(map)) {
            log.info("验证登录失败token：{}", token);
            returnMsg = JSONObject.fromObject(Result.noLogin()).toString();
        } else {//检查账号是否已失效
            Integer id = Integer.valueOf(String.valueOf(map.get("id")));
            Admin admin = adminService.findById(id);
            if (admin == null || admin.getStatus().equals(AdminStatus.DISABLE.getType())) {
                log.info("账号已失效，请联系管理员");
                returnMsg = JSONObject.fromObject(Result.accessDeny().msg("账号已失效,请联系管理员")).toString();
            } else {
                List<SysRole> roleList = sysRoleService.findByAdminId(admin.getId());
                if(CollectionUtil.isNotEmpty(roleList)){
                    SysRole sysRole = roleList.get(0);
                    if(sysRole.getStatus().intValue() == 0){
                        log.info("账号权限已关闭");
                        returnMsg = JSONObject.fromObject(Result.accessDeny().msg("账号权限已关闭")).toString();
                    }
                } else {
                    log.info("用户未设置角色信息");
                    returnMsg = JSONObject.fromObject(Result.accessDeny().msg("用户未设置角色信息")).toString();
                }
            }
        }
        if (returnMsg != null) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json; charset=utf-8");
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            PrintWriter out = null;
            try {
                out = httpResponse.getWriter();
                out.append(returnMsg);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            return false;
        }
        //再存5分钟，保证会话时长
        redisOpenService.hset(RedisKeyEnum.ADMIN_TOKEN.generateKey(token), map);
        //已登录的，就保存该token从redis查到的用户信息
        Admin admin = BeanUtil.mapToBean(map, Admin.class, true);
        SubjectUtil.setCurrentUser(admin);
        return true;
    }

}