package com.fulu.game.play.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.exception.ParamsExceptionEnums;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.UserTechAuthService;
import com.fulu.game.play.controller.exception.ParamsException;
import com.fulu.game.play.shiro.PlayUserToken;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController{

    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private WxMaService wxService;

    @RequestMapping("tech/list")
    public Result userTechList(){
        //查询所有用户认证的技能
        List<UserTechAuth> techAuthList = userTechAuthService.findByUserId(Constant.DEF_USER_ID,true);
        return Result.success().data(techAuthList);
    }

    /**
     * 用户-注册
     * @param userVO
     * @return
     */
    @RequestMapping("/save")
    public Result save(@ModelAttribute UserVO userVO){
        User user = userService.save(userVO);
        return Result.success().data(user).msg("恭喜您注册成功！");
    }

    /**
     * 用户-查询余额
     * @param id
     * @return
     */
    @RequestMapping(value = "/balance/get",method = RequestMethod.PUT)
    public Result getBalance(@RequestParam("id") Integer id){
        User user = userService.findById(id);
        return Result.success().data(user.getBalance()).msg("查询成功！");
    }

    /**
     * 小程序提交参数code
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Result login(@RequestParam("code") String code) throws WxErrorException {
        if (StringUtils.isBlank(code)) {
            throw new ParamsException(ParamsExceptionEnums.PARAM_NULL_EXCEPTION);
        }
        WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
        String sessionKey = session.getSessionKey();
        String openId = session.getOpenid();

        //1.认证和凭据的token
        PlayUserToken playUserToken = new PlayUserToken(openId, sessionKey);
        Subject subject = SecurityUtils.getSubject();
        //2.提交认证和凭据给身份验证系统
        try{
            subject.login(playUserToken);
            User user = (User)subject.getPrincipal();
            Map<String, Object> map = BeanUtil.beanToMap(user);
            map.put("token", SubjectUtil.getToken());
            return Result.success().data(map).msg("登录成功!");
        }catch (AuthenticationException e) {
            return Result.error().msg("用户登陆信息错误！");
        }catch (Exception e){
            log.error("登录异常",e);
            return Result.error();
        }
    }

    /**
     * 绑定手机号后根据token更新用户信息
     * @param userVO
     * @return
     */
    @RequestMapping(value = "/mobile",method = RequestMethod.POST)
    public Result mobile(@ModelAttribute UserVO userVO){
        //验证token后保存用户信息
        return null;
    }

}
