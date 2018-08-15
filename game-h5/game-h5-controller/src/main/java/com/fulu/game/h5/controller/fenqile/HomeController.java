package com.fulu.game.h5.controller.fenqile;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.service.BannerService;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分期乐-HomeController
 *
 * @author Gong ZeChun
 * @date 2018/8/13 15:48
 */
@Controller
@Slf4j
public class HomeController extends BaseController {


    private final BannerService bannerService;




    @Autowired
    public HomeController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    /**
     * banner展示列表
     *
     * @return 封装结果集
     */
    @PostMapping("/banner/list")
    @ResponseBody
    public Result list() {
        BannerVO bannerVO = new BannerVO();
        bannerVO.setDisable(true);
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        return Result.success().data(bannerList);
    }


    @RequestMapping(value = "/fenqile/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@RequestParam("code") String code){

        return Result.success();
    }


}
