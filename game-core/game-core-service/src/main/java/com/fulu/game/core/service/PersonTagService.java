package com.fulu.game.core.service;

import com.fulu.game.core.entity.PersonTag;

import java.util.List;
import java.util.Map;

/**
 * 个人标签关联表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 15:50:34
 */
public interface PersonTagService extends ICommonService<PersonTag,Integer>{

    List<PersonTag> findByUserId(Integer userId);

}
