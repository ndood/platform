package com.fulu.game.core.service;

import com.fulu.game.core.entity.RegistSource;
import com.fulu.game.core.entity.vo.RegistSourceVO;
import me.chanjar.weixin.common.exception.WxErrorException;

import java.util.List;

/**
 * @author yanbiao
 * @date 2018-06-13 15:33:21
 */
public interface RegistSourceService extends ICommonService<RegistSource, Integer> {

    RegistSource save(String name, String remark) throws WxErrorException;

    List<RegistSource> findByParam(RegistSourceVO rsVO);
}
