package com.fulu.game.core.dao;

import com.fulu.game.core.entity.AdviceFile;
import com.fulu.game.core.entity.vo.AdviceFileVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author yanbiao
 * @date 2018-07-02 13:37:16
 */
@Mapper
public interface AdviceFileDao extends ICommonDao<AdviceFile, Integer> {

    List<AdviceFile> findByParameter(AdviceFileVO adviceFileVO);

    void insertList(List<AdviceFile> list);
}
