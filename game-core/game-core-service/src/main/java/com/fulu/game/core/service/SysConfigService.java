package com.fulu.game.core.service;

import com.fulu.game.core.entity.SysConfig;

import java.util.List;

/**
 * 系统配置表
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-05-14 14:54:03
 */
public interface SysConfigService {

    List<SysConfig> findAll();

    List<SysConfig> findByVersion(String version);
}
