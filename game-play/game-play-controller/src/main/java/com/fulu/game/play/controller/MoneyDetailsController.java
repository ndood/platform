package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.MoneyDetailsVO;
import com.fulu.game.core.service.MoneyDetailsService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/moneyDetails")
public class MoneyDetailsController extends BaseController{

    @Autowired
    private MoneyDetailsService moneyDetailsService;

    @RequestMapping("/list")
    public Result list(@ModelAttribute MoneyDetailsVO moneyDetailsVO,
                       @RequestParam("pageSize") Integer pageSize,
                       @RequestParam("pageNum") Integer pageNum){
        moneyDetailsVO.setTargetId(((User)SubjectUtil.getCurrentUser()).getId());
        PageInfo<MoneyDetailsVO> list = moneyDetailsService.listByUser(moneyDetailsVO,pageSize,pageNum);
        return Result.success().data(list).msg("查询列表成功！");
    }
}
