package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.UserNightInfoVO;
import com.fulu.game.core.service.MidnightService;
import com.fulu.game.core.service.UserNightInfoService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 午夜场管理Controller
 *
 * @author Gong ZeChun
 * @date 2018/9/17 14:36
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/night")
public class MidnightController extends BaseController {
    @Autowired
    private MidnightService midnightService;
    @Autowired
    private UserNightInfoService userNightInfoService;

    /**
     * 设置午夜场起止时间
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 封装结果集
     */
    @PostMapping("/time/config")
    public Result setConfig(@RequestParam String startTime, @RequestParam String endTime) {
        midnightService.setConfig(startTime, endTime);
        return Result.success().msg("设置成功");
    }

    /**
     * 获取午夜场陪玩师列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @return 封装结果集
     */
    @PostMapping("/service-user/list")
    public Result list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo<UserNightInfoVO> pageInfo = userNightInfoService.list(pageNum, pageSize);
        return Result.success().data(pageInfo).msg("查询成功！");
    }

    /**
     * 移除陪玩师
     *
     * @param id 午夜场陪玩师信息表主键id
     * @return 封装结果集
     */
    @PostMapping("/service-user/remove")
    public Result remove(@RequestParam Integer id) {
        userNightInfoService.remove(id);
        return Result.success().msg("移除成功！");
    }
}
