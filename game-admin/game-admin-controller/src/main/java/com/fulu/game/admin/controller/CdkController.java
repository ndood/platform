package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.CdkGroup;
import com.fulu.game.core.entity.vo.CdkVO;
import com.fulu.game.core.service.CdkGroupService;
import com.fulu.game.core.service.CdkService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/cdk")
@Slf4j
public class CdkController {

    @Autowired
    private CdkGroupService cdkGroupService;
    @Autowired
    private CdkService cdkService;

    /**
     * 生成cdk
     *
     * @param cdkGroup
     * @return
     */
    @PostMapping("/group/create")
    public Result create(@Valid CdkGroup cdkGroup) {
        Boolean success = cdkGroupService.generate(cdkGroup);
        if (success) {
            return Result.success().msg("cdk生成成功");
        } else {
            return Result.error().msg("cdk生成异常");
        }
    }

    /**
     * cdk列表
     *
     * @param pageNum
     * @param pageSize
     * @param series
     * @return
     */
    @PostMapping("/list")
    public Result list(@RequestParam("pageNum") Integer pageNum,
                       @RequestParam("pageSize") Integer pageSize,
                       @RequestParam(value = "series", required = false) String series) {
        String orderBy = "update_time desc";
        PageInfo<CdkVO> resultPage = cdkService.list(pageNum, pageSize, series, orderBy);
        return Result.success().data(resultPage);
    }

    /**
     * cdk批次列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("/group/list")
    public Result groupList(@RequestParam("pageNum") Integer pageNum,
                            @RequestParam("pageSize") Integer pageSize) {
        String orderBy = "id desc";
        PageInfo<CdkGroup> resultPage = cdkGroupService.list(pageNum, pageSize, orderBy);
        return Result.success().data(resultPage);
    }

    /**
     * 统计已使用数量
     *
     * @param groupId
     * @return
     */
    @PostMapping("/group/count")
    public Result countGroupUse(@RequestParam("groupId") Integer groupId) {
        int count = cdkService.count(groupId, true);
        return Result.success().data(count);
    }
}
