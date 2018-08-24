package com.fulu.game.admin.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.FenqileOrderVO;
import com.fulu.game.core.entity.vo.FenqileReconRecordVO;
import com.fulu.game.core.entity.vo.searchVO.FenqileOrderSearchVO;
import com.fulu.game.core.service.FenqileOrderService;
import com.fulu.game.core.service.FenqileReconRecordService;
import com.fulu.game.core.service.FenqileReconciliationService;
import com.fulu.game.core.service.impl.FenqileOrderServiceImpl;
import com.fulu.game.core.service.impl.FenqileReconRecordServiceImpl;
import com.fulu.game.core.service.impl.FenqileReconciliationServiceImpl;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * 分期乐订单Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/15 10:20
 */
@RestController
@RequestMapping("/api/v1/fenqile/order")
public class FenqileOrderController extends BaseController {
    private final FenqileOrderService fenqileOrderService;
    private final FenqileReconciliationService fenqileReconciliationService;
    private final FenqileReconRecordService fenqileReconRecordService;

    @Autowired
    public FenqileOrderController(FenqileOrderServiceImpl fenqileOrderService,
                                  FenqileReconciliationServiceImpl fenqileReconciliationService, FenqileReconRecordServiceImpl fenqileReconRecordService) {
        this.fenqileOrderService = fenqileOrderService;
        this.fenqileReconciliationService = fenqileReconciliationService;
        this.fenqileReconRecordService = fenqileReconRecordService;
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
        PageInfo<FenqileOrderVO> pageInfo = fenqileOrderService.list(pageNum, pageSize, orderBy, searchVO);
        return Result.success().data(pageInfo).msg("查询列表成功！");
    }

    /**
     * 分期乐订单列表导出
     *
     * @param searchVO 查询VO
     * @param response response
     */
    @RequestMapping("/export")
    public void export(FenqileOrderSearchVO searchVO, HttpServletResponse response) {
        String title = "分期乐订单列表";
        List<FenqileOrderVO> resultList = fenqileOrderService.list(searchVO);
        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, FenqileOrderVO.class, resultList);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * @param orderNos           订单号（多个以英文逗号隔开）
     * @param startTime          订单时间开始区间
     * @param endTime            订单时间结束区间
     * @param remark             备注
     * @param unReconCount       应对账（未对账）订单数量
     * @param unReconTotalAmount 应对账（未对账）金额
     * @return 封装结果集
     */
    @PostMapping("/recon")
    public Result recon(String orderNos, Date startTime, Date endTime, String remark,
                        Integer unReconCount, BigDecimal unReconTotalAmount) {
        fenqileReconciliationService.recon(orderNos, startTime, endTime, remark, unReconCount, unReconTotalAmount);
        return Result.success().msg("对账成功！");
    }

    /**
     * 获取对账记录
     *
     * @param pageNum   页码
     * @param pageSize  每页显示数据条数
     * @param startTime 对账开始时间
     * @param endTime   对账结束时间
     * @return 封装结果集
     */
    @PostMapping("/recon-record")
    public Result reconRecord(@RequestParam Integer pageNum,
                              @RequestParam Integer pageSize,
                              Date startTime,
                              Date endTime) {
        PageInfo<FenqileReconRecordVO> pageInfo = fenqileReconRecordService.reconRecord(pageNum, pageSize, startTime, endTime);
        return Result.success().data(pageInfo).msg("查询成功！");
    }
}
