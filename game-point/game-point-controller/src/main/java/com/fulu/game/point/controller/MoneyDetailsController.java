package com.fulu.game.point.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.MoneyDetailsVO;
import com.fulu.game.core.service.MoneyDetailsService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户零钱明细Controller
 *
 * @author Gong ZeChun
 * @date 2018/7/30 17:06
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/moneyDetails")
public class MoneyDetailsController extends BaseController {

    @Autowired
    private MoneyDetailsService moneyDetailsService;

    @Autowired
    private UserService userService;

    /**
     * 获取零钱明细列表
     *
     * @param moneyDetailsVO 查询VO
     * @param pageSize       每页显示数据条数
     * @param pageNum        页码
     * @return 封装结果集
     */
    @RequestMapping("/list")
    public Result list(MoneyDetailsVO moneyDetailsVO,
                       @RequestParam("pageSize") Integer pageSize,
                       @RequestParam("pageNum") Integer pageNum) {
        User user = userService.getCurrentUser();
        moneyDetailsVO.setTargetId(user.getId());
        PageInfo<MoneyDetailsVO> list = moneyDetailsService.listByUser(moneyDetailsVO, pageSize, pageNum);
        return Result.success().data(list).msg("查询列表成功！");
    }
}
