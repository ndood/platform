package com.fulu.game.core.service;

import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.CdkGroup;

import java.util.List;

/**
 * cdk记录表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:53
 */
public interface CdkService extends ICommonService<Cdk, Integer> {

    Cdk findBySeries(String series);

    int insertList(CdkGroup cdkGroup);
}
