package com.fulu.game.core.service;

import com.fulu.game.core.entity.Advice;
import com.fulu.game.core.entity.vo.AdviceVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户建议表
 *
 * @author yanbiao
 * @date 2018-07-02 11:03:20
 */
public interface AdviceService extends ICommonService<Advice, Integer> {

    /**
     * 小程序端-提建议
     * @param content
     * @param contact
     * @param advicePicUrls
     */
    @Transactional
    void addAdvice(String content, String contact, String[] advicePicUrls);
}
