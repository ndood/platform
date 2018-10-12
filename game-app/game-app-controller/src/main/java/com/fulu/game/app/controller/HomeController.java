package com.fulu.game.app.controller;


import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.app.shiro.AppUserToken;
import com.fulu.game.app.utils.RequestUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.common.enums.PlatformBannerEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.entity.vo.ProductShowCaseVO;
import com.fulu.game.core.service.BannerService;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.ProductService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping
public class HomeController extends BaseController {


    @Autowired
    private UserService userService;

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestParam(required = true) String code,
                        @RequestParam(required = true) String mobile,
                        HttpServletRequest request) {
        AppUserToken appUserToken = new AppUserToken(mobile, code);
        String ip = RequestUtil.getIpAdrress(request);
        appUserToken.setHost(ip);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(appUserToken);
            User user = userService.getCurrentUser();
            userService.updateUserIpAndLastTime(ip);
            user.setOpenId(null);
            user.setPointOpenId(null);
            user.setBalance(null);
            Map<String, Object> result = BeanUtil.beanToMap(user);
            result.put("token", SubjectUtil.getToken());
            if (user.getHeadPortraitsUrl() == null) {
                log.error("新注册用户;user{}", user);
                return Result.newUser().data(result);
            }
            return Result.success().data(result).msg("登录成功!");
        } catch (Exception e) {
            if (e.getCause() instanceof UserException) {
                if (UserException.ExceptionCode.USER_BANNED_EXCEPTION.equals(((UserException) e.getCause()).getExceptionCode())) {
                    log.error("用户被封禁,mobile:{}", mobile);
                    return Result.userBanned();
                }
            }
            log.error("登录异常", e);
            return Result.error().msg("验证码错误！");
        }
    }





    /**
     * 点击发送验证码接口
     * @param mobile
     * @return
     */
    @PostMapping("/sms/verify")
    public Result sms(@RequestParam("mobile") String mobile) {
        //缓存中查找该手机是否有验证码
        if (redisOpenService.hasKey(RedisKeyEnum.SMS.generateKey(mobile))) {
            String times = redisOpenService.get(RedisKeyEnum.SMS.generateKey(mobile));
            if (Integer.parseInt(times) > Constant.MOBILE_CODE_SEND_TIMES) {
                return Result.error().msg("半小时内发送次数不能超过" + Constant.MOBILE_CODE_SEND_TIMES + "次，请等待！");
            } else {
                String verifyCode = SMSUtil.sendVerificationCode(mobile);
                log.info("发送验证码{}={}", mobile, verifyCode);
                redisOpenService.set(RedisKeyEnum.SMS_VERIFY_CODE.generateKey(mobile), verifyCode, Constant.VERIFYCODE_CACHE_TIME);
                times = String.valueOf(Integer.parseInt(times) + 1);
                redisOpenService.set(RedisKeyEnum.SMS_VERIFY_CODE_TIMES.generateKey(mobile), times, Constant.MOBILE_CACHE_TIME);
                return Result.success().data(verifyCode).msg("验证码发送成功！");
            }
        } else {
            String verifyCode = SMSUtil.sendVerificationCode(mobile);
            log.info("发送验证码{}={}", mobile, verifyCode);
            redisOpenService.set(RedisKeyEnum.SMS_VERIFY_CODE.generateKey(mobile), verifyCode, Constant.VERIFYCODE_CACHE_TIME);
            redisOpenService.set(RedisKeyEnum.SMS_VERIFY_CODE_TIMES.generateKey(mobile), "1", Constant.MOBILE_CACHE_TIME);
            return Result.success().data(verifyCode).msg("验证码发送成功！");
        }
    }

    /**
     * 获取首页banner列表接口
     * @return
     */
    @PostMapping("/banner/list")
    @ResponseBody
    public Result list() {
        BannerVO bannerVO = new BannerVO();
        bannerVO.setDisable(true);
        bannerVO.setPlatformType(PlatformBannerEnum.APP.getType());
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        return Result.success().data(bannerList);
    }


    /**
     * 获取首页信息接口
     * @return
     */
    @PostMapping("/home/index")
    @ResponseBody
    public Result homeIndex() {
        BannerVO bannerVO = new BannerVO();
        bannerVO.setDisable(true);
        bannerVO.setPlatformType(PlatformBannerEnum.APP.getType());
        // 获取banner列表
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        Map<String, Object> map = new HashMap<>();
        map.put("bannerList",bannerList);
        //获取一级分类
        List<Category> categoryList = categoryService.findByPid(CategoryParentEnum.ACCOMPANY_PLAY.getType(), true);
        for (Category category : categoryList) {
            if (category.getIndexIcon() != null) {
                category.setIcon(category.getIndexIcon());
            }
        }
        map.put("firstList",categoryList);
        //获取所有小的分类
        List<Category> categorySecondList = categoryService.findAllAccompanyPlayCategory();
        for (Category category : categorySecondList) {
            if (category.getIndexIcon() != null) {
                category.setIcon(category.getIndexIcon());
            }
            PageInfo<ProductShowCaseVO> pageInfo = productService.findProductShowCase(category.getId(), null, 1, 8, null, null, null);
            if(pageInfo != null){
                category.setProductList(pageInfo.getList());
            }
        }
        map.put("categoryList",categorySecondList);
        return Result.success().data(map);
    }

}
