package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Report;
import lombok.Data;


/**
 * 举报表
 *
 * @author Gong Zechun
 * @date 2018-08-27 13:55:55
 */
@Data
public class ReportVO extends Report {

    /**
     * 举报证据文件路径
     */
    String[] fileUrl;
}
