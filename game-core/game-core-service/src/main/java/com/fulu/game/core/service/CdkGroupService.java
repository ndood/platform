package com.fulu.game.core.service;

import com.fulu.game.core.entity.CdkGroup;
import com.github.pagehelper.PageInfo;

/**
 * cdk批次表
 *
 * @author yanbiao
 * @date 2018-06-13 15:34:02
 */
public interface CdkGroupService extends ICommonService<CdkGroup, Integer> {

    /**
     * 生成cdk批次和cdk
     *
     * @param cdkGroup
     * @return
     */
    Boolean generate(CdkGroup cdkGroup);

    /**
     * cdk批次列表
     *
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    PageInfo<CdkGroup> list(Integer pageNum, Integer pageSize, String orderBy);

    /**
     * cdk批次废除
     * @param groupId
     */
    void abolish(Integer groupId);
}
