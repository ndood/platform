package com.fulu.game.play.controller;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.ResultStatus;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.exception.UserExceptionEnums;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.UserInfoVO;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.entity.vo.WxUserInfo;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.UserTechAuthService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {

    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserInfoAuthService userInfoAuthService;

    @RequestMapping("tech/list")
    public Result userTechList() {
        User user = userService.getCurrentUser();
        //查询所有用户认证的技能
        List<UserTechAuth> techAuthList = userTechAuthService.findByUserId(user.getId(), true);
        return Result.success().data(techAuthList);
    }

    /**
     * 用户-查询余额
     *
     * @return
     */
    @PostMapping("/balance/get")
    public Result getBalance() {
        User user = userService.getCurrentUser();
        return Result.success().data(user.getBalance()).msg("查询成功！");
    }

    /**
     * 用户-进入我的页面
     *
     * @return
     */
    @PostMapping("/get")
    public Result get(@RequestParam(name = "mobile", required = false, defaultValue = "false") Boolean mobile,
                      @RequestParam(name = "idcard", required = false, defaultValue = "false") Boolean idcard,
                      @RequestParam(name = "gender", required = false, defaultValue = "false") Boolean gender,
                      @RequestParam(name = "realname", required = false, defaultValue = "false") Boolean realname,
                      @RequestParam(name = "age", required = false, defaultValue = "false") Boolean age) {
        User user = userService.getCurrentUser();
        user.setId(null);
        user.setBalance(null);
        user.setOpenId(null);
        user.setPassword(null);
        user.setSalt(null);
        if (null != idcard && !idcard)
            user.setIdcard(null);
        if (!realname)
            user.setRealname(null);
        if (!gender)
            user.setGender(null);
        if (!mobile)
            user.setMobile(null);
        if (!age)
            user.setAge(null);
        return Result.success().data(user).msg("查询信息成功！");
    }

    /**
     * 用户-更新个人信息
     *
     * @param userVO
     * @return
     */
    @RequestMapping("/update")
    public Result update(@ModelAttribute UserVO userVO) {
        User user = userService.getCurrentUser();
        user.setAge(userVO.getAge());
        user.setGender(userVO.getGender());
        user.setCity(userVO.getCity());
        user.setProvince(userVO.getProvince());
        user.setCountry(userVO.getCountry());
        user.setBirth(userVO.getBirth());
        user.setConstellation(userVO.getConstellation());
        user.setNickname(userVO.getNickname());
        user.setHeadPortraitsUrl(userVO.getHeadPortraitsUrl());
        userService.update(user);
        updateRedisUser(user);

        user.setId(null);
        user.setBalance(null);
        user.setOpenId(null);
        user.setPassword(null);
        user.setSalt(null);
        user.setIdcard(null);
        user.setRealname(null);
        return Result.success().data(user).msg("个人信息设置成功！");
    }

    /**
     * 点击发送验证码接口
     *
     * @param mobile
     * @return
     */
    @PostMapping("/mobile/sms")
    public Result sms(@RequestParam("mobile") String mobile) {
        String token = SubjectUtil.getToken();
        //缓存中查找该手机是否有验证码
        if (redisOpenService.hasKey(RedisKeyEnum.SMS.generateKey(mobile))) {
            String times = redisOpenService.get(RedisKeyEnum.SMS.generateKey(mobile));
            if (Integer.parseInt(times) > Constant.MOBILE_CODE_SEND_TIMES_DEV) {
                return Result.error().msg("半小时内发送次数不能超过" + Constant.MOBILE_CODE_SEND_TIMES_DEV + "次，请等待！");
            } else {
                String verifyCode = SMSUtil.sendVerificationCode(mobile);
                log.info("发送验证码：" + verifyCode);
                redisOpenService.hset(RedisKeyEnum.SMS.generateKey(token), mobile, verifyCode, Constant.VERIFYCODE_CACHE_TIME_DEV);
                times = String.valueOf(Integer.parseInt(times) + 1);
                redisOpenService.set(RedisKeyEnum.SMS.generateKey(mobile), times, Constant.MOBILE_CACHE_TIME_DEV);
                return Result.success().msg("验证码发送成功！");
            }
        } else {
            String verifyCode = SMSUtil.sendVerificationCode(mobile);
            log.info("发送验证码：" + verifyCode);
            redisOpenService.hset(RedisKeyEnum.SMS.generateKey(token), mobile, verifyCode, Constant.VERIFYCODE_CACHE_TIME_DEV);
            redisOpenService.set(RedisKeyEnum.SMS.generateKey(mobile), "1", Constant.MOBILE_CACHE_TIME_DEV);
            return Result.success().msg("验证码发送成功！");
        }
    }

    @PostMapping("/mobile/bind")
    public Result bind(@ModelAttribute WxUserInfo wxUserInfo) {
        String token = SubjectUtil.getToken();
        //验证手机号的验证码
        String redisVerifyCode = redisOpenService.hget(RedisKeyEnum.SMS.generateKey(token), wxUserInfo.getMobile());
        if (null == redisVerifyCode) {
            return Result.error().msg("验证码失效");
        } else {
            String verifyCode = wxUserInfo.getVerifyCode();
            if (verifyCode != null && !verifyCode.equals(redisVerifyCode)) {
                return Result.error().msg("验证码提交错误");
            } else {//绑定手机号
                String openId = redisOpenService.hget(RedisKeyEnum.PLAY_TOKEN.generateKey(token)).get("openId").toString();
                User newUser = null;
                User openIdUser = userService.findByOpenId(openId);
                User mobileUser = userService.findByMobile(wxUserInfo.getMobile());
                if (null != mobileUser) {
                    newUser = mobileUser;
                } else {
                    newUser = openIdUser;
                }
                newUser.setMobile(wxUserInfo.getMobile());
                newUser.setGender(Integer.parseInt(wxUserInfo.getGender()));
                newUser.setNickname(wxUserInfo.getNickName());
                newUser.setHeadPortraitsUrl(wxUserInfo.getAvatarUrl());
                newUser.setCity(wxUserInfo.getCity());
                newUser.setProvince(wxUserInfo.getProvince());
                newUser.setCountry(wxUserInfo.getCountry());
                newUser.setUpdateTime(new Date());
                userService.update(newUser);
                //后台添加的记录只有mobile没有openId的需要删除
                if (null != mobileUser) {
                    userService.deleteById(openIdUser.getId());
                    //updateRedisUser();
                }
                newUser.setOpenId(null);
                newUser.setBalance(null);
                return Result.success().data(newUser).msg("手机号绑定成功！");
            }
        }

    }

    @PostMapping("/im/save")
    public Result imSave(@RequestParam("status") Integer status,
                         @RequestParam("imId") String imId,
                         @RequestParam("imPsw") String imPsw) {
        User user = userService.getCurrentUser();
        if (status == 200) {
            user.setImId(imId);
            user.setImPsw(imPsw);
            userService.update(user);
        }else if(status == 500){
            log.info("id===" + user.getId() + "注册IM用户失败！");
            return Result.error(ResultStatus.IM_REGIST_FAIL).msg("IM用户注册失败！");
        }
        return Result.success().msg("IM用户信息保存成功！");
    }

    @PostMapping("chatwith/get")
    public Result chatWithGet(@RequestParam("id") Integer id) {
        UserInfoVO userInfoVO = userInfoAuthService.findUserCardByUserId(id, false, true, true, true);
        return Result.success().data(userInfoVO).msg("查询聊天对象信息成功！");
    }

    public void updateRedisUser(User user){
        String token = SubjectUtil.getToken();
        Map<String, Object> userMap = new HashMap<>();
        userMap = BeanUtil.beanToMap(user);
        redisOpenService.hset(RedisKeyEnum.PLAY_TOKEN.generateKey(token),userMap);
    }

}
