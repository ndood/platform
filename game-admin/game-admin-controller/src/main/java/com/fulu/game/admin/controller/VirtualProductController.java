package com.fulu.game.admin.controller;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.VirtualProduct;
import com.fulu.game.core.service.VirtualProductService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 礼物Controller
 *
 * @author Gong ZeChun
 * @date 2018/9/2 12:47
 */
@RestController
@RequestMapping("/api/v1/virtual-product")
public class VirtualProductController extends BaseController {

    private final VirtualProductService virtualProductService;

    @Autowired
    public VirtualProductController(VirtualProductService virtualProductService) {
        this.virtualProductService = virtualProductService;
    }

    /**
     * 获取礼物列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @return 封装结果集
     */
    @PostMapping("/gift/list")
    public Result list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo<VirtualProduct> pageInfo = virtualProductService.findGiftByPage(pageNum, pageSize);
        return Result.success().data(pageInfo).msg("查询成功！");
    }

    /**
     * 新增礼物
     *
     * @param virtualProduct 礼物Bean
     * @return 封装结果集
     */
    @PostMapping("/gift/add")
    public Result add(VirtualProduct virtualProduct) {
        VirtualProduct resultVirtualProduct = virtualProductService.add(virtualProduct);
        return Result.success().data(resultVirtualProduct).msg("新增成功！");
    }

    /**
     * 编辑礼物
     *
     * @param virtualProduct 礼物Bean
     * @return 封装结果集
     */
    @PostMapping("/gift/update")
    public Result update(VirtualProduct virtualProduct) {
        virtualProductService.update(virtualProduct);
        return Result.success().msg("编辑成功！");
    }

    /**
     * 逻辑删除礼物
     *
     * @param id 礼物id
     * @return 封装结果集
     */
    @PostMapping("/gift/delete")
    public Result delete(@RequestParam Integer id) {
        VirtualProduct virtualProduct = new VirtualProduct();
        virtualProduct.setId(id);
        virtualProduct.setDelFlag(Boolean.TRUE);
        virtualProduct.setUpdateTime(DateUtil.date());
        virtualProductService.update(virtualProduct);
        return Result.success().msg("删除成功！");
    }
}
