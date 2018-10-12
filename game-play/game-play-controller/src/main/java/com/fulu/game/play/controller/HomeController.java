package com.fulu.game.play.controller;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.common.enums.PlatformBannerEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.SysConfig;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.entity.vo.ProductShowCaseVO;
import com.fulu.game.core.service.*;
import com.fulu.game.play.shiro.PlayUserToken;
import com.fulu.game.play.utils.RequestUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class HomeController extends BaseController {

    @Autowired
    private WxMaServiceSupply wxMaServiceSupply;
    @Autowired
    private UserService userService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private BannerService bannerService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @PostMapping("/banner/list")
    @ResponseBody
    public Result list() {
        BannerVO bannerVO = new BannerVO();
        bannerVO.setDisable(true);
        bannerVO.setPlatformType(PlatformBannerEnum.PLAY.getType());
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        return Result.success().data(bannerList);
    }

    /**
     * 初始化加载系统配置
     *
     * @return
     */
    @RequestMapping(value = "/sys/config", method = RequestMethod.POST)
    @ResponseBody
    public Result sysConfig(@RequestParam(value = "version", required = false) String version) {
        List<SysConfig> result = sysConfigService.findByVersion(version, PlatformEcoEnum.PLAY.getType());
        if (CollectionUtil.isEmpty(result)) {
            result = new ArrayList<SysConfig>();
            SysConfig sysConfig1 = new SysConfig();
            sysConfig1.setName("MMCON");
            sysConfig1.setValue("CLOSE");
            SysConfig sysConfig2 = new SysConfig();
            sysConfig2.setName("PAYCON");
            sysConfig2.setValue("CLOSE");
            result.add(sysConfig1);
            result.add(sysConfig2);
        }
        return Result.success().data(result);
    }

    /**
     * 小程序提交参数code
     *
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@RequestParam("code") String code,
                        @RequestParam(value = "sourceId", required = false) Integer sourceId,
                        HttpServletRequest request) throws WxErrorException {
        if (StringUtils.isBlank(code)) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        WxMaJscode2SessionResult session = wxMaServiceSupply.playWxMaService().getUserService().getSessionInfo(code);
        String openId = session.getOpenid();
        //1.认证和凭据的token
        PlayUserToken playUserToken = new PlayUserToken(openId, session.getSessionKey(), sourceId);
        String ip = RequestUtil.getIpAdrress(request);
        playUserToken.setHost(ip);
        Subject subject = SecurityUtils.getSubject();
        //2.提交认证和凭据给身份验证系统
        try {
            subject.login(playUserToken);
            User user = userService.getCurrentUser();
            userService.updateUserIpAndLastTime(ip);
            Map<String, Object> result = BeanUtil.beanToMap(user);
            result.put("token", SubjectUtil.getToken());
            result.put("userId", user.getId());

            //对已授权用户判断是否赠送登录奖励
            String unionId = user.getUnionId();
            boolean flag;
            if (StringUtils.isBlank(unionId)) {
                flag = false;
            } else {
                flag = userService.loginReceiveVirtualMoney(user);
            }
            result.put("bonus", flag ? 1 : 0);

            if (user.getUnionId() == null) {
                log.error("用户没有unionId;user{}", user);
                return Result.newUser().data(result);
            }
            return Result.success().data(result).msg("登录成功!");
        } catch (AuthenticationException e) {
            if (e.getCause() instanceof UserException) {
                if (UserException.ExceptionCode.USER_BANNED_EXCEPTION.equals(((UserException) e.getCause()).getExceptionCode())) {
                    log.error("用户被封禁,openId:{}", openId);
                    return Result.userBanned();
                }
            }
            return Result.noLogin().msg("用户验证信息错误！");
        } catch (Exception e) {
            log.error("登录异常!", e);
            return Result.error().msg("登陆异常！");

        }
    }


    @RequestMapping(value = "/test/login", method = RequestMethod.POST)
    @ResponseBody
    public Result testLogin(String openId, @RequestParam(value = "sourceId", required = false) Integer sourceId, HttpServletRequest request) {
        log.info("==调用/test/login方法==");
        PlayUserToken playUserToken = new PlayUserToken(openId, "", sourceId);
        String ip = RequestUtil.getIpAdrress(request);
        playUserToken.setHost(ip);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(playUserToken);
            User user = userService.getCurrentUser();
            Map<String, Object> result = BeanUtil.beanToMap(user);
            result.put("token", SubjectUtil.getToken());
            result.put("userId", user.getId());
            return Result.success().data(result).msg("测试登录成功!");
        } catch (AuthenticationException e) {
            if (e.getCause() instanceof UserException) {
                if (UserException.ExceptionCode.USER_BANNED_EXCEPTION.equals(((UserException) e.getCause()).getExceptionCode())) {
                    log.error("用户被封禁,openId:{}", openId);
                    return Result.userBanned();
                }
            }
            return Result.noLogin().msg("测试登录用户验证信息错误！");
        } catch (Exception e) {
            log.error("测试登录异常!", e);
            return Result.error().msg("测试登陆异常！");
        }
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
        bannerVO.setPlatformType(PlatformBannerEnum.PLAY.getType());
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
