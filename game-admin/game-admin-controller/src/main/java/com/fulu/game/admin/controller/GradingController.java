package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.GradingPrice;
import com.fulu.game.core.entity.vo.GradingPriceVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.GradingPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/grading-price")
public class GradingController extends BaseController {


    @Autowired
    private GradingPriceService gradingPriceService;
    @Autowired
    private AdminService adminService;

    /**
     * 新建段位价格
     * @param pid
     * @param name
     * @param rank
     * @param price
     * @return
     */
    @PostMapping(value = "create")
    public Result create(Integer pid,
                         String name,
                         Integer rank,
                         BigDecimal price) {
        GradingPrice gradingPrice = gradingPriceService.create(pid, name, rank, price);
        return Result.success().data(gradingPrice);
    }


    /**
     * 修改最后更新时间
     * @param id
     * @return
     */
    @PostMapping(value = "update-time")
    public Result updateTime(Integer id){
        Admin admin = adminService.getCurrentUser();
        GradingPrice gradingPrice = gradingPriceService.findById(id);
        gradingPrice.setAdminId(admin.getId());
        gradingPrice.setAdminName(admin.getName());
        gradingPrice.setUpdateTime(new Date());
        gradingPriceService.update(gradingPrice);
        return Result.success().data(gradingPrice);
    }

    /**
     * 修改段位价格
     * @param id
     * @param name
     * @param rank
     * @param price
     * @return
     */
    @PostMapping(value = "update")
    public Result update(Integer id,
                         String name,
                         Integer rank,
                         BigDecimal price) {
        GradingPrice gradingPrice = gradingPriceService.update(id, name, rank, price);
        return Result.success().data(gradingPrice);
    }


    /**
     * 段位价格列表
     * @param pid
     * @return
     */
    @PostMapping(value = "list")
    public Result list(Integer pid) {
        List<GradingPriceVO> list = gradingPriceService.findVoByPid(pid);
        return Result.success().data(list);
    }




}
