package com.fulu.game.h5.controller.thunder;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PlatformBannerEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.entity.vo.CategoryVO;
import com.fulu.game.core.entity.vo.ProductShowCaseVO;
import com.fulu.game.core.service.BannerService;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.ProductService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.h5.controller.BaseController;
import com.fulu.game.h5.shiro.PlayUserToken;
import com.fulu.game.h5.utils.RequestUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 迅雷Controller
 *
 * @author Gong ZeChun
 * @date 2018/10/9 14:22
 */
@RestController
@Slf4j
@RequestMapping("/thunder")
public class ThunderHomeController extends BaseController {
    @Autowired
    private BannerService bannerService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    /**
     * 测试登录
     *
     * @return
     */
    @RequestMapping(value = "/test/login", method = RequestMethod.POST)
    @ResponseBody
    public Result testLogin(String openId,
                            @RequestParam(value = "sourceId", required = false) Integer sourceId,
                            HttpServletRequest request) {
        log.info("==调用/test/login方法==");
        PlayUserToken playUserToken = PlayUserToken.newBuilder(PlayUserToken.Platform.MP).mpOpenId(openId).build();
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
     * banner列表
     *
     * @param type 类型：1：首页banner; 2：列表页banner
     * @return 封装结果集
     */
    @RequestMapping("/banner/list")
    public Result homePageList(@RequestParam(defaultValue = "1") Integer type) {
        BannerVO bannerVO = new BannerVO();
        bannerVO.setDisable(true);
        if (Constant.THUNDER_HOMEPAGE_BANNER.equals(type)) {
            bannerVO.setPlatformType(PlatformBannerEnum.THUNDER_HOMEPAGE.getType());
        } else {
            bannerVO.setPlatformType(PlatformBannerEnum.THUNDER_LIST.getType());
        }
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        return Result.success().data(bannerList).msg("查询成功！");
    }

    /**
     * 查询首页-陪玩专区-分类信息
     *
     * @return
     */
    @RequestMapping(value = "/category/all")
    public Result allCategory() {
        CategoryVO vo = categoryService.findThunderCategory();
        return Result.success().data(vo).msg("查询成功！");
    }

    /**
     * 首页陪玩师专区-商品列表
     *
     * @param categoryId 分类id
     * @param size       显示数量
     * @return 封装结果集
     */
    @RequestMapping("/product/list")
    public Result productList(@RequestParam Integer categoryId,
                              @RequestParam(defaultValue = "4") Integer size) {
        PageInfo<ProductShowCaseVO> pageInfo = productService.thunderProductList(categoryId, size);
        return Result.success().data(pageInfo).msg("查询成功！");
    }
}