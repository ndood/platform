package com.fulu.game.core.service;

import com.fulu.game.core.entity.AdviceFile;

/**
 * @author yanbiao
 * @date 2018-07-02 13:37:16
 */
public interface AdviceFileService extends ICommonService<AdviceFile, Integer> {

    /**
     * 保存建议图片信息
     * @param advicePicUrls
     * @param adviceId
     */
    void save(String[] advicePicUrls, Integer adviceId);
}
