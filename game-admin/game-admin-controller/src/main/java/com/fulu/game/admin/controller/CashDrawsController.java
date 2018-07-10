package com.fulu.game.admin.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import com.fulu.game.core.service.CashDrawsService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * yanbiao
 * 2018.4.24
 * 管理员-提现申请单处理
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/cashDraws")
public class CashDrawsController extends BaseController{

    @Autowired
    private CashDrawsService cashDrawsService;

     /**
     * 管理员-查看提现申请列表
     * @return
     */
    @PostMapping("/list")
    public Result list(CashDrawsVO cashDrawsVO,
                       @RequestParam("pageNum") Integer pageNum,
                       @RequestParam("pageSize") Integer pageSize){
        PageInfo<CashDraws> cashDrawsList = cashDrawsService.list(cashDrawsVO,pageNum,pageSize);
        return Result.success().data(cashDrawsList).msg("查询列表成功！");
    }

    /**
     * 提现申请单导出
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping("/export")
    public void orderExport(HttpServletResponse response,
                            CashDrawsVO cashDrawsVO) throws Exception {
        String title = "提现申请单列表";
        List<CashDraws> cashDrawsList = cashDrawsService.list(cashDrawsVO);
        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, CashDraws.class, cashDrawsList);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 管理员-打款动作
     * @return
     */
    @PostMapping("/draw")
    public Result draw(@RequestParam("cashId") Integer cashId,
            @RequestParam(name="comment",required = false) String comment){
        CashDraws cashDraws = cashDrawsService.draw(cashId,comment);
        if (null == cashDraws){
            return Result.error().msg("申请单不存在！");
        }
        return Result.success().data(cashDraws).msg("打款成功！");
    }
}

