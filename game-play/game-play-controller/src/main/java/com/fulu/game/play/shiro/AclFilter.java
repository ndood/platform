package com.fulu.game.play.shiro;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.xiaoleilu.hutool.util.BeanUtil;
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

    @Override
    protected boolean isAccessAllowed(ServletRequest request,
                                      ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request,
                                     ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("token");
        Map<String, Object> map = redisOpenService.hget(RedisKeyEnum.TOKEN.generateKey(token));
        // 没有登录授权 且没有记住我
        if (MapUtils.isEmpty(map)) {
            log.info("验证登录失败token：{}", token);
            // 没有登录
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json; charset=utf-8");
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");

            PrintWriter out = null;

            try{
                out = httpResponse.getWriter();
                out.append(JSONObject.fromObject(Result.noLogin()).toString());
            }catch(IOException e){
                e.printStackTrace();
            }finally {
                if (out != null) {
                    out.close();
                }
            }
            return false;
        }
        //在存5分钟，保证会话时长
        redisOpenService.hset(RedisKeyEnum.TOKEN.generateKey(token), map);

        //已登录的，就保存该token从redis查到的用户信息
        Admin admin = BeanUtil.mapToBean(map, Admin.class, true);
        SubjectUtil.setCurrentUset(admin);
        return true;
    }

}