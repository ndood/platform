package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.VirtualPayOrderVO;
import com.fulu.game.core.service.VirtualPayOrderService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 充值管理Controller
 *
 * @author Gong ZeChun
 * @date 2018/9/11 14:04
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/charge")
public class ChargeController extends BaseController {
    @Qualifier("virtualPayOrderServiceImpl")
    @Autowired
    private VirtualPayOrderService payOrderService;

    /**
     * 查询余额和虚拟币充值列表
     *
     * @param payOrderVO 查询VO
     * @param pageNum    页码
     * @param pageSize   每页获取数据条数
     * @param orderBy    排序字符串
     * @return 封装结果集
     */
    @PostMapping("/list")
    public Result balanceList(VirtualPayOrderVO payOrderVO,
                              @RequestParam Integer pageNum,
                              @RequestParam Integer pageSize,
                              String orderBy) {
        PageInfo<VirtualPayOrderVO> pageInfo = payOrderService.chargeList(payOrderVO, pageNum, pageSize, orderBy);
        return Result.success().data(pageInfo).msg("查询成功!");
    }
}
