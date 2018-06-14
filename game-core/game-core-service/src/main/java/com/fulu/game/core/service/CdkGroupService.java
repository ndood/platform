package com.fulu.game.core.service;

import com.fulu.game.core.entity.CdkGroup;

/**
 * cdk批次表
 *
 * @author yanbiao
 * @date 2018-06-13 15:34:02
 */
public interface CdkGroupService extends ICommonService<CdkGroup, Integer> {

    Boolean generate(CdkGroup cdkGroup);
}
