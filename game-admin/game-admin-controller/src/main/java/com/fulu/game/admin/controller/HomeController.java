package com.fulu.game.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.config.WxMpServiceSupply;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Admin;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private WxMpServiceSupply wxMpServiceSupply;
    @Autowired
    private WxMaServiceSupply wxMaServiceSupply;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(String username,
                        String password,
                        @RequestParam(value = "rememberMe", required = false, defaultValue = "false") Boolean rememberMe) throws Exception {
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            Admin admin = (Admin) subject.getPrincipal();
            Map<String, Object> map = BeanUtil.beanToMap(admin);
            map.put("token", SubjectUtil.getToken());
            return Result.success().data(map).msg("登录成功!");
        } catch (AuthenticationException e) {
            log.error("shiro验证失败", e);
            return Result.error().msg("登录失败,请联系管理员");
        } catch (Exception e) {
            log.error("登录异常", e);
            return Result.error().msg("登录异常");
        }
    }

    //fixme gzc demo

    /**
     * 获取公钥（Base64编码后的字符串）
     *
     * @return 封装结果集
     */
    @RequestMapping("/public-key/get")
    @ResponseBody
    public Result getPublickey() {
        Map<String, Object> rsaMap = Constant.RSA_MAP;
        if (MapUtils.isEmpty(rsaMap)) {
            rsaMap.put("rsa", new RSA());
        }

        Map<String, Object> resultMap = new HashMap<>();
        RSA rsa = (RSA) rsaMap.get("rsa");
        resultMap.put("publicKey", rsa.getPublicKeyBase64());
        resultMap.put("privateKey", rsa.getPrivateKeyBase64());
        return Result.success().data(resultMap).msg("获取成功!");
    }

    //fixme gzc demo

    /**
     * 解密
     *
     * @param encrypted 前端传回的加密字符串
     *                  （查过资料，前端使用jsencrypt.js的话，会把结果默认用Base64编码成字符串，所以后端要先用Base64解码之后再处理）
     * @return 封装结果集
     */
    @RequestMapping("/test")
    @ResponseBody
    public Result test(String encrypted) throws UnsupportedEncodingException {
        System.out.println("后端接收到的加密字符串：" + encrypted);
        System.out.println(encrypted);
        Map<String, Object> rsaMap = Constant.RSA_MAP;
        if (MapUtils.isEmpty(rsaMap)) {
            return Result.error().msg("嗯哼");
        }

        RSA rsa = (RSA) rsaMap.get("rsa");
        byte[] result = rsa.decrypt(Base64.decode(encrypted, "UTF-8"), KeyType.PrivateKey);
        String str = new String(result, "UTF-8");
        System.out.println("后端解密之后的：" + str);

//        JSONObject jsonObject = (JSONObject) JSONObject.parse(str);
//        String name = jsonObject.getString("name");
//        String age = jsonObject.getString("age");
        return Result.success().data(str).msg("获取成功!");
    }

    @PostMapping("/transfer")
    public Result transferToUser() throws WxPayException {

        //龚小明的公众号的openid
//        String publicOpenId = "os6HU00YbkERVByDFGmLVgju5-jY";

        //肖松的公众号的openid
        String publicOpenId = "os6HU0wMkzhapiUK2KlJadbmSgtw";

        //龚泽淳的陪玩的openid
        String playOpenId = "oZKvq4sB4rbCSxe0Zdf1MAtNCWrA";//龚泽淳的陪玩openId
//        String pointOpenId = "oTxDr4mo8Z-J7xLlVJRcOntPiC6M"; //龚泽淳的上分openId
        String partnerTradeNo = "CTEST1809115004381111";

        EntPayRequest request = EntPayRequest.newBuilder()
                .partnerTradeNo(partnerTradeNo)
                .openid(playOpenId)
                .amount(30)
                .spbillCreateIp("10.10.10.10")
                .checkName(WxPayConstants.CheckNameOption.NO_CHECK)
                .description("gzc：这里自定义")
                .build();

//        String result = this.wxMpServiceSupply.wxMpPayService().getEntPayService().entPay(request).toString();
        String result = this.wxMaServiceSupply.playWxPayService().getEntPayService().entPay(request).toString();
        System.out.println(result);
        return Result.success();
    }
}