package com.fulu.game.core.service;

import com.fulu.game.core.entity.Tag;

import java.util.List;
import java.util.Map;

/**
 * 标签表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 16:29:26
 */
public interface TagService extends ICommonService<Tag,Integer>{


    Tag create(Integer categoryId, String tagName);

}
