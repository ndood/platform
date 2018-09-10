package com.fulu.game.admin.shiro;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.AdminStatus;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
public class AclFilter extends AccessControlFilter {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private AdminService adminService;

    @Override
    protected boolean isAccessAllowed(ServletRequest request,
                                      ServletResponse response, Object mappedValue) throws Exception {
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