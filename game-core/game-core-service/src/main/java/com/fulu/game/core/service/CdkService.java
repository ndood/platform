package com.fulu.game.core.service;

import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.CdkGroup;
import com.fulu.game.core.entity.vo.CdkVO;
import com.github.pagehelper.PageInfo;

/**
 * cdk记录表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:53
 */
public interface CdkService extends ICommonService<Cdk, Integer> {

    Cdk findBySeries(String series);

    int insertList(CdkGroup cdkGroup);

    PageInfo<CdkVO> list(Integer pageNum, Integer pageSize, String series, String orderBy);
}
