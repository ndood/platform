package com.fulu.game.h5.controller.mp;

import com.fulu.game.h5.service.impl.fenqile.H5PayServiceImpl;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信Controller
 *
 * @author Gong ZeChun
 * @date 2018/9/5 14:17
 */
@Controller
@RequestMapping(value = "/wechat/mp")
@Slf4j
public class WechatController {

    @Autowired
    private H5PayServiceImpl h5PayService;

    @ResponseBody
    @RequestMapping("/pay/callback")
    public String payNotify(HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            return h5PayService.payResult(xmlResult);
        } catch (Exception e) {
            log.error("xml消息转换异常{}", e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }
}
