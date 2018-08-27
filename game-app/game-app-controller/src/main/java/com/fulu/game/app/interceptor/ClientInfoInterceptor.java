package com.fulu.game.app.interceptor;

import com.fulu.game.app.util.RequestUtil;
import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.utils.SubjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class ClientInfoInterceptor extends HandlerInterceptorAdapter {



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            ClientInfo clientInfo = RequestUtil.request2Bean(request, ClientInfo.class);
            SubjectUtil.setUserClientInfo(clientInfo);
        }catch (Exception e){
            log.error("拦截器出错",e);
        }
        return true;
    }

}
