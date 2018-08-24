package com.fulu.game.app.shiro;


import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

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

        return true;
    }

}