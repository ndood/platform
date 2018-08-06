package com.fulu.game.play.shiro;

import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
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

/**
 * 调用小程序服务接口时都会先进入此类的方法
 */
@Slf4j
public class AclFilter extends AccessControlFilter {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    /**
     * 首先调用若返回true则继续执行filter和servlet，否则调用onAccessDenied
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
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
        Map<String, Object> map = redisOpenService.hget(RedisKeyEnum.PLAY_TOKEN.generateKey(token));
        redisOpenService.hset(RedisKeyEnum.PLAY_TOKEN.generateKey(token), map);
        //sessionKey 时间用户token时间保持一致
        String sessionKey = redisOpenService.get(RedisKeyEnum.WX_SESSION_KEY.generateKey(token));
        redisOpenService.set(RedisKeyEnum.WX_SESSION_KEY.generateKey(token),sessionKey);

        //已登录的，就保存该token从redis查到的用户信息
        User user = BeanUtil.mapToBean(map, User.class, true);
        SubjectUtil.setCurrentUser(user);
        log.info("AclFilter验证通过，续存token {}", token);
        SubjectUtil.setToken(token);
        return true;
    }

}