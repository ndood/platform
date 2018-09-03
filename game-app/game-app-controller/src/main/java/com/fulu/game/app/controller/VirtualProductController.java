package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.VirtualProduct;
import com.fulu.game.core.service.VirtualProductOrderService;
import com.fulu.game.core.service.VirtualProductService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 礼物Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/30 11:31
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/virtual-product")
public class VirtualProductController extends BaseController {

    private final VirtualProductService virtualProductService;
    private final VirtualProductOrderService virtualProductOrderService;

    @Autowired
    public VirtualProductController(VirtualProductService virtualProductService,
                                    VirtualProductOrderService virtualProductOrderService) {
        this.virtualProductService = virtualProductService;
        this.virtualProductOrderService = virtualProductOrderService;
    }

    /**
     * 分页获取礼物列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @return 封装结果集
     */
    @PostMapping("/gift/list")
    public Result findGiftList(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo<VirtualProduct> pageInfo = virtualProductService.findGiftByPage(pageNum, pageSize);
        return Result.success().data(pageInfo).msg("查询成功！");
    }

    /**
     * 赠送礼物
     *
     * @param fromUserId       发起人id
     * @param targetUserId     接收人id
     * @param virtualProductId 虚拟商品id
     * @return 封装结果集
     */
    @PostMapping("/gift/send")
    public Result sendGift(@RequestParam Integer fromUserId,
                           @RequestParam Integer targetUserId,
                           @RequestParam Integer virtualProductId) {
        virtualProductOrderService.sendGift(fromUserId, targetUserId, virtualProductId);
        return Result.success().msg("送礼物成功！");
    }
}
