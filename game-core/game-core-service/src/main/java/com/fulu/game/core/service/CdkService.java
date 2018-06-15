package com.fulu.game.core.service;

import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.CdkGroup;
import com.fulu.game.core.entity.vo.CdkVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * cdk记录表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:53
 */
public interface CdkService extends ICommonService<Cdk, Integer> {

    /**
     * 通过序列号查询
     *
     * @param series
     * @return
     */
    Cdk findBySeries(String series);

    /**
     * 批量插入
     *
     * @param cdkGroup
     * @return
     */
    int insertList(CdkGroup cdkGroup);

    /**
     * cdk列表
     *
     * @param pageNum
     * @param pageSize
     * @param series
     * @param orderBy
     * @return
     */
    PageInfo<CdkVO> list(Integer pageNum, Integer pageSize, String series, String orderBy);

    /**
     * 按使用状态统计
     *
     * @param groupId
     * @param b
     * @return
     */
    int count(Integer groupId, Boolean b);

    /**
     * 条件查询列表
     * @param cdkVO
     * @return
     */
    List<Cdk> findByParam(CdkVO cdkVO);
}
