package com.fulu.game.h5.controller.thunder;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PlatformBannerEnum;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.service.BannerService;
import com.fulu.game.h5.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * 首页banner列表
     *
     * @return 封装结果集
     */
    @RequestMapping("/home-page/banner/list")
    public Result homePageList() {
        BannerVO bannerVO = new BannerVO();
        bannerVO.setDisable(true);
        bannerVO.setPlatformType(PlatformBannerEnum.THUNDER_HOMEPAGE.getType());
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        return Result.success().data(bannerList).msg("查询成功！");
    }

    /**
     * 列表页banner列表
     *
     * @return 封装结果集
     */
    @RequestMapping("/list-page/banner/list")
    public Result list() {
        BannerVO bannerVO = new BannerVO();
        bannerVO.setDisable(true);
        bannerVO.setPlatformType(PlatformBannerEnum.THUNDER_LIST.getType());
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        return Result.success().data(bannerList).msg("查询成功！");
    }

    /**
     * 首页陪玩师列表
     *
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/product/list")
    public Result productList(Integer type,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "4") Integer pageSize) {

        return null;
    }
}
