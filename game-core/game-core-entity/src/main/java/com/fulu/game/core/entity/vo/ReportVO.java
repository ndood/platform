package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Report;
import lombok.Data;

import java.util.Date;


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
    private String[] fileUrl;

    /**
     * 处理开始时间
     */
    private Date startTime;

    /**
     * 处理结束时间
     */
    private Date endTime;

    /**
     * 排序字符串
     */
    private String orderBy;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页显示数据条数
     */
    private Integer pageSize;

    /**
     * 举报人昵称
     */
    private String nickname;

    /**
     * 被举报人昵称
     */
    private String reportedUserNickname;
}
