package com.fulu.game.admin.shiro;

import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
        String token = request.getParameter("token");
        Map<String, Object> map = redisOpenService.hget(RedisKeyEnum.TOKEN.generateKey(token));
        // 没有登录授权 且没有记住我
        if (map == null) {
            log.info("验证登录失败token：{}", token);
            // 如果没有登录，直接进行之后的流程
            return true;
        }
        //在存5分钟，保证会话时长
        redisOpenService.hset(RedisKeyEnum.TOKEN.generateKey(token), map);

        //已登录的，就保存该token从redis查到的用户信息
        Admin admin = BeanUtil.mapToBean(map, Admin.class, true);
        SubjectUtil.setCurrentUset(admin);
        return false;
    }

}