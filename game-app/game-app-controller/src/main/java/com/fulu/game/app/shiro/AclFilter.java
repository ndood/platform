package com.fulu.game.app.shiro;


import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 调用小程序服务接口时都会先进入此类的方法
 */
@Slf4j
public class AclFilter extends AccessControlFilter {

    /** 不需要登录的请求 */
    private static List<String> NOT_REQUIRE_LOGIN_ACTION = new ArrayList<>();

    static {
        try {
            NOT_REQUIRE_LOGIN_ACTION = IOUtils.readLines(AclFilter.class.getClassLoader().getResourceAsStream("notRequireLoginAction.data"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    /**
     * 首先调用若返回true则继续执行filter和servlet，否则调用onAccessDenied
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        List<String> urls = NOT_REQUIRE_LOGIN_ACTION;
        if(urls != null && urls.size() > 0){
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String action = httpRequest.getRequestURI().replace(httpRequest.getContextPath(), "");
            String token = httpRequest.getHeader("token");
            boolean flag = false;
            for(String url: urls){
                if(action != null && !"".equals(action) && url != null &&
                        !"".equals(url) && url.equals(action)){
                    log.info("notRequireLoginAction: " + action);
                    flag = true;
                    break;
                }
            }
            // 不需权限验证，但是如果登录的话，需要设置当前线程变量信息
            if(flag){
                // 设置当前线程变量信息
                setThreadLocalInfo(token);
                return true;
            }
        }
        return false;
    }

    /**
     * 验证token的有效性
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("token");
        log.info("head中的token:{}", token);
        log.info("uri:{}",httpRequest.getRequestURI());
        // 没有登录授权 且没有记住我
        if (!redisOpenService.hasKey(RedisKeyEnum.PLAY_TOKEN.generateKey(token))) {
            log.info("token {} 验证失效=====", token);
            // 没有登录
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            try {
                out = httpResponse.getWriter();
                JSONObject res = new JSONObject();
                res.put("status", "501");
                res.put("data", "");
                res.put("msg", "您未登录，暂无访问权限！");
                out.write(res.toString());
            } catch (IOException e) {
                log.error("IO异常:{}", e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            return false;
        }
        //设置当前线程变量信息
        setThreadLocalInfo(token);
        return true;
    }

    /**
     * 设置当前线程变量信息
     * @param token
     */
    private void setThreadLocalInfo(String token){
        if(redisOpenService.hasKey(RedisKeyEnum.PLAY_TOKEN.generateKey(token))){
            Map<String, Object> map = redisOpenService.hget(RedisKeyEnum.PLAY_TOKEN.generateKey(token));
            redisOpenService.hset(RedisKeyEnum.PLAY_TOKEN.generateKey(token), map, Constant.APP_EXPIRE_TIME);
            //已登录的，就保存该token从redis查到的用户信息
            User user = BeanUtil.mapToBean(map, User.class, Boolean.TRUE);
            SubjectUtil.setCurrentUser(user);
            log.info("AclFilter验证通过，续存token {}", token);
            SubjectUtil.setToken(token);
        }
    }

}