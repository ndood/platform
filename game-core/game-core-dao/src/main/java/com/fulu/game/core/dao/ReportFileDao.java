package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ReportFile;
import com.fulu.game.core.entity.vo.ReportFileVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 举报文件表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-27 13:56:48
 */
@Mapper
public interface ReportFileDao extends ICommonDao<ReportFile, Integer> {

    List<ReportFile> findByParameter(ReportFileVO reportFileVO);

}
