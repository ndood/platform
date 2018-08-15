package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.FenqileOrderVO;
import com.fulu.game.core.entity.vo.searchVO.FenqileOrderSearchVO;
import com.fulu.game.core.service.impl.FenqileOrderServiceImpl;
import com.fulu.game.core.service.impl.FenqileReconciliationServiceImpl;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分期乐订单Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/15 10:20
 */
@RestController
@RequestMapping("/api/v1/fenqile/order")
public class FenqileOrderController extends BaseController {
    private final FenqileOrderServiceImpl fenqileOrderService;
    private final FenqileReconciliationServiceImpl fenqileReconciliationService;

    @Autowired
    public FenqileOrderController(FenqileOrderServiceImpl fenqileOrderService,
                                  FenqileReconciliationServiceImpl fenqileReconciliationService) {
        this.fenqileOrderService = fenqileOrderService;
        this.fenqileReconciliationService = fenqileReconciliationService;
    }

    /**
     * 获取分期乐订单列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @param orderBy  排序字符串
     * @param searchVO 查询VO
     * @return 封装结果集
     */
    @PostMapping("/list")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize,
                       String orderBy,
                       FenqileOrderSearchVO searchVO) {
        PageInfo pageInfo = fenqileOrderService.list(pageNum, pageSize, orderBy, searchVO);
        return Result.success().data(pageInfo).msg("查询列表成功！");
    }

    /**
     * 获取对账金额汇总
     *
     * @param searchVO 查询VO
     * @return 封装结果集
     */
    @PostMapping("/total-recon-amount")
    public Result getTotalReconAmount(FenqileOrderSearchVO searchVO) {
        FenqileOrderVO vo = fenqileOrderService.getTotalReconAmount(searchVO);
        return Result.success().data(vo).msg("查询列表成功！");
    }

    /**
     * 对账
     *
     * @param orderNos 订单号（多个以英文逗号隔开）
     * @param remark   备注
     * @return 封装结果集
     */
    @PostMapping("/recon")
    public Result recon(String orderNos, String remark) {
        fenqileReconciliationService.recon(orderNos, remark);
        return Result.success().msg("对账成功！");
    }
}
