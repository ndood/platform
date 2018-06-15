package com.fulu.game.admin.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.CdkGroup;
import com.fulu.game.core.entity.vo.CdkVO;
import com.fulu.game.core.service.CdkGroupService;
import com.fulu.game.core.service.CdkService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.List;

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

    @PostMapping("/group/abolish")
    public Result groupList(@RequestParam("groupId") Integer groupId) {
        //todo 废除后是否有其他操作
        cdkGroupService.abolish(groupId);
        return Result.success().msg("操作成功");
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

    /**
     * cdk列表导出
     *
     * @param response
     * @param groupId
     * @throws Exception
     */
    @RequestMapping("/export/{groupId}")
    public void grantExport(HttpServletResponse response,
                            @PathVariable(name = "groupId", required = true) Integer groupId) throws Exception {
        String title = "CDK列表";
        CdkVO cdkVO = new CdkVO();
        cdkVO.setGroupId(groupId);
        List<Cdk> cdkList = cdkService.findByParam(cdkVO);
        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, Cdk.class, cdkList);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
        workbook.write(response.getOutputStream());
    }
}
