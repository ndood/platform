package com.fulu.game.admin.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.CashServerAuthStatusEnum;
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

/**
 * yanbiao
 * 2018.4.24
 * 管理员-提现申请单处理
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/cashDraws")
public class CashDrawsController extends BaseController {

    @Autowired
    private CashDrawsService cashDrawsService;

    /**
     * 管理员(运营)-查看提现申请列表
     *
     * @return
     */
    @PostMapping("/list")
    public Result list(CashDrawsVO cashDrawsVO,
                       @RequestParam("pageNum") Integer pageNum,
                       @RequestParam("pageSize") Integer pageSize) {
        PageInfo<CashDrawsVO> cashDrawsList = cashDrawsService.list(cashDrawsVO, pageNum, pageSize);
        return Result.success().data(cashDrawsList).msg("查询列表成功！");
    }


    /**
     * 财务-查看提现申请列表
     *
     * @return
     */
    @PostMapping("/financer-auth/list")
    public Result financerAuthlist(CashDrawsVO cashDrawsVO,
                                   @RequestParam("pageNum") Integer pageNum,
                                   @RequestParam("pageSize") Integer pageSize) {
        cashDrawsVO.setServerAuth(CashServerAuthStatusEnum.AUTH_SUCCESS.getType());
        PageInfo<CashDrawsVO> cashDrawsList = cashDrawsService.list(cashDrawsVO, pageNum, pageSize);
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
        PageInfo<CashDrawsVO> cashDrawsList = cashDrawsService.list(cashDrawsVO);
        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, CashDrawsVO.class, cashDrawsList.getList());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 财务-打款动作
     *
     * @return
     */
    @PostMapping("/draw")
    public Result draw(@RequestParam("cashId") Integer cashId,
                       @RequestParam(name = "comment", required = false) String comment) {
        CashDraws cashDraws = cashDrawsService.draw(cashId, comment);
        if (null == cashDraws) {
            return Result.error().msg("申请单不存在！");
        }
        return Result.success().data(cashDraws).msg("打款成功！");
    }

    /**
     * 财务-打款动作（直接打款到用户微信零钱）
     *
     * @param cashId  提现id
     * @param sign    签名
     * @param comment 备注
     * @return 封装结果集
     */
    @PostMapping("/direct/draw")
    public Result directDraw(@RequestParam Integer cashId,
                             @RequestParam String sign,
                             @RequestParam(name = "comment", required = false) String comment) {
        CashDraws cashDraws = cashDrawsService.directDraw(cashId, sign, comment);
        if (null == cashDraws) {
            return Result.error().msg("打款失败！");
        }
        return Result.success().data(cashDraws).msg("打款成功！");
    }

    /**
     * 管理员-拒绝打款
     *
     * @param cashId
     * @param comment
     * @return
     */
    @PostMapping("/refuse")
    public Result refuse(@RequestParam("cashId") Integer cashId,
                         @RequestParam(name = "comment", required = false) String comment) {
        boolean flag = cashDrawsService.refuse(cashId, comment);
        if (!flag) {
            return Result.error().msg("操作失败，订单不存在");
        }
        return Result.success().msg("操作成功");
    }

    /**
     * 管理员(运营)-审批通过
     *
     * @param cashId
     * @return
     */
    @PostMapping("/admin-auth/succ")
    public Result adminAuthSucc(@RequestParam("cashId") Integer cashId) {

        CashDraws cd = new CashDraws();
        cd.setCashId(cashId);
        cd.setServerAuth(CashServerAuthStatusEnum.AUTH_SUCCESS.getType());

        cashDrawsService.update(cd);

        return Result.success().msg("操作成功");
    }
}

